package com.bwt.blocks.turntable;

import com.bwt.blocks.*;
import com.bwt.blocks.mining_charge.MiningChargeBlock;
import com.bwt.tags.BwtBlockTags;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface VerticalBlockAttachmentHelper {
    interface CanPropagatePredicate {
        CanPropagatePredicate FALSE = (level, pos, state) -> false;
        CanPropagatePredicate TRUE = (level, pos, state) -> true;
        CanPropagatePredicate DEFAULT = (level, pos, state) -> state.isCollisionShapeFullBlock(level, pos) || state.is(BwtBlockTags.TRANSFERS_ROTATION_UPWARD_OVERRIDE);

        boolean test(Level level, BlockPos pos, BlockState state);
    }

    HashMap<Class<? extends Block>, CanPropagatePredicate> canPropagatePredicates = new HashMap<>();

    static void register(Class<? extends Block> blockClass, CanPropagatePredicate canPropagatePredicate) {
        canPropagatePredicates.put(blockClass, canPropagatePredicate);
    }

    static boolean canPropagateRotationUpwards(Level level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        return canPropagatePredicates.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(block))
                .findAny()
                .map(Map.Entry::getValue)
                .orElse(CanPropagatePredicate.DEFAULT)
                .test(level, pos, state);
    }

    static void registerDefaults() {
        register(AirBlock.class, CanPropagatePredicate.FALSE);
        register(SidingBlock.class, (level, pos, state) -> SidingBlock.isHorizontal(state));
        register(MouldingBlock.class, (level, pos, state) -> MouldingBlock.isVertical(state));
        register(AnchorBlock.class, (level, pos, state) -> AnchorBlock.isHorizontal(state));
        register(SawBlock.class, (level, pos, state) -> SawBlock.isHorizontal(state));
        register(MiningChargeBlock.class, (level, pos, state) -> MiningChargeBlock.isHorizontal(state));
        register(HandCrankBlock.class, CanPropagatePredicate.FALSE);
    }
}
