package com.bwt.utils;

import net.minecraft.block.Block;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

public class BlockUtils {
    public static VoxelShape rotateCuboidFromUp(Direction direction, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        float deg_90 = ((float) Math.PI) / 2f;
        Vec3d half = new Vec3d(8, 8, 8);

        // Shift to center of block for rotation
        Vec3d minPos = new Vec3d(xMin, yMin, zMin).subtract(half);
        Vec3d maxPos = new Vec3d(xMax, yMax, zMax).subtract(half);

        switch (direction) {
            case UP:
                break;
            case DOWN:
                minPos = minPos.rotateZ(2 * deg_90);
                maxPos = maxPos.rotateZ(2 * deg_90);
                break;
            case NORTH:
                minPos = minPos.rotateX(deg_90);
                maxPos = maxPos.rotateX(deg_90);
                break;
            case SOUTH:
                minPos = minPos.rotateX(-deg_90);
                maxPos = maxPos.rotateX(-deg_90);
                break;
            case EAST:
                minPos = minPos.rotateZ(deg_90);
                maxPos = maxPos.rotateZ(deg_90);
                break;
            case WEST:
                minPos = minPos.rotateZ(-deg_90);
                maxPos = maxPos.rotateZ(-deg_90);
                break;
        }

        minPos = minPos.add(half);
        maxPos = maxPos.add(half);
        return Block.createCuboidShape(
                Math.min(minPos.getX(), maxPos.getX()),
                Math.min(minPos.getY(), maxPos.getY()),
                Math.min(minPos.getZ(), maxPos.getZ()),
                Math.max(minPos.getX(), maxPos.getX()),
                Math.max(minPos.getY(), maxPos.getY()),
                Math.max(minPos.getZ(), maxPos.getZ())
        );
    }

    public static VoxelShape rotateCuboidFromUp(Direction direction, Box box) {
        return rotateCuboidFromUp(direction, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    public static VoxelShape rotateCuboid(Direction fromDirection, Direction toDirection, Box box) {
        // Rotate source to up direction via opposite direction
        VoxelShape shape = rotateCuboidFromUp(fromDirection.equals(Direction.DOWN) ? fromDirection : fromDirection.getOpposite(), box);
        // Rotate
        return rotateCuboidFromUp(toDirection, shape.getBoundingBox());
    }
}
