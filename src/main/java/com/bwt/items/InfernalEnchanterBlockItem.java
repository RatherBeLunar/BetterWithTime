package com.bwt.items;

import com.bwt.items.components.BwtDataComponents;
import net.minecraft.block.Block;
import net.minecraft.component.ComponentType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class InfernalEnchanterBlockItem extends BlockItem  {
    public InfernalEnchanterBlockItem(Block block, Settings settings) {
        super(block, settings);
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        appendComponentTooltip(stack, BwtDataComponents.INFERNAL_ENCHANTER_DECORATION_COMPONENT, context, tooltip::add, type);
    }


    private <T extends TooltipAppender> void appendComponentTooltip(ItemStack stack, ComponentType<T> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type) {
        T tooltipAppender = stack.get(componentType);
        if (tooltipAppender != null) {
            tooltipAppender.appendTooltip(context, textConsumer, type);
        }
    }

}
