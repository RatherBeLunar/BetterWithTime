package com.bwt.entities;

import com.bwt.items.BwtItems;
import com.bwt.utils.rectangular_entity.EntityRectDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import java.util.function.Predicate;

public class WaterWheelEntity extends HorizontalMechPowerSourceEntity {
    public static final float height = 4.8f;
    public static final float width = 4.8f;
    public static final float length = 0.8f;

    public WaterWheelEntity(EntityType<? extends WaterWheelEntity> entityType, Level level) {
        super(entityType, level);

    }

    public WaterWheelEntity(Level level, Vec3 pos, Direction facing) {
        super(BwtEntities.waterWheelEntity, level, pos, facing);
    }

    @Override
    public EntityRectDimensions getRectDimensions() {
        return EntityRectDimensions.fixed(WaterWheelEntity.width, WaterWheelEntity.height, WaterWheelEntity.length);
    }

    @Override
    public boolean tryToSpawn(Player player) {
        return super.tryToSpawn(
                player,
                Component.nullToEmpty("Not enough room to place Water Wheel"),
                Component.nullToEmpty("Water Wheel placement is obstructed by something, or by you")
        );
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d = 128.0 * getViewScale();
        return distance < d * d;
    }

    @Override
    public Predicate<BlockPos> getBlockInterferencePredicate() {
        return blockPos -> !level().getBlockState(blockPos).is(BlockTags.AIR) && !level().getFluidState(blockPos).is(FluidTags.WATER);
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
        Level level = level();
        Vec3i facingVector = getDirection().getNormal();
        float velocity = BlockPos.betweenClosedStream(getBoundingBox())
            .map(blockPos -> {
               FluidState fluidState = level.getFluidState(blockPos);
               if (!fluidState.is(FluidTags.WATER)) {
                   return 0f;
               }
               Vec3 fluidVelocity = fluidState.getFlow(level, blockPos);
               if (fluidState.hasProperty(FlowingFluid.FALLING) && fluidState.getValue(FlowingFluid.FALLING)) {
                   fluidVelocity = new Vec3(fluidVelocity.x(), -1, fluidVelocity.z());
               }
               Vec2 fluidVelocity2d = new Vec2(
                       (float) (fluidVelocity.x() * Math.abs(facingVector.getZ()) + fluidVelocity.z() * Math.abs(facingVector.getX())),
                       (float) fluidVelocity.y()
               );
               Vec3 centerToPointVector = blockPos.getCenter().subtract(position());
               // This vector isn't to the edge of the whole water wheel, but to the application of force
               // The addition of X and Z here is possible since only one will be non-zero
               Vec2 centerToPointVector2d = new Vec2((float) (centerToPointVector.x() + centerToPointVector.z()), (float) centerToPointVector.y());
               // Tangent vector, clockwise along the circle, normalized to 1 magnitude
               Vec2 tangentUnitVector = new Vec2(centerToPointVector2d.y, -centerToPointVector2d.x).normalized();
               // Component of fluid velocity along clockwise tangent unit vector
               return fluidVelocity2d.dot(tangentUnitVector);
            })
            .reduce(Float::sum)
            .orElse(0f);
        // Correct for facing directions
        velocity *= (facingVector.getX() - facingVector.getZ());
        // Clamp
        return Mth.clamp(velocity, -1, 1);
    }


    @Override
    public ItemStack getPickResult() {
        return new ItemStack(BwtItems.waterWheelItem);
    }
}
