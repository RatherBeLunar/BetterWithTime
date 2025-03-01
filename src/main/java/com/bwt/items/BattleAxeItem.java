package com.bwt.items;

import com.bwt.tags.BwtBlockTags;
import com.bwt.utils.LockableItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;

import java.util.List;

public class BattleAxeItem extends SwordItem {
    public BattleAxeItem(ToolMaterial material, LockableItemSettings settings) {
        super(material, settings.component(DataComponentTypes.TOOL, createToolComponent(material)).maxDamage(material.getDurability()).lock());
    }

    private static ToolComponent createToolComponent(ToolMaterial material) {
        return new ToolComponent(
                List.of(
                        ToolComponent.Rule.ofNeverDropping(material.getInverseTag()),
                        ToolComponent.Rule.ofAlwaysDropping(BwtBlockTags.BATTLEAXE_MINEABLE, material.getMiningSpeedMultiplier()),
                        ToolComponent.Rule.ofAlwaysDropping(List.of(Blocks.COBWEB), 15.0F)
                ),
                1.0F,
                1
        );
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return Items.NETHERITE_AXE.useOnBlock(context);
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        if (state.isOf(Blocks.COBWEB)) {
            return 15.0f;
        }
        return super.getMiningSpeed(stack, state);
    }
}
