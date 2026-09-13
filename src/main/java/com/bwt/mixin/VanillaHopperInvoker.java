package com.bwt.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HopperBlockEntity.class)
public interface VanillaHopperInvoker {
    @Invoker("isFullContainer")
    static boolean isInventoryFull(Container level, Direction direction) {
        throw new AssertionError();
    }
}
