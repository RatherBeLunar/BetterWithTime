package com.bwt.mixin;

import com.bwt.gamerules.BwtGameRules;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HopperBlockEntity.class)
public class VanillaHopperMixin {
    @Inject(
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/block/entity/HopperBlockEntity;getOutputInventory(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/inventory/Inventory;"
            ),
            method = "insert",
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private static void hookInsert(World world, BlockPos pos, BlockState state, Inventory inventory, CallbackInfoReturnable<Boolean> cir) {
        if (world.getGameRules().getBoolean(BwtGameRules.VANILLA_HOPPERS_DISABLED)) {
            cir.setReturnValue(null);
        }
    }

    @Inject(
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/block/entity/HopperBlockEntity;getInputInventory(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Lnet/minecraft/inventory/Inventory;"
            ),
            method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z",
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private static void hookExtract(World world, Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
        if (world.getGameRules().getBoolean(BwtGameRules.VANILLA_HOPPERS_DISABLED)) {
            cir.setReturnValue(null);
        }
    }
}
