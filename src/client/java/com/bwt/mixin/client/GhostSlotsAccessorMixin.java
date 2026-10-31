package com.bwt.mixin.client;

import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GhostSlots.class)
public interface GhostSlotsAccessorMixin {
    @Invoker("setResult")
    void bwt$setResult(Slot slot, ContextMap contextMap, SlotDisplay slotDisplay);

    @Invoker("setInput")
    void bwt$setInput(Slot slot, ContextMap contextMap, SlotDisplay slotDisplay);
}
