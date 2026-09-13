package com.bwt.mixin.accessors;

import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BoatItem.class)
public interface BoatItemAccessorMixin {
    @Accessor
    Boat.Type getType();

    @Accessor
    boolean getHasChest();
}
