package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlock;
import com.bwt.entities.BwtEntities;
import com.bwt.items.BwtItems;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AdvancementsGenerator extends FabricAdvancementProvider {
    public static final Identifier background = Id.mc("textures/gui/advancements/backgrounds/adventure.png");

    public AdvancementsGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
        AdvancementEntry rootAdvancement = Advancement.Builder.create()
                .display(
                        BwtBlocks.companionCubeBlock,
                        Text.literal("Better With Time"),
                        Text.literal("Made with love, and souls"),
                        background,
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criterion("activeImmediately", TickCriterion.Conditions.createTick())
                .build(consumer, "bwt/root");
        AdvancementEntry hempSeedsAdvancement = itemAdvancement(BwtItems.hempSeedsItem, "The Beginning", "Till grass to find hemp seeds").parent(rootAdvancement).build(consumer, "bwt/got_hemp_seeds");
        AdvancementEntry hempFiberAdvancement = itemAdvancement(BwtItems.hempFiberItem, "Manual Labor", "Grind your first hemp by hand in a mill stone").parent(hempSeedsAdvancement).build(consumer, "bwt/got_hemp_fiber");
        AdvancementEntry windmillAdvancement = Advancement.Builder.create().parent(hempFiberAdvancement)
                .display(
                        BwtItems.windmillItem,
                        Text.literal("Mechanical Age"),
                        Text.literal("Make space for your first windmill and place it on an axle"),
                        background,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("placed_windmill", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(BwtEntities.windmillEntity)))
                .build(consumer, "bwt/placed_windmill");

        AdvancementEntry cauldronAdvancement = blockPlacedAdvancement(
                BwtBlocks.cauldronBlock,
                "Perpetual Stew",
                "Place a cauldron over some fire. Baby, you got a stew going!"
        ).parent(rootAdvancement).build(consumer, "bwt/placed_cauldron");
        AdvancementEntry lightBlockAdvancement = itemAdvancement(BwtBlocks.lightBlockBlock, "Underground Grow Operation", "Create a light block. To hemp, it might as well be the sun").parent(cauldronAdvancement).build(consumer, "bwt/got_light_block");
        AdvancementEntry dungAdvancement = itemAdvancement(BwtItems.dungItem, "The Stench of Progress", "Feed your pet wolf, and let nature take its course").parent(cauldronAdvancement).build(consumer, "bwt/got_dung");
        AdvancementEntry tannedLeatherAdvancement = itemAdvancement(BwtItems.tannedLeatherItem, "Dung-Tanned", "Tan leather in the cauldron using dung").parent(dungAdvancement).build(consumer, "bwt/got_tanned_leather");
        AdvancementEntry sawAdvancement = blockPlacedAdvancement(BwtBlocks.sawBlock, "DIY Carpentry", "Build and power a saw in pursuit of finer wood pieces").parent(tannedLeatherAdvancement).build(consumer, "bwt/placed_saw");


    }

    public Advancement.Builder itemAdvancement(ItemConvertible item, String title, String description) {
        return Advancement.Builder.create()
                .display(
                        item, // The display icon
                        Text.literal(title), // The title
                        Text.literal(description), // The description
                        background, // Background image used
                        AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
                        true, // Show toast top right
                        true, // Announce to chat
                        false // Hidden in the advancement tab
                )
                // The first string used in criterion is the name referenced by other advancements when they want to have 'requirements'
                .criterion("got_" + Registries.ITEM.getId(item.asItem()).getPath(), InventoryChangedCriterion.Conditions.items(item));
    }

    public Advancement.Builder blockPlacedAdvancement(Block block, String title, String description, AdvancementCriterion<?> criterion) {
        return Advancement.Builder.create()
                .display(
                        block.asItem(), // The display icon
                        Text.literal(title), // The title
                        Text.literal(description), // The description
                        background, // Background image used
                        AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
                        true, // Show toast top right
                        true, // Announce to chat
                        false // Hidden in the advancement tab
                )
                // The first string used in criterion is the name referenced by other advancements when they want to have 'requirements'
                .criterion("place_" + Registries.BLOCK.getId(block).getPath(), criterion);
    }

    public Advancement.Builder blockPlacedAdvancement(Block block, String title, String description) {
        return blockPlacedAdvancement(block, title, description, ItemCriterion.Conditions.createPlacedBlock(block));
    }

    public Advancement.Builder neighborStateAdvancement(
            Block block,
            String title,
            String description,
            Direction direction,
            LocationPredicate.Builder neighborStatePredicate
    ) {
        LootCondition.Builder thisCondition = BlockStatePropertyLootCondition.builder(block);
        LootCondition.Builder neighborCondition = LocationCheckLootCondition.builder(neighborStatePredicate, new BlockPos(direction.getVector()));
        AdvancementCriterion<ItemCriterion.Conditions> criterion = ItemCriterion.Conditions.createPlacedBlock(thisCondition, neighborCondition);
        return blockPlacedAdvancement(block, title, description, criterion);
    }
}
