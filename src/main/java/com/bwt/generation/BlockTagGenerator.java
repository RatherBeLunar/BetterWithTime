package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.tags.BwtBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
    public BlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(BlockTags.REPLACEABLE).add(BwtBlocks.detectorLogicBlock);
        getOrCreateTagBuilder(BlockTags.CLIMBABLE).add(BwtBlocks.ropeBlock);
        getOrCreateTagBuilder(BlockTags.RAILS).add(BwtBlocks.stoneDetectorRailBlock, BwtBlocks.obsidianDetectorRailBlock);

        getOrCreateTagBuilder(BwtBlockTags.BLOCK_DISPENSER_INHALE_NOOP)
                .forceAddTag(BlockTags.WITHER_IMMUNE)
                .forceAddTag(BlockTags.FIRE)
                .forceAddTag(BlockTags.PORTALS);
        getOrCreateTagBuilder(BwtBlockTags.DETECTABLE_SMALL_CROPS)
                .add(Blocks.WHEAT)
                .add(Blocks.CARROTS)
                .add(Blocks.POTATOES);
        FabricTagBuilder woodenSidingBuilder = getOrCreateTagBuilder(BwtBlockTags.WOODEN_SIDING_BLOCKS);
        FabricTagBuilder woodenMouldingBuilder = getOrCreateTagBuilder(BwtBlockTags.WOODEN_MOULDING_BLOCKS);
        FabricTagBuilder woodenCornerBuilder = getOrCreateTagBuilder(BwtBlockTags.WOODEN_CORNER_BLOCKS);
        FabricTagBuilder sidingBuilder = getOrCreateTagBuilder(BwtBlockTags.SIDING_BLOCKS);
        FabricTagBuilder mouldingBuilder = getOrCreateTagBuilder(BwtBlockTags.MOULDING_BLOCKS);
        FabricTagBuilder cornerBuilder = getOrCreateTagBuilder(BwtBlockTags.CORNER_BLOCKS);
        BwtBlocks.sidingBlocks.stream().filter(sidingBlock -> Registries.BLOCK.getId(sidingBlock.fullBlock).getPath().contains("planks")).forEach(woodenSidingBuilder::add);
        BwtBlocks.mouldingBlocks.stream().filter(mouldingBlock -> Registries.BLOCK.getId(mouldingBlock.fullBlock).getPath().contains("planks")).forEach(woodenMouldingBuilder::add);
        BwtBlocks.cornerBlocks.stream().filter(cornerBlock -> Registries.BLOCK.getId(cornerBlock.fullBlock).getPath().contains("planks")).forEach(woodenCornerBuilder::add);
        sidingBuilder.addTag(BwtBlockTags.WOODEN_SIDING_BLOCKS);
        mouldingBuilder.addTag(BwtBlockTags.WOODEN_MOULDING_BLOCKS);
        cornerBuilder.addTag(BwtBlockTags.WOODEN_CORNER_BLOCKS);
        BwtBlocks.sidingBlocks.stream().filter(sidingBlock -> !Registries.BLOCK.getId(sidingBlock.fullBlock).getPath().contains("planks")).forEach(sidingBuilder::add);
        BwtBlocks.mouldingBlocks.stream().filter(mouldingBlock -> !Registries.BLOCK.getId(mouldingBlock.fullBlock).getPath().contains("planks")).forEach(mouldingBuilder::add);
        BwtBlocks.cornerBlocks.stream().filter(cornerBlock -> !Registries.BLOCK.getId(cornerBlock.fullBlock).getPath().contains("planks")).forEach(cornerBuilder::add);
    }
}
