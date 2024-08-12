package com.bwt.items.components;

import net.fabricmc.api.ModInitializer;
import net.minecraft.component.DataComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class BwtDataComponents  implements ModInitializer  {


    private static <T> DataComponentType<T> register(Identifier id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, (builderOperator.apply(DataComponentType.builder())).build());
    }

    public static DataComponentType<ArcaneEnchantmentComponent> ARCANE_ENCHANTMENT_COMPONENT = register(new Identifier("bwt", "arcane_enchantment"),
            builder -> builder.codec(ArcaneEnchantmentComponent.CODEC).packetCodec(ArcaneEnchantmentComponent.PACKET_CODEC).cache());


    @Override
    public void onInitialize() {

    }
}
