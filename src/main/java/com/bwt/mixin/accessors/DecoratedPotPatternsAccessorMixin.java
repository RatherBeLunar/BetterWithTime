package com.bwt.mixin.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;

@Mixin(DecoratedPotPatterns.class)
public interface DecoratedPotPatternsAccessorMixin {
    @Accessor
    static Map<Item, ResourceKey<DecoratedPotPattern>> getITEM_TO_POT_TEXTURE() {
        throw new UnsupportedOperationException();
    }
}
