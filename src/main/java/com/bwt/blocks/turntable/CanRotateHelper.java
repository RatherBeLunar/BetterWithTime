package com.bwt.blocks.turntable;

import net.minecraft.block.*;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.block.enums.ChestType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public interface CanRotateHelper {
    interface CanRotatePredicate {
        CanRotatePredicate FALSE = (world, pos, state) -> false;
        CanRotatePredicate TRUE = (world, pos, state) -> true;

        boolean test(World world, BlockPos pos, BlockState state);
    }

    HashMap<Class<? extends Block>, CanRotatePredicate> canRotatePredicates = new HashMap<>();

    static void register(Class<? extends Block> blockClass, CanRotatePredicate canRotatePredicate) {
        canRotatePredicates.put(blockClass, canRotatePredicate);
    }

    static boolean canRotate(World world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        return canRotatePredicates.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(block))
                .findAny()
                .map(Map.Entry::getValue)
                .orElse(CanRotatePredicate.TRUE)
                .test(world, pos, state);
    }

    static void registerDefaults() {
        register(PistonBlock.class, (world, pos, state) -> !state.get(PistonBlock.EXTENDED));
        register(PistonHeadBlock.class, CanRotatePredicate.FALSE);
        register(PistonExtensionBlock.class, CanRotatePredicate.FALSE);
        register(WallTorchBlock.class, CanRotatePredicate.FALSE);
        register(WallRedstoneTorchBlock.class, CanRotatePredicate.FALSE);
        register(WallSignBlock.class, CanRotatePredicate.FALSE);
        register(DeadCoralWallFanBlock.class, CanRotatePredicate.FALSE);
        register(WallBannerBlock.class, CanRotatePredicate.FALSE);
        register(VineBlock.class, CanRotatePredicate.FALSE);
        register(AmethystClusterBlock.class, CanRotatePredicate.FALSE);
        register(WallHangingSignBlock.class, CanRotatePredicate.FALSE);
        register(WallMountedBlock.class, (world, pos, state) -> state.get(WallMountedBlock.FACE).equals(BlockFace.FLOOR));
        register(BellBlock.class, (world, pos, state) -> state.get(BellBlock.ATTACHMENT).equals(Attachment.FLOOR));
        register(BedBlock.class, CanRotatePredicate.FALSE);
        register(ChestBlock.class, (world, pos, state) -> state.get(ChestBlock.CHEST_TYPE).equals(ChestType.SINGLE));
    }
}
