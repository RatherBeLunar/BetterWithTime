package com.bwt.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RadiusAroundBlockStream {
    protected static Vec3i oneDimensionalIndexTo3DCoord(int idx, int xRadius, int yRadius, int zRadius) {
        final int yMax = 2 * yRadius + 1;
        final int zMax = 2 * zRadius + 1;
        int x = idx / (yMax * zMax);
        idx -= (x * yMax * zMax);
        int y = idx / zMax;
        int z = idx % zMax;
        x -= xRadius;
        y -= yRadius;
        z -= zRadius;
        return new Vec3i(x, y, z);
    }

    public static Stream<BlockPos> radiusAroundBlock3D(BlockPos centerPos, int xRadius, int yRadius, int zRadius, boolean includeSelf) {
        final int oneDRange = (2 * xRadius + 1) * (2 * yRadius + 1) * (2 * zRadius + 1);
        Stream<BlockPos> stream = IntStream
                .range(0, oneDRange)
                .mapToObj(idx -> oneDimensionalIndexTo3DCoord(idx, xRadius, yRadius, zRadius))
                .map(centerPos::add);
        if (!includeSelf) {
            stream = stream.filter(pos -> !pos.equals(centerPos));
        }
        return stream;
    }

    public static Stream<BlockPos> radiusAroundBlock2D(BlockPos centerPos, int xRadius, int zRadius, boolean includeSelf) {
        return radiusAroundBlock3D(centerPos, xRadius, 0, zRadius, includeSelf);
    }

    /**
     * Streams all blocks in a 3D box around a center point, with the center point included
     * @param centerPos the BlockPos around which to stream
     * @param radius defines the maximum difference in x, y, and z coordinates from centerPos
     * @return a stream of exactly (2 * radius + 1) ^ 3 BlockPos elements (e.g. radius 1 -> 27 elements)
     */
    public static Stream<BlockPos> allBlocksInRadius(BlockPos centerPos, int radius) {
        return radiusAroundBlock3D(centerPos, radius, radius, radius, true);
    }

    /**
     * Streams all blocks in a 3D box around a center point, excluding the center point itself
     * @param centerPos the BlockPos around which to stream
     * @param radius defines the maximum difference in x, y, and z coordinates from centerPos
     * @return a stream of exactly (2 * radius + 1) ^ 3 - 1 BlockPos elements (e.g. radius 1 -> 26 elements)
     */
    public static Stream<BlockPos> neighboringBlocksInRadius(BlockPos centerPos, int radius) {
        return radiusAroundBlock3D(centerPos, radius, radius, radius, false);
    }

    /**
     * Streams all blocks in a 2D box around a center point, with the center point included
     * @param centerPos the BlockPos around which to stream
     * @param radius defines the maximum difference in x and z coordinates from centerPos
     * @return a stream of exactly (2 * radius + 1) ^ 2 BlockPos elements (e.g. radius 1 -> 9 points)
     */
    public static Stream<BlockPos> allBlocksInHorizontalRadius(BlockPos centerPos, int radius) {
        return radiusAroundBlock2D(centerPos, radius, radius, true);
    }

    /**
     * Streams all blocks in a 2D box around a center point, excluding the center point itself
     * @param centerPos the BlockPos around which to stream
     * @param radius defines the maximum difference in x and z coordinates from centerPos
     * @return a stream of exactly (2 * radius + 1) ^ 2 - 1 BlockPos elements (e.g. radius 1 -> 8 points)
     */
    public static Stream<BlockPos> neighboringBlocksInHorizontalRadius(BlockPos centerPos, int radius) {
        return radiusAroundBlock2D(centerPos, radius, radius, false);
    }

    public static Stream<BlockPos> allNeighbors(BlockPos centerPos) {
        return neighboringBlocksInRadius(centerPos, 1);
    }

    public static Stream<BlockPos> allHorizontalNeighbors(BlockPos centerPos) {
        return neighboringBlocksInHorizontalRadius(centerPos, 1);
    }
}
