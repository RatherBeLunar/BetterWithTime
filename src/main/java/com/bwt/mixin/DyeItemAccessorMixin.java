package com.bwt.mixin;

import net.minecraft.item.DyeItem;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DyeItem.class)
public interface DyeItemAccessorMixin {
    @Accessor
    static Map<DyeColor, DyeItem> getDYES() {
        return null;
    }
}
