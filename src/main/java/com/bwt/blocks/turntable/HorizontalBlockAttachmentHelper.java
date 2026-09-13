package com.bwt.blocks.turntable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public interface HorizontalBlockAttachmentHelper {
    interface IsAttachedPredicate {
        IsAttachedPredicate FALSE = (attachedToPos, attachedToState, thisPos, thisState) -> false;
        boolean test(BlockPos attachedToPos, BlockState attachedToState, BlockPos thisPos, BlockState thisState);

        static IsAttachedPredicate directional(DirectionalIsAttachedPredicate directionalIsAttachedPredicate) {
            return (attachedToPos, attachedToState, thisPos, thisState) -> {
                Vec3i directionVector = thisPos.subtract(attachedToPos);
                Direction direction = Direction.fromDelta(directionVector.getX(), 0, directionVector.getZ());
                return directionalIsAttachedPredicate.test(attachedToState, thisState, direction);
            };
        }
    }

    interface DirectionalIsAttachedPredicate {
        boolean test(BlockState attachedToState, BlockState thisState, Direction directionBetween);
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
        IsAttachedPredicate facingBlockPredicate = IsAttachedPredicate.directional((attachedToState, thisState, direction) -> direction == thisState.getValue(DirectionalBlock.FACING));
        IsAttachedPredicate horizontalFacingBlockPredicate = IsAttachedPredicate.directional((attachedToState, thisState, direction) -> direction == thisState.getValue(HorizontalDirectionalBlock.FACING));
        IsAttachedPredicate wallHangingSignPredicate = IsAttachedPredicate.directional((attachedToState, thisState, direction) -> direction.getClockWise().getAxis() == thisState.getValue(HorizontalDirectionalBlock.FACING).getAxis());
        IsAttachedPredicate wallMountedBlockPredicate = IsAttachedPredicate.directional((attachedToState, thisState, direction) -> {
            if (!thisState.getValue(FaceAttachedHorizontalDirectionalBlock.FACE).equals(AttachFace.WALL)) {
                return false;
            }
            return direction == thisState.getValue(FaceAttachedHorizontalDirectionalBlock.FACING);
        });
        IsAttachedPredicate connectingBlockPredicate = IsAttachedPredicate.directional((attachedToState, thisState, direction) -> thisState.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(Objects.requireNonNull(direction).getOpposite())));
        IsAttachedPredicate bellPredicate = IsAttachedPredicate.directional((attachedToState, thisState, direction) -> switch (thisState.getValue(BellBlock.ATTACHMENT)) {
            case FLOOR, CEILING -> false;
            case SINGLE_WALL, DOUBLE_WALL -> thisState.getValue(BellBlock.FACING).getOpposite().equals(direction);
        });

        register(WallTorchBlock.class, horizontalFacingBlockPredicate);
        register(RedstoneWallTorchBlock.class, horizontalFacingBlockPredicate);
        register(WallSignBlock.class, horizontalFacingBlockPredicate);
        register(BaseCoralWallFanBlock.class, horizontalFacingBlockPredicate);
        register(WallBannerBlock.class, horizontalFacingBlockPredicate);
        register(TripWireHookBlock.class, horizontalFacingBlockPredicate);
        register(LadderBlock.class, horizontalFacingBlockPredicate);
        register(GlowLichenBlock.class, connectingBlockPredicate);
        register(AmethystClusterBlock.class, facingBlockPredicate);
        register(BellBlock.class, bellPredicate);
        register(WallHangingSignBlock.class, wallHangingSignPredicate);
        register(FaceAttachedHorizontalDirectionalBlock.class, wallMountedBlockPredicate);
    }
}
