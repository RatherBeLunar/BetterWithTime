package com.bwt.blocks.turntable;

import com.bwt.blocks.*;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public interface VerticalBlockAttachmentHelper {
    interface CanPropagatePredicate {
        CanPropagatePredicate FALSE = (world, pos, state) -> false;
        CanPropagatePredicate TRUE = (world, pos, state) -> true;
        CanPropagatePredicate DEFAULT = (world, pos, state) -> state.isFullCube(world, pos);

        boolean test(World world, BlockPos pos, BlockState state);
    }

    HashMap<Class<? extends Block>, CanPropagatePredicate> canPropagatePredicates = new HashMap<>();

    static void register(Class<? extends Block> blockClass, CanPropagatePredicate canPropagatePredicate) {
        canPropagatePredicates.put(blockClass, canPropagatePredicate);
    }

    static boolean canPropagateRotationUpwards(World world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        return canPropagatePredicates.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(block))
                .findAny()
                .map(Map.Entry::getValue)
                .orElse(CanPropagatePredicate.DEFAULT)
                .test(world, pos, state);
    }

    static void registerDefaults() {
        register(AirBlock.class, CanPropagatePredicate.FALSE);
        register(SidingBlock.class, (world, pos, state) -> SidingBlock.isHorizontal(state));
        register(MouldingBlock.class, (world, pos, state) -> MouldingBlock.isVertical(state));
        register(AnchorBlock.class, (world, pos, state) -> AnchorBlock.isHorizontal(state));
        register(SawBlock.class, (world, pos, state) -> SawBlock.isHorizontal(state));
        register(HandCrankBlock.class, CanPropagatePredicate.FALSE);
        register(StairsBlock.class, CanPropagatePredicate.TRUE);
        register(WallBlock.class, CanPropagatePredicate.TRUE);
        register(AnvilBlock.class, CanPropagatePredicate.TRUE);
    }
}
