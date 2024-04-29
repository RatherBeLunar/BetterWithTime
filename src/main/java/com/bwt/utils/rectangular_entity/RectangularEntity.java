package com.bwt.utils.rectangular_entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class RectangularEntity extends Entity {
    public RectangularEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void calculateDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.calculateDimensions();
        this.setPosition(d, e, f);
    }

    abstract public EntityRectDimensions getRectDimensions();

    @Override
    public void setYaw(float yaw) {
        super.setYaw(yaw);
        this.setBoundingBox(this.calculateBoundingBox());
    }

    @Override
    protected Box calculateBoundingBox() {
        int num90degreeRotations = Math.round(this.getYaw() / 90);
        Vec3d pos = this.getPos();
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        EntityRectDimensions dimensions = this.getRectDimensions();
        float half_width;
        float half_length;
        if (num90degreeRotations % 2 == 0) {
            half_width = dimensions.width() / 2.0f;
            half_length = dimensions.length() / 2.0f;
        }
        else {
            half_length = dimensions.width() / 2.0f;
            half_width = dimensions.length() / 2.0f;
        }
        float half_height = dimensions.height() / 2.0f;
        return new Box(x - half_width, y - half_height, z - half_length, x + half_width, y + half_height, z + half_length);
    }

    @Override
    public Box getVisibilityBoundingBox() {
        return super.getVisibilityBoundingBox();
    }
}
