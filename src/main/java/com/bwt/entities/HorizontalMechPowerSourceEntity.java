package com.bwt.entities;

import com.bwt.blocks.AxleBlock;
import com.bwt.blocks.BwtBlocks;
import com.bwt.utils.rectangular_entity.RectangularEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.function.Predicate;

public abstract class HorizontalMechPowerSourceEntity extends RectangularEntity {
    protected float rotation = 0;
    protected float prevRotation = 0;

    protected int ticksBeforeNextFullUpdate = 20;

    protected static final TrackedData<Float> rotationSpeed = DataTracker.registerData(HorizontalMechPowerSourceEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public HorizontalMechPowerSourceEntity(EntityType<? extends HorizontalMechPowerSourceEntity> type, World world) {
        super(type, world);
        this.intersectionChecked = true;
    }

    public HorizontalMechPowerSourceEntity(EntityType<? extends HorizontalMechPowerSourceEntity> type, World world, Vec3d pos, Direction facing) {
        this(type, world);
        setPosition(pos);
        setYaw(facing.asRotation());
    }

    public interface Factory {
        HorizontalMechPowerSourceEntity create(World world, Vec3d pos, Direction facing);
    }


    abstract public boolean tryToSpawn(PlayerEntity player);
    abstract public Predicate<BlockPos> getBlockInterferencePredicate();
    abstract float computeRotation();
    abstract float getSpeedToPowerThreshold();

    @Override
    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return (dimensions.height / 2) - 1;
    }

    @Override
    protected MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(rotationSpeed, 0f);
    }

    public float getRotation() {
        return rotation;
    }

    protected void setRotation(float rotation) {
        rotation = (rotation + 360f) % 360f;
        this.prevRotation = this.rotation;
        this.rotation = rotation;
    }

    public float getPrevRotation() {
        return prevRotation;
    }

    public float getRotationSpeed() {
        return getDataTracker().get(rotationSpeed);
    }

    public void setRotationSpeed(float speed) {
        getDataTracker().set(rotationSpeed, speed);
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.DESTROY;
    }

    public boolean tryToSpawn(PlayerEntity player, Text blockBlockedErrorMessage, Text entityBlockedErrorMessage) {
        if (player instanceof ServerPlayerEntity) {
            player = null;
        }

        if (placementBlockedByBlock()) {
            if(player != null) {
                player.sendMessage(blockBlockedErrorMessage);
            }
            return false;
        }
        if (placementBlockedByEntity()) {
            if(player != null) {
                player.sendMessage(entityBlockedErrorMessage);
            }
            return false;
        }

        if (placementHasBadAxleState()) {
            return false;
        }

        setRotationSpeed(computeRotation());
        World world = getWorld();
        world.spawnEntity(this);
        return true;
    }

    public boolean placementBlockedByBlock() {
        Predicate<BlockPos> blockInterferencePredicate = getBlockInterferencePredicate();
        return BlockPos.stream(getBoundingBox())
                // Ignore the axle we're on
                .filter(blockPos -> !blockPos.equals(this.getBlockPos()))
                .anyMatch(blockInterferencePredicate);
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        destroyWithDrop();
    }

    public boolean placementBlockedByEntity() {
        ArrayList<Entity> anyEntities = new ArrayList<>();
        getWorld().collectEntitiesByType(
                TypeFilter.instanceOf(Entity.class),
                getBoundingBox(),
                entity -> entity != this && EntityPredicates.EXCEPT_SPECTATOR.test(entity) && !(entity instanceof ItemEntity),
                anyEntities, 1);
        return !anyEntities.isEmpty();
    }

    public boolean placementHasBadAxleState() {
        World world = getWorld();

        BlockState axleBlock = world.getBlockState(getBlockPos());

        // Bad block type
        if (!axleBlock.isOf(BwtBlocks.axleBlock) && !axleBlock.isOf(BwtBlocks.axlePowerSourceBlock)) {
            return true;
        }
        Direction.Axis axleAxis = axleBlock.get(AxleBlock.AXIS);
        float yaw = getYaw();

        // Misaligned
        return Direction.from(axleAxis, Direction.AxisDirection.NEGATIVE).asRotation() != yaw
                && Direction.from(axleAxis, Direction.AxisDirection.POSITIVE).asRotation() != yaw;
    }

    @Override
    public void tick() {
        super.tick();

        if (isRemoved()) {
            return;
        }

        if (getWorld().isClient) {
            updateRotation();
        }
        else {
            ticksBeforeNextFullUpdate--;
            if (ticksBeforeNextFullUpdate <= 0) {
                ticksBeforeNextFullUpdate = 20;
                fullUpdate();
            }
        }
    }

    protected void updateRotation() {
        setRotation(rotation + this.getDataTracker().get(rotationSpeed));
    }

    protected void fullUpdate() {
        if (placementBlockedByBlock() || placementBlockedByEntity() || placementHasBadAxleState()) {
            destroyWithDrop();
            return;
        }

        setRotationSpeed(computeRotation());

        setHostAxlePower(Math.abs(getRotationSpeed()) > getSpeedToPowerThreshold());
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.getWorld().isClient || this.isRemoved()) {
            return true;
        }
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).getAbilities().creativeMode;
        if (!bl /* && this.getDamageWobbleStrength() > 40.0f || this.shouldAlwaysKill(source) */) {
            destroyWithDrop();
        } else /* if (bl) */{
            discard();
        }
        return true;
    }

    public void destroyWithDrop() {
        if (isRemoved()) return;
        dropStack(getPickBlockStack(), 0.5f);
        kill();
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        setHostAxlePower(false);
    }

    protected void setHostAxlePower(boolean powered) {
        World world = getWorld();
        BlockPos pos = getBlockPos();
        BlockState hostBlockState = world.getBlockState(pos);
        if (!powered && hostBlockState.isOf(BwtBlocks.axlePowerSourceBlock)) {
            world.setBlockState(pos, BwtBlocks.axleBlock.getDefaultState()
                    .with(AxleBlock.AXIS, hostBlockState.get(AxleBlock.AXIS)));
        }
        if (powered && hostBlockState.isOf(BwtBlocks.axleBlock)) {
            world.setBlockState(pos, BwtBlocks.axlePowerSourceBlock.getDefaultState()
                    .with(AxleBlock.AXIS, hostBlockState.get(AxleBlock.AXIS)));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("rotationSpeed", dataTracker.get(rotationSpeed));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(rotationSpeed, nbt.getFloat("rotationSpeed"));
    }
}
