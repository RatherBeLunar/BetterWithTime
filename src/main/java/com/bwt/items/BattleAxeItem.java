package com.bwt.items;

import com.bwt.tags.BwtBlockTags;
import com.bwt.utils.LockableItemSettings;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;

public class BattleAxeItem extends SwordItem {
    public BattleAxeItem(Tier material, LockableItemSettings settings) {
        super(material, settings.component(DataComponents.TOOL, createToolComponent(material)).durability(material.getUses()).lock());
    }

    private static Tool createToolComponent(Tier material) {
        return new Tool(
                List.of(
                        Tool.Rule.deniesDrops(material.getIncorrectBlocksForDrops()),
                        Tool.Rule.minesAndDrops(BwtBlockTags.BATTLEAXE_MINEABLE, material.getSpeed()),
                        Tool.Rule.minesAndDrops(List.of(Blocks.COBWEB), 15.0F)
                ),
                1.0F,
                1
        );
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return Items.NETHERITE_AXE.useOn(context);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(Blocks.COBWEB)) {
            return 15.0f;
        }
        return super.getDestroySpeed(stack, state);
    }
}
