package com.bwt.items;

import com.bwt.items.components.ArcaneEnchantmentComponent;
import com.bwt.items.components.BwtDataComponents;
import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ArcaneTomeItem extends Item {
    public ArcaneTomeItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        appendComponentTooltip(stack, BwtDataComponents.ARCANE_ENCHANTMENT_COMPONENT, context, tooltip::add, type);
    }

    private <T extends TooltipAppender> void appendComponentTooltip(ItemStack stack, ComponentType<T> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type) {
        T tooltipAppender = stack.get(componentType);
        if (tooltipAppender != null) {
            tooltipAppender.appendTooltip(context, textConsumer, type);
        }
    }

    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    public static ItemStack forEnchantment(RegistryEntry<Enchantment> enchantment) {
        ItemStack itemStack = new ItemStack(BwtItems.arcaneTome);
        itemStack.set(BwtDataComponents.ARCANE_ENCHANTMENT_COMPONENT, new ArcaneEnchantmentComponent(enchantment, true));
        return itemStack;
    }


}