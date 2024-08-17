package com.bwt.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.item.TooltipType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.TooltipAppender;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public class ArcaneEnchantmentComponent implements TooltipAppender {
    public static final ArcaneEnchantmentComponent DEFAULT = new ArcaneEnchantmentComponent(null, true);
    public static final Codec<ArcaneEnchantmentComponent> CODEC;
    public static final PacketCodec<RegistryByteBuf, ArcaneEnchantmentComponent> PACKET_CODEC;
    final RegistryEntry<Enchantment> enchantment;
    final boolean showInTooltip;

    public ArcaneEnchantmentComponent(RegistryEntry<Enchantment> enchantment, boolean showInTooltip) {
        this.enchantment = enchantment;
        this.showInTooltip = showInTooltip;
    }


    private static Text getEnchantmentName(Enchantment enchantment) {
        MutableText mutableText = Text.translatable(enchantment.getTranslationKey());
        if (enchantment.isCursed()) {
            mutableText.formatted(Formatting.RED);
        } else {
            mutableText.formatted(Formatting.GRAY);
        }

        return mutableText;
    }

    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        if (this.showInTooltip) {
            Enchantment enchantment = this.enchantment.value();
            tooltip.accept(getEnchantmentName(enchantment));
        }
    }
    public RegistryEntry<Enchantment> getEnchantmentEntry() {
        return this.enchantment;
    }

    public boolean isShowInTooltip() {
        return showInTooltip;
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof ArcaneEnchantmentComponent)) {
            return false;
        } else {
            ArcaneEnchantmentComponent itemEnchantmentsComponent = (ArcaneEnchantmentComponent)o;
            return this.showInTooltip == itemEnchantmentsComponent.showInTooltip && this.enchantment.equals(itemEnchantmentsComponent.enchantment);
        }
    }

    public int hashCode() {
        int i = this.enchantment != null ? this.enchantment.hashCode() : 0;
        i = 31 * i + (this.showInTooltip ? 1 : 0);
        return i;
    }

    public String toString() {
        String var10000 = String.valueOf(this.enchantment);
        return "ArcaneEnchantment{enchantment=" + var10000 + ", showInTooltip=" + this.showInTooltip + "}";
    }

    public Enchantment getEnchantment() {
        return this.enchantment.getKey().map(Registries.ENCHANTMENT::get).orElseGet(null);
    }


    static {
        CODEC =  RecordCodecBuilder.create((instance) -> instance.group(
                Registries.ENCHANTMENT.getEntryCodec().fieldOf("enchantment").forGetter(ArcaneEnchantmentComponent::getEnchantmentEntry
                ),
                Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(ArcaneEnchantmentComponent::isShowInTooltip)).apply(instance, ArcaneEnchantmentComponent::new)
        );
        PACKET_CODEC = PacketCodec.tuple(PacketCodecs.registryEntry(RegistryKeys.ENCHANTMENT), (component) -> component.enchantment, PacketCodecs.BOOL, (component) -> component.showInTooltip, ArcaneEnchantmentComponent::new);
    }


}