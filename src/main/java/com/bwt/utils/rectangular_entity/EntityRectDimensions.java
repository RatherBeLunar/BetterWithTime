package com.bwt.utils.rectangular_entity;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.util.math.Box;

public class EntityRectDimensions extends EntityDimensions {
    public final float length;
    public EntityRectDimensions(float width, float height, float length, boolean fixed) {
        super(width, height, fixed);
        this.length = length;
    }
    public static EntityRectDimensions fixed(float width, float height, float length) {
        return new EntityRectDimensions(width, height, length, true);
    }

    @Override
    public Box getBoxAt(double x, double y, double z) {
        double half_width = this.width / 2.0f;
        double half_height = this.length / 2.0f;
        return new Box(x - half_width, y, z - half_height, x + half_width, y + (double)this.height, z + half_height);
    }
}
