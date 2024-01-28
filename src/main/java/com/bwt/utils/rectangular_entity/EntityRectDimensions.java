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

    public Box getBoxAt(float x, float y, float z) {
        float f = this.width / 2.0f;
        float f1 = this.length / 2.0f;
        float g = this.height;
        return new Box(x - (double)f, y, z - (double)f1, x + (double)f, y + (double)g, z + (double)f1);
    }
}
