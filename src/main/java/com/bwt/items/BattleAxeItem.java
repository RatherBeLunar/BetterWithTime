package com.bwt.items;

import com.bwt.tags.BwtBlockTags;
import com.bwt.utils.LockableItemSettings;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;

public class BattleAxeItem extends SwordItem {
    public BattleAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, LockableItemSettings properties) {
        super(material, attackDamage, attackSpeed, applyToolProperties(material, properties, attackDamage, attackSpeed).lock());
    }

    public static LockableItemSettings applyToolProperties(ToolMaterial toolMaterial, LockableItemSettings properties, float attackDamage, float attackSpeed) {
        HolderGetter<Block> holderGetter = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        return properties
                .durability(toolMaterial.durability())
                .repairable(toolMaterial.repairItems())
                .enchantable(toolMaterial.enchantmentValue())
                .component(
                        DataComponents.TOOL,
                        new Tool(
                                List.of(
                                        Tool.Rule.deniesDrops(holderGetter.getOrThrow(toolMaterial.incorrectBlocksForDrops())),
                                        Tool.Rule.minesAndDrops(holderGetter.getOrThrow(BwtBlockTags.BATTLEAXE_MINEABLE), toolMaterial.speed()),
                                        Tool.Rule.minesAndDrops(HolderSet.direct(Blocks.COBWEB.builtInRegistryHolder()), 15.0F)
                                ),
                                1.0F,
                                1
                        )
                )
                .attributes(
                        ItemAttributeModifiers.builder()
                                .add(
                                        Attributes.ATTACK_DAMAGE,
                                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamage + toolMaterial.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
                                        EquipmentSlotGroup.MAINHAND
                                )
                                .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                                .build()
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
