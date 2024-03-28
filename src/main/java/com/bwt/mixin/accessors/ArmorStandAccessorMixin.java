package com.bwt.mixin.accessors;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ArmorStandEntity.class)
public interface ArmorStandAccessorMixin {
    @Accessor
    DefaultedList<ItemStack> getHeldItems();

    @Accessor
    DefaultedList<ItemStack> getArmorItems();
}
