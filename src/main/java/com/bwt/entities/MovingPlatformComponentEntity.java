package com.bwt.entities;

import com.bwt.utils.rectangular_entity.EntityRectDimensions;
import com.bwt.utils.rectangular_entity.RectangularEntity;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MovingPlatformComponentEntity extends RectangularEntity {
    public final MovingRopeEntity owner;
    public final Vec3i offset;
    public final BlockState cachedState;
    public final BlockEntity blockEntity;
    public final EntityRectDimensions stateDimensions;

    public MovingPlatformComponentEntity(MovingRopeEntity owner, Vec3i offset, BlockState cachedState, BlockEntity blockEntity) {
        super(owner.getType(), owner.level());
        this.owner = owner;
        this.offset = offset;
        this.cachedState = cachedState;
        this.blockEntity = blockEntity;
        setPos(owner.position().add(Vec3.atLowerCornerOf(offset)));
        VoxelShape shape = cachedState.getCollisionShape(owner.level(), owner.blockPosition().offset(offset));
        AABB outlineShape = shape.bounds();
        this.stateDimensions = EntityRectDimensions.fixed((float) (outlineShape.maxX - outlineShape.minX), (float) (outlineShape.maxY - outlineShape.minY), (float) (outlineShape.maxZ - outlineShape.minZ));
        this.refreshDimensions();
    }


    @Override
    protected AABB makeBoundingBox() {
        if (stateDimensions == null) {
            return Shapes.block().bounds().move(position());
        }
        return super.makeBoundingBox();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || this.owner == entity;
    }

    public EntityRectDimensions getRectDimensions() {
        return this.stateDimensions;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}
