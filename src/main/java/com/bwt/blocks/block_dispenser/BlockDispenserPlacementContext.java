package com.bwt.blocks.block_dispenser;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockDispenserPlacementContext
        extends ItemPlacementContext {
    private final Direction facing;

    public BlockDispenserPlacementContext(World world, BlockPos pos, Direction facing, ItemStack stack, Direction side) {
        super(world, null, Hand.MAIN_HAND, stack, new BlockHitResult(Vec3d.ofBottomCenter(pos), side, pos, false));
        this.facing = facing;
    }

    @Override
    public BlockPos getBlockPos() {
        return this.getHitResult().getBlockPos();
    }

    @Override
    public boolean canPlace() {
        return this.getWorld().getBlockState(this.getHitResult().getBlockPos()).canReplace(this);
    }

    public boolean canPlace(Direction direction) {
        return this.getWorld().getBlockState(this.getHitResult().getBlockPos())
                .withIfExists(Properties.FACING, direction)
                .withIfExists(Properties.AXIS, direction.getAxis())
                .withIfExists(Properties.ROTATION, ((int) direction.asRotation()))
                .canReplace(this);
    }

    @Override
    public boolean canReplaceExisting() {
        return this.canPlace();
    }

    @Override
    public Direction getPlayerLookDirection() {
        return facing.getOpposite();
    }

    @Override
    public Direction[] getPlacementDirections() {
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
    public Direction getHorizontalPlayerFacing() {
        return this.facing.getAxis() == Direction.Axis.Y ? Direction.NORTH : this.facing;
    }

    @Override
    public boolean shouldCancelInteraction() {
        return false;
    }

    @Override
    public float getPlayerYaw() {
        return this.facing.getHorizontal() * 90;
    }
}