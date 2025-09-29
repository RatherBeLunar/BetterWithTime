package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.MaterialInheritedBlock;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlock;
import com.bwt.entities.BwtEntities;
import com.bwt.items.BwtItems;
import com.bwt.tags.BwtBlockTags;
import com.bwt.tags.BwtItemTags;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.List;
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
                .build(consumer, Id.MOD_ID + "/root");
        AdvancementEntry hempSeedsAdvancement = itemAdvancement(
                BwtItems.hempSeedsItem,
                "The Beginning",
                "Till grass to find hemp seeds"
        ).parent(rootAdvancement).build(consumer, Id.MOD_ID + "/got_hemp_seeds");
        AdvancementEntry hempFiberAdvancement = itemAdvancement(
                BwtItems.hempFiberItem,
                "Manual Labor",
                "Grind your first hemp by hand in a mill stone with a hand crank"
        ).parent(hempSeedsAdvancement).build(consumer, Id.MOD_ID + "/got_hemp_fiber");
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
                .build(consumer, Id.MOD_ID + "/placed_windmill");

        AdvancementEntry cauldronAdvancement = blockPlacedAdvancement(
                BwtBlocks.cauldronBlock,
                "Perpetual Stew",
                "Place a cauldron over some fire. Baby, you got a stew going!"
        ).parent(rootAdvancement).build(consumer, Id.MOD_ID + "/placed_cauldron");
        AdvancementEntry lightBlockAdvancement = itemAdvancement(
                BwtBlocks.lightBlockBlock,
                "Underground Grow Operation",
                "Create a light block. To hemp, it might as well be the sun"
        ).parent(cauldronAdvancement).build(consumer, Id.MOD_ID + "/got_light_block");
        AdvancementEntry dungAdvancement = itemAdvancement(
                BwtItems.dungItem,
                "The Stench of Progress",
                "Feed your pet wolf, and let nature take its course"
        ).parent(cauldronAdvancement).build(consumer, Id.MOD_ID + "/got_dung");
        AdvancementEntry tannedLeatherAdvancement = itemAdvancement(
                BwtItems.tannedLeatherItem,
                "Dung-Tanned",
                "Tan leather in the cauldron using dung"
        ).parent(dungAdvancement).build(consumer, Id.MOD_ID + "/got_tanned_leather");
        AdvancementEntry sawAdvancement = blockPlacedAdvancement(
                BwtBlocks.sawBlock,
                "DIY Carpentry",
                "Build and power a saw in pursuit of finer wood pieces"
        ).parent(tannedLeatherAdvancement).build(consumer, Id.MOD_ID + "/placed_saw");
        AdvancementEntry cornerAdvancement = itemTagAdvancement(
                BwtBlocks.cornerBlocks.stream().filter(cornerBlock -> cornerBlock.fullBlock == Blocks.OAK_PLANKS).findFirst().orElseThrow(),
                BwtItemTags.WOODEN_CORNER_BLOCKS,
                "Cut my Block Into Pieces",
                "Use the saw to chop a plank down into corners. Maybe these smaller pieces can be used more efficiently!"
        ).parent(sawAdvancement).build(consumer, Id.MOD_ID + "/got_wooden_corner");
        AdvancementEntry hopperAdvancement = blockPlacedAdvancement(
                BwtBlocks.hopperBlock,
                "The Original Hopper",
                "Place your first mechanical hopper. It has a filter slot; what could that be for?"
        ).parent(cornerAdvancement).build(consumer, Id.MOD_ID + "/placed_hopper");
        AdvancementEntry hellfireDustAdvancement = itemAdvancement(
                BwtItems.hellfireDustItem,
                "Soul Extraction",
                "Extract the souls out of ground netherrack using a soul sand filtered hopper. Be sure to power the hopper to let them escape!"
        ).parent(hopperAdvancement).build(consumer, Id.MOD_ID + "/got_hellfire_dust");
        AdvancementEntry hibachiAdvancement = blockPlacedAdvancement(
                BwtBlocks.hibachiBlock,
                "Fire on Demand",
                "Concentrate hellfire dust in a cauldron, and make a hibachi grill for redstone-toggled fire"
        ).parent(hellfireDustAdvancement).build(consumer, Id.MOD_ID + "/placed_hibachi");
        AdvancementEntry bellowsAdvancement = blockPlacedAdvancement(
                BwtBlocks.bellowsBlock,
                "Inhale, Exhale, Repeat",
                "Build and power the bellows, to stoke the hibachi's fire even further. There's something about the timing..."
        ).parent(sawAdvancement).build(consumer, Id.MOD_ID + "/placed_bellows");
        AdvancementEntry turntableAdvancement = blockPlacedAdvancement(
                BwtBlocks.turntableBlock,
                "The Tables Have Turned",
                "Build and power the Turntable. Throw some clay on it to make pottery!"
        ).parent(sawAdvancement).build(consumer, Id.MOD_ID + "/placed_turntable");
        AdvancementEntry potteryAdvancement = itemAdvancement(
                BwtBlocks.unfiredCrucibleBlock,
                "Potter's Guild",
                "Spin your first piece of pottern on the Turntable. Next, into the Kiln!",
                BwtBlocks.unfiredCrucibleBlock, BwtBlocks.unfiredPlanterBlock, BwtBlocks.unfiredVaseBlock, BwtBlocks.unfiredUrnBlock, BwtBlocks.unfiredMouldBlock
        ).parent(turntableAdvancement).build(consumer, Id.MOD_ID + "/got_pottery");
        AdvancementEntry glueAdvancement = itemAdvancement(
                BwtItems.glueItem,
                "Animal Byproducts",
                "Render animal products into glue and tallow in a Cauldron above stoked fire",
                BwtItems.glueItem, BwtItems.tallowItem
        ).parent(bellowsAdvancement).build(consumer, Id.MOD_ID + "/got_glue_or_tallow");
        AdvancementEntry waterWheelAdvancement = Advancement.Builder.create().parent(glueAdvancement)
                .display(
                        BwtItems.waterWheelItem,
                        Text.literal("Mechanical Age 2"),
                        Text.literal("Harness more reliable mechanical power from a Water Wheel"),
                        background,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("placed_water_wheel", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(BwtEntities.waterWheelEntity)))
                .build(consumer, Id.MOD_ID + "/placed_water_wheel");
        AdvancementEntry kilnAdvancement = itemAdvancement(
                Blocks.BRICKS,
                "Fired and Glazed",
                "Surround your pottery with enough brick blocks above a stoked fire, and it will harden into its final form! The kiln can cook other things, too.",
                BwtBlocks.crucibleBlock, BwtBlocks.planterBlock, BwtBlocks.vaseBlocks.get(DyeColor.WHITE), BwtBlocks.urnBlock, BwtItems.mouldItem
        ).parent(potteryAdvancement).build(consumer, Id.MOD_ID + "/kiln_fired");
        AdvancementEntry soulUrnAdvancement = itemAdvancement(
                BwtItems.soulUrnItem,
                "Soul Containment",
                "Capture 8 souls released from a hopper in an urn placed below"
        ).parent(hellfireDustAdvancement).build(consumer, Id.MOD_ID + "/got_soul_urn");
        AdvancementEntry crucibleAdvancement = blockPlacedAdvancement(
                BwtBlocks.crucibleBlock,
                "Melt and Smelt",
                "Place a Crucible over stoked fire. It heats up hot enough to melt most metals"
        ).parent(kilnAdvancement).build(consumer, Id.MOD_ID + "/placed_crucible");
        AdvancementEntry netheriteAdvancement = itemAdvancement(
                Items.NETHERITE_INGOT,
                "Soul-Forged Steel",
                "Smelt a netherite ingot in the crucible with iron, coal dust, gold, and a soul urn. Or find one in a loot chest, I guess"
        ).parent(crucibleAdvancement).build(consumer, Id.MOD_ID + "/got_netherite_ingot");
        AdvancementEntry soulForgeAdvancement = blockPlacedAdvancement(
                BwtBlocks.soulForgeBlock,
                "A New Age",
                "Place your first Soul Forge. It's better than an anvil!"
        ).parent(netheriteAdvancement).build(consumer, Id.MOD_ID + "/placed_soul_forge");
    }

    public Advancement.Builder itemAdvancement(ItemConvertible displayItem, String title, String description, ItemConvertible... items) {
        Advancement.Builder builder = Advancement.Builder.create()
                .display(
                        displayItem, // The display icon
                        Text.literal(title), // The title
                        Text.literal(description), // The description
                        background, // Background image used
                        AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
                        true, // Show toast top right
                        true, // Announce to chat
                        false // Hidden in the advancement tab
                )
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        for (ItemConvertible item : items) {
            builder = builder.criterion("got_" + Registries.ITEM.getId(item.asItem()).getPath(), InventoryChangedCriterion.Conditions.items(item));
        }
        return builder;
    }

    public Advancement.Builder itemAdvancement(ItemConvertible item, String title, String description) {
        return itemAdvancement(item, title, description, item);
    }

    public Advancement.Builder itemTagAdvancement(ItemConvertible displayItem, TagKey<Item> itemTag, String title, String description) {
        return Advancement.Builder.create()
                .display(
                        displayItem, // The display icon
                        Text.literal(title), // The title
                        Text.literal(description), // The description
                        background, // Background image used
                        AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
                        true, // Show toast top right
                        true, // Announce to chat
                        false // Hidden in the advancement tab
                )
                .criterion("got_" + itemTag.id().getPath(), InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(BwtItemTags.WOODEN_CORNER_BLOCKS)));
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
