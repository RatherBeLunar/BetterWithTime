package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class AdvancementsGenerator extends FabricAdvancementProvider {
    public AdvancementsGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<AdvancementEntry> consumer) {
        AdvancementEntry rootAdvancement = Advancement.Builder.create()
                .display(
                        BwtBlocks.companionCubeBlock, // The display icon
                        Text.literal("Better With Time"), // The title
                        Text.literal("Made with love, and souls"), // The description
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"), // Background image used
                        AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
                        true, // Show toast top right
                        true, // Announce to chat
                        false // Hidden in the advancement tab
                )
                // The first string used in criterion is the name referenced by other advancements when they want to have 'requirements'
                .criterion("got_hemp_seeds", InventoryChangedCriterion.Conditions.items(BwtItems.hempSeedsItem))
                .build(consumer, "bwt/root");
        AdvancementEntry hempSeedsAdvancement = Advancement.Builder.create()
                .display(
                        BwtItems.hempSeedsItem, // The display icon
                        Text.literal("The Beginning"), // The title
                        Text.literal("Till grass to find hemp seeds"), // The description
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"), // Background image used
                        AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
                        true, // Show toast top right
                        true, // Announce to chat
                        false // Hidden in the advancement tab
                )
                // The first string used in criterion is the name referenced by other advancements when they want to have 'requirements'
                .criterion("got_hemp_seeds", InventoryChangedCriterion.Conditions.items(BwtItems.hempSeedsItem))
                .build(consumer, "bwt/root");
    }
}
