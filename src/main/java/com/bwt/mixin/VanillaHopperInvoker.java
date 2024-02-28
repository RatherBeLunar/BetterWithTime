package com.bwt.mixin;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HopperBlockEntity.class)
public interface VanillaHopperInvoker {
    @Invoker("isInventoryFull")
    static boolean isInventoryFull(Inventory world, Direction direction) {
        throw new AssertionError();
    }
}
