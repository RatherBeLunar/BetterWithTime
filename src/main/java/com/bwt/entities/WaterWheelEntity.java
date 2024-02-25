package com.bwt.entities;

import com.bwt.blocks.AxleBlock;
import com.bwt.blocks.AxlePowerSourceBlock;
import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import com.bwt.utils.rectangular_entity.RectangularEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.ArrayList;

public class WaterWheelEntity extends RectangularEntity {
    public static final float height = 4.8f;
    public static final float width = 4.8f;
    public static final float length = 0.8f;

    private int ticksBeforeNextFullUpdate = 20;
    private float rotation = 0;
    private float prevRotation = 0;

    private static final TrackedData<Float> rotationSpeed = DataTracker.registerData(WaterWheelEntity.class, TrackedDataHandlerRegistry.FLOAT);


    public WaterWheelEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
        this.intersectionChecked = true;
    }

    public WaterWheelEntity(World world, Vec3d pos, Direction facing) {
        this(BwtEntities.waterWheelEntity, world);
        setPosition(pos);
        setYaw(facing.asRotation());
    }

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
        setCustomName(Text.of(Float.valueOf(speed).toString()));
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

    public boolean tryToSpawn() {
        if (placementBlockedByBlock()) {
            if(MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(Text.of("Not enough room to place Water Wheel"));
            }
            return false;
        }
        if (placementBlockedByEntity()) {
            if(MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(Text.of("Water Wheel placement is obstructed by something, or by you"));
            }
            return false;
        }
        // This must be the last condition checked,
        // because it has a side effect of converting the axle to powered
        if (placementHasBadAxleState()) {
            return false;
        }

        setRotationSpeed(computeRotation());
        World world = getWorld();
        world.spawnEntity(this);
        return true;
    }

    public boolean placementBlockedByBlock() {
        return BlockPos.stream(getBoundingBox())
                // Ignore the axle we're on
                .filter(blockPos -> !blockPos.equals(this.getBlockPos()))
                .anyMatch(blockPos -> !getWorld().getBlockState(blockPos).isAir() && !getWorld().getFluidState(blockPos).isIn(FluidTags.WATER));
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        destroyWithDrop();
    }

    public boolean placementBlockedByEntity() {
        ArrayList<Entity> anyEntities = new ArrayList<>();
        getWorld().collectEntitiesByType(TypeFilter.instanceOf(Entity.class), getBoundingBox(), entity -> entity != this, anyEntities, 1);
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
        if (Direction.from(axleAxis, Direction.AxisDirection.NEGATIVE).asRotation() != yaw
            && Direction.from(axleAxis, Direction.AxisDirection.POSITIVE).asRotation() != yaw
        ) {
            return true;
        }

        // Not a fail condition, but a state correction to power the axle
        // Especially important on placement
        if (!axleBlock.isOf(BwtBlocks.axlePowerSourceBlock)) {
            world.setBlockState(getBlockPos(), BwtBlocks.axlePowerSourceBlock.getDefaultState().with(AxlePowerSourceBlock.AXIS, axleAxis));
        }

        return false;
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

    public float computeRotation() {
        return Math.min(1, Math.max(getClockwiseRotationForce(), -1));
    }

    protected float getClockwiseRotationForce() {
        World world = getWorld();
        return BlockPos.stream(getBoundingBox())
            .map(blockPos -> {
               FluidState fluidState = world.getFluidState(blockPos);
               if (!fluidState.isIn(FluidTags.WATER)) {
                   return 0f;
               }
               Vec3d fluidVelocity = fluidState.getVelocity(world, blockPos);
               Vec3i facingVector = getHorizontalFacing().rotateYClockwise().getVector();
               Vec2f fluidVelocity2d = new Vec2f(
                       (float) (fluidVelocity.getX() * facingVector.getX() + fluidVelocity.getZ() * facingVector.getZ()),
                       (float) fluidVelocity.getY()
               );
               Vec3d centerToPointVector = blockPos.toCenterPos().subtract(getPos());
               // This vector isn't to the edge of the whole water wheel, but to the application of force
               // The addition of X and Z here is possible since only one will be non-zero
               Vec2f centerToPointVector2d = new Vec2f((float) (centerToPointVector.getX() + centerToPointVector.getZ()), (float) centerToPointVector.getY());
               // Tangent vector, clockwise along the circle, normalized to 1 magnitude
               Vec2f tangentUnitVector = new Vec2f(centerToPointVector2d.y, -centerToPointVector2d.x).normalize();
               // Component of fluid velocity along clockwise tangent unit vector
               return fluidVelocity2d.dot(tangentUnitVector);
            })
            .reduce(Float::sum)
            .orElse(0f);
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
        float currentSpeed = getRotationSpeed();

        boolean powered = currentSpeed > 0.01f || currentSpeed < -0.01f;

        setHostAxlePower(powered);
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
        } else /* if (pushedByFluids) */{
            discard();
        }
        return true;
    }

    public void destroyWithDrop() {
        if (isRemoved()) return;
        dropStack(BwtItems.waterWheelItem.getDefaultStack(), 0.5f);
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
    public ItemStack getPickBlockStack() {
        return new ItemStack(BwtItems.waterWheelItem);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }
}
