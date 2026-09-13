package com.bwt.mixin.accessors;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TransientCraftingContainer.class)
public interface CraftingInventoryAccessorMixin {
    @Mutable
    @Accessor("items")
    void setInventory(NonNullList<ItemStack> inventory);
}
