package com.bwt.mechanical.impl;

import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DirectionTools {

    private static final Map<Direction.Axis, List<Direction>> axisToDirections = Map.of(
        Direction.Axis.X, List.of(Direction.EAST, Direction.WEST),
        Direction.Axis.Y, List.of(Direction.UP, Direction.DOWN),
        Direction.Axis.Z, List.of(Direction.NORTH, Direction.SOUTH)
    );
    public static final List<Direction> DIRECTIONS = List.of(Direction.values());

    public static List<Direction> fromAxis(Direction.Axis axis) {
        return axisToDirections.get(axis);
    }

    public static List<Direction> filter(Predicate<Direction> predicate) {
        return Stream.of(Direction.values()).filter(predicate).toList();
    }

    public static List<Direction> all() {
        return DIRECTIONS;
    }
}
