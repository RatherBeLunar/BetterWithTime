package com.bwt.blocks.block_dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class BlockDispenserPlacementContext
        extends BlockPlaceContext {
    private final Direction facing;

    public BlockDispenserPlacementContext(Level level, BlockPos pos, Direction facing, ItemStack stack, Direction side) {
        super(level, null, InteractionHand.MAIN_HAND, stack, new BlockHitResult(Vec3.atBottomCenterOf(pos), side, pos, false));
        this.facing = facing;
    }

    @Override
    public BlockPos getClickedPos() {
        return this.getHitResult().getBlockPos();
    }

    @Override
    public boolean canPlace() {
        return this.getLevel().getBlockState(this.getHitResult().getBlockPos()).canBeReplaced(this);
    }

    public boolean canPlace(Direction direction) {
        return this.getLevel().getBlockState(this.getHitResult().getBlockPos())
                .trySetValue(BlockStateProperties.FACING, direction)
                .trySetValue(BlockStateProperties.AXIS, direction.getAxis())
                .trySetValue(BlockStateProperties.ROTATION_16, ((int) direction.toYRot()))
                .canBeReplaced(this);
    }

    @Override
    public boolean replacingClickedOnBlock() {
        return this.canPlace();
    }

    @Override
    public Direction getNearestLookingDirection() {
        return facing.getOpposite();
    }

    public Direction getNearestLookingVerticalDirection() {
        return switch (this.facing.getAxis()) {
            case X, Z -> Direction.DOWN;
            case Y -> facing;
        };
    }

    @Override
    public Direction[] getNearestLookingDirections() {
        return switch (this.facing) {
            case DOWN ->
                    new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
            case UP ->
                    new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
            case NORTH ->
                    new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP, Direction.EAST, Direction.WEST};
            case SOUTH ->
                    new Direction[]{Direction.SOUTH, Direction.NORTH, Direction.DOWN, Direction.UP, Direction.EAST, Direction.WEST};
            case WEST ->
                    new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH};
            case EAST ->
                    new Direction[]{Direction.EAST, Direction.WEST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH};
        };
    }

    @Override
    public Direction getHorizontalDirection() {
        return this.facing.getAxis() == Direction.Axis.Y ? Direction.NORTH : this.facing;
    }

    @Override
    public boolean isSecondaryUseActive() {
        return false;
    }

    @Override
    public float getRotation() {
        return this.facing.get2DDataValue() * 90;
    }
}