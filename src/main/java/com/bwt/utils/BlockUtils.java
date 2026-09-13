package com.bwt.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockUtils {
    public static VoxelShape rotateCuboidFromUp(Direction direction, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        float deg_90 = ((float) Math.PI) / 2f;
        Vec3 half = new Vec3(8, 8, 8);

        // Shift to center of block for rotation
        Vec3 minPos = new Vec3(xMin, yMin, zMin).subtract(half);
        Vec3 maxPos = new Vec3(xMax, yMax, zMax).subtract(half);

        switch (direction) {
            case UP:
                break;
            case DOWN:
                minPos = minPos.zRot(2 * deg_90);
                maxPos = maxPos.zRot(2 * deg_90);
                break;
            case NORTH:
                minPos = minPos.xRot(deg_90);
                maxPos = maxPos.xRot(deg_90);
                break;
            case SOUTH:
                minPos = minPos.xRot(-deg_90);
                maxPos = maxPos.xRot(-deg_90);
                break;
            case EAST:
                minPos = minPos.zRot(deg_90);
                maxPos = maxPos.zRot(deg_90);
                break;
            case WEST:
                minPos = minPos.zRot(-deg_90);
                maxPos = maxPos.zRot(-deg_90);
                break;
        }

        minPos = minPos.add(half);
        maxPos = maxPos.add(half);
        return Block.box(
                Math.min(minPos.x(), maxPos.x()),
                Math.min(minPos.y(), maxPos.y()),
                Math.min(minPos.z(), maxPos.z()),
                Math.max(minPos.x(), maxPos.x()),
                Math.max(minPos.y(), maxPos.y()),
                Math.max(minPos.z(), maxPos.z())
        );
    }

    public static VoxelShape rotateCuboidFromUp(Direction direction, AABB box) {
        return rotateCuboidFromUp(direction, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    public static VoxelShape rotateCuboid(Direction fromDirection, Direction toDirection, AABB box) {
        // Rotate source to up direction via opposite direction
        VoxelShape shape = rotateCuboidFromUp(fromDirection.getAxis().isVertical() ? fromDirection : fromDirection.getOpposite(), box);
        // Rotate
        return rotateCuboidFromUp(toDirection, shape.bounds());
    }
}
