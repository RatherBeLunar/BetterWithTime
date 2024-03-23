package com.bwt.entities;

import com.bwt.items.BwtItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class WaterWheelEntity extends HorizontalMechPowerSourceEntity {
    public static final float height = 4.8f;
    public static final float width = 4.8f;
    public static final float length = 0.8f;

    public WaterWheelEntity(EntityType<? extends WaterWheelEntity> entityType, World world) {
        super(entityType, world);

    }

    public WaterWheelEntity(World world, Vec3d pos, Direction facing) {
        super(BwtEntities.waterWheelEntity, world, pos, facing);
    }

    @Override
    public boolean tryToSpawn(PlayerEntity player) {
        return super.tryToSpawn(
                player,
                Text.of("Not enough room to place Water Wheel"),
                Text.of("Water Wheel placement is obstructed by something, or by you")
        );
    }

    @Override
    public Predicate<BlockPos> getBlockInterferencePredicate() {
        return blockPos -> !getWorld().getBlockState(blockPos).isAir() && !getWorld().getFluidState(blockPos).isIn(FluidTags.WATER);
    }

    @Override
    float getSpeedToPowerThreshold() {
        return 0.75f;
    }

    @Override
    public float computeRotation() {
//        return Math.min(1, Math.max(getClockwiseRotationForce(), -1));
        return getCounterClockwiseRotationVelocity();
    }

    protected float getCounterClockwiseRotationVelocity() {
        World world = getWorld();
        Vec3i facingVector = getHorizontalFacing().getVector();
        float velocity = BlockPos.stream(getBoundingBox())
            .map(blockPos -> {
               FluidState fluidState = world.getFluidState(blockPos);
               if (!fluidState.isIn(FluidTags.WATER)) {
                   return 0f;
               }
               Vec3d fluidVelocity = fluidState.getVelocity(world, blockPos);
               if (fluidState.contains(FlowableFluid.FALLING) && fluidState.get(FlowableFluid.FALLING)) {
                   fluidVelocity = new Vec3d(fluidVelocity.getX(), -1, fluidVelocity.getZ());
               }
               Vec2f fluidVelocity2d = new Vec2f(
                       (float) (fluidVelocity.getX() * Math.abs(facingVector.getZ()) + fluidVelocity.getZ() * Math.abs(facingVector.getX())),
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
        // Correct for facing directions
        velocity *= (facingVector.getX() - facingVector.getZ());
        // Clamp
        return MathHelper.clamp(velocity, -1, 1);
    }


    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(BwtItems.waterWheelItem);
    }
}
