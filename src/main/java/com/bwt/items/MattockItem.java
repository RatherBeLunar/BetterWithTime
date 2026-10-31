package com.bwt.items;

import com.bwt.tags.BwtBlockTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class MattockItem extends Item {
    public static final int ITEM_DAMAGE_PER_ATTACK = 2;
    public static final float DISABLE_BLOCKING_FOR_SECONDS = 0.0f;
    public static final float DEFAULT_MINING_SPEED = 1.0f;
    public static final int DAMAGE_PER_BLOCK = 1;
    public static final boolean CAN_DESTROY_BLOCKS_IN_CREATIVE = true;

    public MattockItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Properties properties) {
        super(applyMattockProperties(properties, material, attackDamage, attackSpeed));
    }

    public static Item.Properties applyMattockProperties(Item.Properties properties, ToolMaterial toolMaterial, float attackDamage, float attackSpeed) {
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
                                        Tool.Rule.minesAndDrops(holderGetter.getOrThrow(BwtBlockTags.MATTOCK_MINEABLE), toolMaterial.speed())
                                ),
                                DEFAULT_MINING_SPEED,
                                DAMAGE_PER_BLOCK,
                                CAN_DESTROY_BLOCKS_IN_CREATIVE
                        )
                )
                .component(DataComponents.WEAPON, new Weapon(ITEM_DAMAGE_PER_ATTACK, DISABLE_BLOCKING_FOR_SECONDS))
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
        // Only shovels have a right click action, so we inherit from that
        return Items.NETHERITE_SHOVEL.useOn(context);
    }
}
