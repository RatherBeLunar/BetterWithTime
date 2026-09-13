package com.bwt.entities;

import com.bwt.blocks.axles.AxleBlock;
import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.GearBoxBlock;
import com.bwt.items.BwtItems;
import com.bwt.utils.rectangular_entity.EntityRectDimensions;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WindmillEntity extends HorizontalMechPowerSourceEntity {
    public static final int NUM_SAILS = 4;

    public static final float height = 12.8f;
    public static final float width = 12.8f;
    public static final float length = 0.8f;

    private static final float rotationPerTick = -0.12F;
    private static final float rotationPerTickInStorm = -2.0F;
    private static final float rotationPerTickInNether = -0.07F;
    private static final int secondsInStormBeforeOverpower = 30;

    protected static final EntityDataAccessor<Integer> dyeIndex = SynchedEntityData.defineId(WindmillEntity.class, EntityDataSerializers.INT);
    protected static final List<EntityDataAccessor<Integer>> sailColors = IntStream.range(0, NUM_SAILS).mapToObj(i ->
            SynchedEntityData.defineId(WindmillEntity.class, EntityDataSerializers.INT)).collect(Collectors.toList());
    protected int overpowerTimer = 0;

    public WindmillEntity(EntityType<? extends WindmillEntity> entityType, Level level) {
        super(entityType, level);
    }

    public WindmillEntity(Level level, Vec3 pos, Direction facing) {
        super(BwtEntities.windmillEntity, level, pos, facing);
    }

    @Override
    public EntityRectDimensions getRectDimensions() {
        return EntityRectDimensions.fixed(WindmillEntity.width, WindmillEntity.height, WindmillEntity.length);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(dyeIndex, 0);
        sailColors.forEach(bladeColor -> builder.define(bladeColor, DyeColor.WHITE.getId()));
    }

    public DyeColor getSailColor(int index) {
        return DyeColor.byId(entityData.get(sailColors.get(index)));
    }

    public void setSailColor(int index, DyeColor dyeColor) {
        entityData.set(sailColors.get(index), dyeColor.getId());
    }

    @Override
    public boolean tryToSpawn(Player player) {
        return super.tryToSpawn(
                player,
                Component.nullToEmpty("Not enough room to place Wind Mill (They are friggin HUGE!)"),
                Component.nullToEmpty("Wind Mill placement is obstructed by something, or by you")
        );
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d = 128.0 * getViewScale();
        return distance < d * d;
    }

    @Override
    public Predicate<BlockPos> getBlockInterferencePredicate() {
        return blockPos -> !level().getBlockState(blockPos).is(BlockTags.AIR);
    }

    @Override
    float getSpeedToPowerThreshold() {
        return 0.01f;
    }

    @Override
    public float computeRotation() {
        Level level = level();
        // Nether
        if (level.dimensionType().ultraWarm()) {
            return rotationPerTickInNether;
        }
        // End dimension or modded
        else if (!level.dimensionType().natural()) {
            return 0.0f;
        }
        // Overworld, sky blocked
        else if (!level.canSeeSky(blockPosition())) {
            return 0.0f;
        }
        // Overworld, storming
        else if (level.isRaining() && level.isThundering()) {
            return rotationPerTickInStorm;
        }
        // Overworld, not raining
        return rotationPerTick;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack stackInHand = player.getItemInHand(hand);
        if (!(stackInHand.getItem() instanceof DyeItem dyeItem)) {
            return super.interact(player, hand);
        }

        DyeColor dyeColor = dyeItem.getDyeColor();
        int dyeIdx = getEntityData().get(dyeIndex);
        if (dyeColor.equals(getSailColor(dyeIdx))) {
            getEntityData().set(dyeIndex, (dyeIdx + 1) % NUM_SAILS);
            return InteractionResult.SUCCESS;
        }
        this.setSailColor(dyeIdx, dyeColor);
        getEntityData().set(dyeIndex, (dyeIdx + 1) % NUM_SAILS);
        if (!player.getAbilities().instabuild) {
            stackInHand.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void fullUpdate() {
        super.fullUpdate();
        if (Math.abs(getRotationSpeed()) < Math.abs(rotationPerTickInStorm)) {
            overpowerTimer = 0;
            return;
        }
        overpowerTimer++;
        if (overpowerTimer >= secondsInStormBeforeOverpower) {
            breakConnectedGearBoxes();
        }
    }

    protected void breakConnectedGearBoxes() {
        BlockPos hostAxlePos = blockPosition();
        BlockState hostAxleState = level().getBlockState(hostAxlePos);

        // Bad block type
        if (!hostAxleState.is(BwtBlocks.axleBlock) && !hostAxleState.is(BwtBlocks.axlePowerSourceBlock)) {
            return;
        }
        Direction.Axis hostAxleAxis = hostAxleState.getValue(AxleBlock.AXIS);
        for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
            for (int i = 1; i <= 4; i++) {
                Direction direction = Direction.fromAxisAndDirection(hostAxleAxis, axisDirection);
                BlockPos connectedPos = hostAxlePos.relative(direction, i);
                BlockState connectedState = level().getBlockState(connectedPos);
                if (
                        connectedState.getBlock() instanceof GearBoxBlock gearBoxBlock
                        // Receiving power from this axle
                        && connectedState.getValue(GearBoxBlock.FACING).equals(direction.getOpposite())
                        // Not switched off
                        && gearBoxBlock.isMechPowered(connectedState)
                ) {
                    GearBoxBlock.breakGearBox(level(), connectedPos);
                    break;
                }
                if (!connectedState.is(BwtBlocks.axleBlock) && !connectedState.is(BwtBlocks.axlePowerSourceBlock)) {
                    break;
                }
                if (!connectedState.getValue(AxleBlock.AXIS).equals(hostAxleAxis)) {
                    break;
                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("dyeIndex", entityData.get(dyeIndex));
        for (int i = 0; i < NUM_SAILS; i++) {
            nbt.putInt("sail" + i + "Color", entityData.get(sailColors.get(i)));
        }
        nbt.putInt("overpowerTimer", overpowerTimer);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        entityData.set(dyeIndex, nbt.getInt("dyeIndex"));
        for (int i = 0; i < NUM_SAILS; i++) {
            if (nbt.contains("sail" + i + "Color", CompoundTag.TAG_INT)) {
                entityData.set(sailColors.get(i), nbt.getInt("sail" + i + "Color"));
            }
        }
        overpowerTimer = nbt.getInt("overpowerTimer");
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(BwtItems.windmillItem);
    }
}
