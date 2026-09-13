package com.bwt.mixin.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;

@Mixin(DyeItem.class)
public interface DyeItemAccessorMixin {
    @Accessor
    static Map<DyeColor, DyeItem> getITEM_BY_COLOR() {
        return null;
    }
}
