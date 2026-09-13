package com.bwt.utils.rectangular_entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public abstract class RectangularEntity extends Entity {
    public RectangularEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void refreshDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.refreshDimensions();
        this.setPos(d, e, f);
    }

    abstract public EntityRectDimensions getRectDimensions();

    @Override
    public void setYRot(float yaw) {
        super.setYRot(yaw);
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    protected AABB makeBoundingBox() {
        EntityRectDimensions dimensions = this.getRectDimensions();
        return dimensions.getBoxAt(position(), getYRot()).move(0, -1 * (dimensions.height() / 2), 0);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling();
    }
}
