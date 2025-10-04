package com.bwt.mixin.accessors;

import net.minecraft.block.DecoratedPotPattern;
import net.minecraft.block.DecoratedPotPatterns;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DecoratedPotPatterns.class)
public interface DecoratedPotPatternsAccessorMixin {
    @Accessor
    static Map<Item, RegistryKey<DecoratedPotPattern>> getSHERD_TO_PATTERN() {
        throw new UnsupportedOperationException();
    }
}
