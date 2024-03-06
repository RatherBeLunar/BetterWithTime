package com.bwt.entities;

import com.bwt.items.BwtItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WindmillEntity extends HorizontalMechPowerSourceEntity {
    public static final int NUM_SAILS = 4;

    public static final float height = 12.8f;
    public static final float width = 12.8f;
    public static final float length = 0.8f;

    private static final float rotationPerTick = -0.12F;
    private static final float rotationPerTickInStorm = -2.0F;
    private static final float rotationPerTickInNether = -0.07F;

    protected static final TrackedData<Integer> dyeIndex = DataTracker.registerData(WindmillEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final List<TrackedData<Integer>> sailColors = IntStream.range(0, NUM_SAILS).mapToObj(i ->
            DataTracker.registerData(WindmillEntity.class, TrackedDataHandlerRegistry.INTEGER)).collect(Collectors.toList());

    public WindmillEntity(EntityType<? extends WindmillEntity> entityType, World world) {
        super(entityType, world);
    }

    public WindmillEntity(World world, Vec3d pos, Direction facing) {
        super(BwtEntities.windmillEntity, world, pos, facing);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(dyeIndex, 0);
        sailColors.forEach(bladeColor -> dataTracker.startTracking(bladeColor, DyeColor.WHITE.getId()));
    }

    public DyeColor getSailColor(int index) {
        return DyeColor.byId(dataTracker.get(sailColors.get(index)));
    }

    public void setSailColor(int index, DyeColor dyeColor) {
        dataTracker.set(sailColors.get(index), dyeColor.getId());
    }

    @Override
    public boolean tryToSpawn(PlayerEntity player) {
        return super.tryToSpawn(
                player,
                Text.of("Not enough room to place Wind Mill (They are friggin HUGE!)"),
                Text.of("Wind Mill placement is obstructed by something, or by you")
        );
    }

    @Override
    public Predicate<BlockPos> getBlockInterferencePredicate() {
        return blockPos -> !getWorld().getBlockState(blockPos).isAir();
    }

    @Override
    float getSpeedToPowerThreshold() {
        return 0.01f;
    }

    @Override
    public float computeRotation() {
        World world = getWorld();
        // Nether
        if (world.getDimension().ultrawarm()) {
            return rotationPerTickInNether;
        }
        // End dimension or modded
        else if (!world.getDimension().natural()) {
            return 0.0f;
        }
        // Overworld, sky blocked
        else if (!world.isSkyVisible(getBlockPos())) {
            return 0.0f;
        }
        // Overworld, raining
        else if (world.isRaining()) {
            return rotationPerTickInStorm;
        }
        // Overworld, not raining
        return rotationPerTick;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack stackInHand = player.getStackInHand(hand);
        if (!(stackInHand.getItem() instanceof DyeItem dyeItem)) {
            return super.interact(player, hand);
        }

        DyeColor dyeColor = dyeItem.getColor();
        int dyeIdx = getDataTracker().get(dyeIndex);
        if (dyeColor.equals(getSailColor(dyeIdx))) {
            getDataTracker().set(dyeIndex, (dyeIdx + 1) % NUM_SAILS);
            return ActionResult.SUCCESS;
        }
        this.setSailColor(dyeIdx, dyeColor);
        getDataTracker().set(dyeIndex, (dyeIdx + 1) % NUM_SAILS);
        if (!player.getAbilities().creativeMode) {
            stackInHand.decrement(1);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("dyeIndex", dataTracker.get(dyeIndex));
        for (int i = 0; i < NUM_SAILS; i++) {
            nbt.putInt("sail" + i + "Color", dataTracker.get(sailColors.get(i)));
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        dataTracker.set(dyeIndex, nbt.getInt("dyeIndex"));
        for (int i = 0; i < NUM_SAILS; i++) {
            if (nbt.contains("sail" + i + "Color", NbtCompound.INT_TYPE)) {
                dataTracker.set(sailColors.get(i), nbt.getInt("sail" + i + "Color"));
            }
        }
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(BwtItems.windmillItem);
    }
}
