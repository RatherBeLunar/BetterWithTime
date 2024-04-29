package com.bwt.utils.rectangular_entity;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public record EntityRectDimensions(float width, float length, float height, float eyeHeight) {
    public static EntityRectDimensions fixed(float width, float length, float height) {
        return new EntityRectDimensions(width, height, length, height / 2f);
    }

    public Box getBoxAt(Vec3d pos, float yaw) {
        return getBoxAt(pos.x, pos.y, pos.z, yaw);
    }

    public Box getBoxAt(double x, double y, double z, float yaw) {
        int num90degreeRotations = Math.round(yaw / 90);
        float half_width;
        float half_length;
        if (num90degreeRotations % 2 == 0) {
            half_width = width / 2.0f;
            half_length = length / 2.0f;
        }
        else {
            half_length = width / 2.0f;
            half_width = length / 2.0f;
        }
        return new Box(
                x - half_width,
                y,
                z - half_length,
                x + half_width,
                y + height,
                z + half_length
        );
    }
}
