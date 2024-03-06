package com.bwt.generation;

import com.bwt.tags.BwtBlockTags;
import com.bwt.tags.BwtItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {
    public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, BlockTagGenerator blockTagGenerator) {
        super(output, completableFuture, blockTagGenerator);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        copy(BwtBlockTags.WOODEN_SIDING_BLOCKS, BwtItemTags.WOODEN_SIDING_BLOCKS);
        copy(BwtBlockTags.WOODEN_MOULDING_BLOCKS, BwtItemTags.WOODEN_MOULDING_BLOCKS);
        copy(BwtBlockTags.WOODEN_CORNER_BLOCKS, BwtItemTags.WOODEN_CORNER_BLOCKS);
        copy(BwtBlockTags.SIDING_BLOCKS, BwtItemTags.SIDING_BLOCKS);
        copy(BwtBlockTags.MOULDING_BLOCKS, BwtItemTags.MOULDING_BLOCKS);
        copy(BwtBlockTags.CORNER_BLOCKS, BwtItemTags.CORNER_BLOCKS);
        getOrCreateTagBuilder(BwtItemTags.PASSES_LADDER_FILTER)
                .forceAddTag(ItemTags.FLOWERS)
                .forceAddTag(ItemTags.SAPLINGS)
                .forceAddTag(ItemTags.TOOLS)
                .forceAddTag(ItemTags.TRIMMABLE_ARMOR)
                .forceAddTag(ItemTags.ARROWS)
                .forceAddTag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .forceAddTag(ItemTags.FISHES)
                .forceAddTag(ItemTags.LOGS)
                .add(
                        Items.BROWN_MUSHROOM,
                        Items.RED_MUSHROOM,
                        Items.TORCH,
                        Items.REDSTONE_TORCH,
                        Items.APPLE,
                        Items.GOLDEN_APPLE,
                        Items.ENCHANTED_GOLDEN_APPLE,
                        Items.COAL,
                        Items.CHARCOAL,
                        Items.DIAMOND,
                        Items.IRON_INGOT,
                        Items.COPPER_INGOT,
                        Items.GOLD_INGOT,
                        Items.NETHERITE_INGOT,
                        Items.STICK,
                        Items.BOWL,
                        Items.MUSHROOM_STEW,
                        Items.STRING,
                        Items.FEATHER,
                        Items.GUNPOWDER,
                        Items.WHEAT_SEEDS,
                        Items.BEETROOT_SEEDS,
                        Items.MELON_SEEDS,
                        Items.PUMPKIN_SEEDS,
                        Items.TORCHFLOWER_SEEDS,
                        Items.WHEAT,
                        Items.BREAD,
                        Items.FLINT,
                        Items.PORKCHOP,
                        Items.COOKED_PORKCHOP,
                        Items.BEEF,
                        Items.COOKED_BEEF,
                        Items.CHICKEN,
                        Items.COOKED_CHICKEN,
                        Items.MUTTON,
                        Items.COOKED_MUTTON,
                        Items.RABBIT,
                        Items.COOKED_RABBIT,
                        Items.PAINTING,
                        Items.BUCKET
                );
    }
}
