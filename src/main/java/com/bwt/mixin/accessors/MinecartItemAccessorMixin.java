package com.bwt.mixin.accessors;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.MinecartItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecartItem.class)
public interface MinecartItemAccessorMixin {
    @Accessor
    AbstractMinecartEntity.Type getType();
}
