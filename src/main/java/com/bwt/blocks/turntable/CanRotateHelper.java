package com.bwt.blocks.turntable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.ChestType;
import java.util.HashMap;
import java.util.Map;

public interface CanRotateHelper {
    interface CanRotatePredicate {
        CanRotatePredicate FALSE = (level, pos, state) -> false;
        CanRotatePredicate TRUE = (level, pos, state) -> true;

        boolean test(Level level, BlockPos pos, BlockState state);
    }

    HashMap<Class<? extends Block>, CanRotatePredicate> canRotateClassPredicates = new HashMap<>();
    HashMap<Block, CanRotatePredicate> canRotateBlockPredicates = new HashMap<>();

    static void register(Class<? extends Block> blockClass, CanRotatePredicate canRotatePredicate) {
        canRotateClassPredicates.put(blockClass, canRotatePredicate);
    }

    static void register(Block block, CanRotatePredicate canRotatePredicate) {
        canRotateBlockPredicates.put(block, canRotatePredicate);
    }

    static boolean canRotate(Level level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        return canRotateClassPredicates.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(block))
                .findAny()
                .map(Map.Entry::getValue)
                .orElse(CanRotatePredicate.TRUE)
                .test(level, pos, state);
    }

    static void registerDefaults() {
        register(PistonBaseBlock.class, (level, pos, state) -> !state.getValue(PistonBaseBlock.EXTENDED));
        register(PistonHeadBlock.class, CanRotatePredicate.FALSE);
        register(MovingPistonBlock.class, CanRotatePredicate.FALSE);
        register(WallTorchBlock.class, CanRotatePredicate.FALSE);
        register(RedstoneWallTorchBlock.class, CanRotatePredicate.FALSE);
        register(WallSignBlock.class, CanRotatePredicate.FALSE);
        register(BaseCoralWallFanBlock.class, CanRotatePredicate.FALSE);
        register(WallBannerBlock.class, CanRotatePredicate.FALSE);
        register(VineBlock.class, CanRotatePredicate.FALSE);
        register(AmethystClusterBlock.class, CanRotatePredicate.FALSE);
        register(WallHangingSignBlock.class, CanRotatePredicate.FALSE);
        register(FaceAttachedHorizontalDirectionalBlock.class, (level, pos, state) -> state.getValue(FaceAttachedHorizontalDirectionalBlock.FACE).equals(AttachFace.FLOOR));
        register(BellBlock.class, (level, pos, state) -> state.getValue(BellBlock.ATTACHMENT).equals(BellAttachType.FLOOR));
        register(BedBlock.class, CanRotatePredicate.FALSE);
        register(ChestBlock.class, (level, pos, state) -> state.getValue(ChestBlock.TYPE).equals(ChestType.SINGLE));
        register(Blocks.OBSIDIAN, CanRotatePredicate.FALSE);
        register(Blocks.CRYING_OBSIDIAN, CanRotatePredicate.FALSE);
        register(Blocks.BEDROCK, CanRotatePredicate.FALSE);
    }
}
