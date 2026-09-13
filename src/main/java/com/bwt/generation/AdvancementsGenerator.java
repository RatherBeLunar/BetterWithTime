package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.entities.BwtEntities;
import com.bwt.items.BwtItems;
import com.bwt.tags.BwtItemTags;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementsGenerator extends FabricAdvancementProvider {
    public static final ResourceLocation background = Id.mc("textures/gui/advancements/backgrounds/adventure.png");

    public AdvancementsGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        AdvancementHolder rootAdvancement = Advancement.Builder.advancement()
                .display(
                        BwtBlocks.companionCubeBlock,
                        Component.literal("Better With Time"),
                        Component.literal("Made with love, and souls"),
                        background,
                        AdvancementType.TASK,
                        false,
                        false,
                        false
                )
                .addCriterion("active_immediately", PlayerTrigger.TriggerInstance.tick())
                .save(consumer, Id.MOD_ID + "/root");
        AdvancementHolder hempSeedsAdvancement = itemAdvancement(
                BwtItems.hempSeedsItem,
                "The Beginning",
                "Till grass to find hemp seeds"
        ).parent(rootAdvancement).save(consumer, Id.MOD_ID + "/got_hemp_seeds");
        AdvancementHolder hempFiberAdvancement = itemAdvancement(
                BwtItems.hempFiberItem,
                "Manual Labor",
                "Grind your first hemp by hand in a mill stone with a hand crank"
        ).parent(hempSeedsAdvancement).save(consumer, Id.MOD_ID + "/got_hemp_fiber");
        AdvancementHolder windmillAdvancement = Advancement.Builder.advancement().parent(hempFiberAdvancement)
                .display(
                        BwtItems.windmillItem,
                        Component.literal("Mechanical Age"),
                        Component.literal("Make space for your first windmill and place it on an axle"),
                        background,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("placed_windmill", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(BwtEntities.windmillEntity)))
                .save(consumer, Id.MOD_ID + "/placed_windmill");

        AdvancementHolder cauldronAdvancement = blockPlacedAdvancement(
                BwtBlocks.cauldronBlock,
                "Perpetual Stew",
                "Place a cauldron over some fire. Baby, you got a stew going!"
        ).parent(rootAdvancement).save(consumer, Id.MOD_ID + "/placed_cauldron");
        AdvancementHolder lightBlockAdvancement = itemAdvancement(
                BwtBlocks.lightBlockBlock,
                "Underground Grow Operation",
                "Create a light block. To hemp, it might as well be the sun"
        ).parent(cauldronAdvancement).save(consumer, Id.MOD_ID + "/got_light_block");
        AdvancementHolder dungAdvancement = itemAdvancement(
                BwtItems.dungItem,
                "The Stench of Progress",
                "Feed your pet wolf, and let nature take its course"
        ).parent(cauldronAdvancement).save(consumer, Id.MOD_ID + "/got_dung");
        AdvancementHolder tannedLeatherAdvancement = itemAdvancement(
                BwtItems.tannedLeatherItem,
                "Dung-Tanned",
                "Tan leather in the cauldron using dung"
        ).parent(dungAdvancement).save(consumer, Id.MOD_ID + "/got_tanned_leather");
        AdvancementHolder sawAdvancement = blockPlacedAdvancement(
                BwtBlocks.sawBlock,
                "DIY Carpentry",
                "Build and power a saw in pursuit of finer wood pieces"
        ).parent(tannedLeatherAdvancement).save(consumer, Id.MOD_ID + "/placed_saw");
        AdvancementHolder cornerAdvancement = itemTagAdvancement(
                BwtBlocks.cornerBlocks.stream().filter(cornerBlock -> cornerBlock.fullBlock == Blocks.OAK_PLANKS).findFirst().orElseThrow(),
                BwtItemTags.WOODEN_CORNER_BLOCKS,
                "Cut my Block Into Pieces",
                "Use the saw to chop a plank down into corners. Maybe these smaller pieces can be used more efficiently!"
        ).parent(sawAdvancement).save(consumer, Id.MOD_ID + "/got_wooden_corner");
        AdvancementHolder hopperAdvancement = blockPlacedAdvancement(
                BwtBlocks.hopperBlock,
                "The Original Hopper",
                "Place your first mechanical hopper. It has a filter slot; what could that be for?"
        ).parent(cornerAdvancement).save(consumer, Id.MOD_ID + "/placed_hopper");
        AdvancementHolder hellfireDustAdvancement = itemAdvancement(
                BwtItems.hellfireDustItem,
                "Soul Extraction",
                "Extract the souls out of ground netherrack using a soul sand filtered hopper. Be sure to power the hopper to let them escape!"
        ).parent(hopperAdvancement).save(consumer, Id.MOD_ID + "/got_hellfire_dust");
        AdvancementHolder hibachiAdvancement = blockPlacedAdvancement(
                BwtBlocks.hibachiBlock,
                "Fire on Demand",
                "Concentrate hellfire dust in a cauldron, and make a hibachi grill for redstone-toggled fire"
        ).parent(hellfireDustAdvancement).save(consumer, Id.MOD_ID + "/placed_hibachi");
        AdvancementHolder bellowsAdvancement = blockPlacedAdvancement(
                BwtBlocks.bellowsBlock,
                "Inhale, Exhale, Repeat",
                "Build and power the bellows, to stoke the hibachi's fire even further. There's something about the timing..."
        ).parent(sawAdvancement).save(consumer, Id.MOD_ID + "/placed_bellows");
        AdvancementHolder turntableAdvancement = blockPlacedAdvancement(
                BwtBlocks.turntableBlock,
                "The Tables Have Turned",
                "Build and power the Turntable. Throw some clay on it to make pottery!"
        ).parent(sawAdvancement).save(consumer, Id.MOD_ID + "/placed_turntable");
        AdvancementHolder potteryAdvancement = itemAdvancement(
                BwtBlocks.unfiredCrucibleBlock,
                "Potter's Guild",
                "Spin your first piece of pottern on the Turntable. Next, into the Kiln!",
                BwtBlocks.unfiredDecoratedPotBlockWithSherds, BwtBlocks.unfiredDecoratedPotBlock, BwtBlocks.unfiredCrucibleBlock, BwtBlocks.unfiredPlanterBlock, BwtBlocks.unfiredVaseBlock, BwtBlocks.unfiredUrnBlock, BwtBlocks.unfiredFlowerPotBlock
        ).parent(turntableAdvancement).save(consumer, Id.MOD_ID + "/got_pottery");
        AdvancementHolder glueAdvancement = itemAdvancement(
                BwtItems.glueItem,
                "Animal Byproducts",
                "Render animal products into glue and tallow in a Cauldron above stoked fire",
                BwtItems.glueItem, BwtItems.tallowItem
        ).parent(bellowsAdvancement).save(consumer, Id.MOD_ID + "/got_glue_or_tallow");
        AdvancementHolder waterWheelAdvancement = Advancement.Builder.advancement().parent(glueAdvancement)
                .display(
                        BwtItems.waterWheelItem,
                        Component.literal("Mechanical Age 2"),
                        Component.literal("Harness more reliable mechanical power from a Water Wheel"),
                        background,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("placed_water_wheel", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(BwtEntities.waterWheelEntity)))
                .save(consumer, Id.MOD_ID + "/placed_water_wheel");
        AdvancementHolder kilnAdvancement = itemAdvancement(
                Blocks.BRICKS,
                "Fired and Glazed",
                "Surround your pottery with enough brick blocks above a stoked fire, and it will harden into its final form! The kiln can cook other things, too.",
                Blocks.DECORATED_POT, BwtBlocks.crucibleBlock, BwtBlocks.planterBlock, BwtBlocks.vaseBlocks.get(DyeColor.WHITE), BwtBlocks.urnBlock
        ).parent(potteryAdvancement).save(consumer, Id.MOD_ID + "/kiln_fired");
        AdvancementHolder soulUrnAdvancement = itemAdvancement(
                BwtItems.soulUrnItem,
                "Soul Containment",
                "Capture 8 souls released from a hopper in an urn placed below"
        ).parent(hellfireDustAdvancement).save(consumer, Id.MOD_ID + "/got_soul_urn");
        AdvancementHolder crucibleAdvancement = blockPlacedAdvancement(
                BwtBlocks.crucibleBlock,
                "Melt and Smelt",
                "Place a Crucible over stoked fire. It heats up hot enough to melt most metals"
        ).parent(kilnAdvancement).save(consumer, Id.MOD_ID + "/placed_crucible");
        AdvancementHolder netheriteAdvancement = itemAdvancement(
                Items.NETHERITE_INGOT,
                "Soul-Forged Steel",
                "Smelt a netherite ingot in the crucible with iron, coal dust, gold, and a soul urn. Or find one in a loot chest, I guess"
        ).parent(crucibleAdvancement).save(consumer, Id.MOD_ID + "/got_netherite_ingot");
        AdvancementHolder soulForgeAdvancement = blockPlacedAdvancement(
                BwtBlocks.soulForgeBlock,
                "A New Age",
                "Place your first Soul Forge. It's better than an anvil!"
        ).parent(netheriteAdvancement).save(consumer, Id.MOD_ID + "/placed_soul_forge");
    }

    public Advancement.Builder itemAdvancement(ItemLike displayItem, String title, String description, ItemLike... items) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .display(
                        displayItem, // The display icon
                        Component.literal(title), // The title
                        Component.literal(description), // The description
                        background, // Background image used
                        AdvancementType.TASK, // Options: TASK, CHALLENGE, GOAL
                        true, // Show toast top right
                        true, // Announce to chat
                        false // Hidden in the advancement tab
                )
                .requirements(AdvancementRequirements.Strategy.OR);
        for (ItemLike item : items) {
            builder = builder.addCriterion("got_" + BuiltInRegistries.ITEM.getKey(item.asItem()).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(item));
        }
        return builder;
    }

    public Advancement.Builder itemAdvancement(ItemLike item, String title, String description) {
        return itemAdvancement(item, title, description, item);
    }

    public Advancement.Builder itemTagAdvancement(ItemLike displayItem, TagKey<Item> itemTag, String title, String description) {
        return Advancement.Builder.advancement()
                .display(
                        displayItem, // The display icon
                        Component.literal(title), // The title
                        Component.literal(description), // The description
                        background, // Background image used
                        AdvancementType.TASK, // Options: TASK, CHALLENGE, GOAL
                        true, // Show toast top right
                        true, // Announce to chat
                        false // Hidden in the advancement tab
                )
                .addCriterion("got_" + itemTag.location().getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(BwtItemTags.WOODEN_CORNER_BLOCKS)));
    }

    public Advancement.Builder blockPlacedAdvancement(Block block, String title, String description, Criterion<?> criterion) {
        return Advancement.Builder.advancement()
                .display(
                        block.asItem(), // The display icon
                        Component.literal(title), // The title
                        Component.literal(description), // The description
                        background, // Background image used
                        AdvancementType.TASK, // Options: TASK, CHALLENGE, GOAL
                        true, // Show toast top right
                        true, // Announce to chat
                        false // Hidden in the advancement tab
                )
                .addCriterion("place_" + BuiltInRegistries.BLOCK.getKey(block).getPath(), criterion);
    }

    public Advancement.Builder blockPlacedAdvancement(Block block, String title, String description) {
        return blockPlacedAdvancement(block, title, description, ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(block));
    }

    public Advancement.Builder neighborStateAdvancement(
            Block block,
            String title,
            String description,
            Direction direction,
            LocationPredicate.Builder neighborStatePredicate
    ) {
        LootItemCondition.Builder thisCondition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(block);
        LootItemCondition.Builder neighborCondition = LocationCheck.checkLocation(neighborStatePredicate, new BlockPos(direction.getNormal()));
        Criterion<ItemUsedOnLocationTrigger.TriggerInstance> criterion = ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(thisCondition, neighborCondition);
        return blockPlacedAdvancement(block, title, description, criterion);
    }
}
