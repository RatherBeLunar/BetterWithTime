package com.bwt.mixin.accessors;

import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.MinecartItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecartItem.class)
public interface MinecartItemAccessorMixin {
    @Accessor
    AbstractMinecart.Type getType();
}
