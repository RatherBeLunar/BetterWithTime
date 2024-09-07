package com.bwt.items.components;

import net.fabricmc.api.ModInitializer;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class BwtDataComponents  implements ModInitializer  {


    private static <T> ComponentType<T> register(Identifier id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, (builderOperator.apply(ComponentType.builder())).build());
    }

    public static ComponentType<ArcaneEnchantmentComponent> ARCANE_ENCHANTMENT_COMPONENT = register(Identifier.of("bwt", "arcane_enchantment"),
            builder -> builder.codec(ArcaneEnchantmentComponent.CODEC).packetCodec(ArcaneEnchantmentComponent.PACKET_CODEC).cache());

    public static ComponentType<InfernalEnchanterDecorationComponent> INFERNAL_ENCHANTER_DECORATION_COMPONENT = register(Identifier.of("bwt", "infernal_enchanter_decoration"),
            builder -> builder.codec(InfernalEnchanterDecorationComponent.CODEC).packetCodec(InfernalEnchanterDecorationComponent.PACKET_CODEC).cache());


    @Override
    public void onInitialize() {

    }
}
