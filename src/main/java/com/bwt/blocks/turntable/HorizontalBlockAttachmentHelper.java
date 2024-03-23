package com.bwt.blocks.turntable;

import net.minecraft.block.*;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public interface HorizontalBlockAttachmentHelper {
    interface IsAttachedPredicate {
        IsAttachedPredicate FALSE = (attachedToPos, attachedToState, thisPos, thisState) -> false;
        boolean test(BlockPos attachedToPos, BlockState attachedToState, BlockPos thisPos, BlockState thisState);
    }

    HashMap<Class<? extends Block>, IsAttachedPredicate> isAttachedPredicates = new HashMap<>();

    static void register(Class<? extends Block> blockClass, IsAttachedPredicate isAttachedPredicate) {
        isAttachedPredicates.put(blockClass, isAttachedPredicate);
    }

    static boolean isAttached(BlockPos attachedToPos, BlockState attachedToState, BlockPos thisPos, BlockState thisState) {
        Block block = thisState.getBlock();
        return isAttachedPredicates.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(block))
                .findAny()
                .map(Map.Entry::getValue)
                .orElse(IsAttachedPredicate.FALSE)
                .test(attachedToPos, attachedToState, thisPos, thisState);
    }

    static void registerDefaults() {
        IsAttachedPredicate facingBlockPredicate = (attachedToPos, attachedToState, thisPos, thisState) -> {
            Vec3i directionVector = thisPos.subtract(attachedToPos);
            Direction direction = Direction.fromVector(directionVector.getX(), 0, directionVector.getZ());
            return direction == thisState.get(FacingBlock.FACING);
        };
        IsAttachedPredicate horizontalFacingBlockPredicate = (attachedToPos, attachedToState, thisPos, thisState) -> {
            Vec3i directionVector = thisPos.subtract(attachedToPos);
            Direction direction = Direction.fromVector(directionVector.getX(), 0, directionVector.getZ());
            return direction == thisState.get(HorizontalFacingBlock.FACING);
        };
        IsAttachedPredicate wallHangingSignPredicate = (attachedToPos, attachedToState, thisPos, thisState) -> {
            Vec3i directionVector = thisPos.subtract(attachedToPos);
            Direction direction = Direction.fromVector(directionVector.getX(), 0, directionVector.getZ());
            return Objects.requireNonNull(direction).rotateYClockwise().getAxis() == thisState.get(HorizontalFacingBlock.FACING).getAxis();
        };
        IsAttachedPredicate wallMountedBlockPredicate = (attachedToPos, attachedToState, thisPos, thisState) -> {
            if (!thisState.get(WallMountedBlock.FACE).equals(BlockFace.WALL)) {
                return false;
            }
            Vec3i directionVector = thisPos.subtract(attachedToPos);
            Direction direction = Direction.fromVector(directionVector.getX(), 0, directionVector.getZ());
            return direction == thisState.get(WallMountedBlock.FACING);
        };
        IsAttachedPredicate connectingBlockPredicate = (attachedToPos, attachedToState, thisPos, thisState) -> {
            Vec3i directionVector = thisPos.subtract(attachedToPos);
            Direction direction = Direction.fromVector(directionVector.getX(), 0, directionVector.getZ());
            return thisState.get(ConnectingBlock.FACING_PROPERTIES.get(direction));
        };

        register(WallTorchBlock.class, horizontalFacingBlockPredicate);
        register(WallRedstoneTorchBlock.class, horizontalFacingBlockPredicate);
        register(WallSignBlock.class, horizontalFacingBlockPredicate);
        register(DeadCoralWallFanBlock.class, horizontalFacingBlockPredicate);
        register(WallBannerBlock.class, horizontalFacingBlockPredicate);
        register(TripwireHookBlock.class, horizontalFacingBlockPredicate);
        register(VineBlock.class, connectingBlockPredicate);
        register(AmethystClusterBlock.class, facingBlockPredicate);
        register(WallHangingSignBlock.class, wallHangingSignPredicate);
        register(WallMountedBlock.class, wallMountedBlockPredicate);
    }
}
