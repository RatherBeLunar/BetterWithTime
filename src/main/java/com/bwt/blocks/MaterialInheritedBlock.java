package com.bwt.blocks;

import com.bwt.utils.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;

public abstract class MaterialInheritedBlock extends Block {
    public final Block fullBlock;
    public boolean isWood = false;

    public MaterialInheritedBlock(Properties settings, Block fullBlock) {
        super(settings);
        this.fullBlock = fullBlock;
    }

    public boolean isWood() {
        return isWood;
    }

    public static void registerMaterialBlocks(
            ArrayList<SidingBlock> sidingBlocks,
            ArrayList<MouldingBlock> mouldingBlocks,
            ArrayList<CornerBlock> cornerBlocks,
            ArrayList<ColumnBlock> columnBlocks,
            ArrayList<PedestalBlock> pedestalBlocks,
            ArrayList<TableBlock> tableBlocks
    ) {
        Stream.concat(
            WoodType.values()
                    .map(woodType -> BuiltInRegistries.BLOCK.getOptional(Id.mc(woodType.name() + "_planks")))
                    .filter(Optional::isPresent)
                    .map(Optional::get),
            Stream.of(Blocks.BAMBOO_MOSAIC)
        )
                .forEach(block -> {
            sidingBlocks.add(SidingBlock.ofWoodBlock(block));
            mouldingBlocks.add(MouldingBlock.ofWoodBlock(block));
            cornerBlocks.add(CornerBlock.ofWoodBlock(block));
            columnBlocks.add(ColumnBlock.ofWoodBlock(block));
            pedestalBlocks.add(PedestalBlock.ofWoodBlock(block));
            tableBlocks.add(TableBlock.ofWoodBlock(block));
        });
        List<BlockFamily> blockFamilies = List.of(
                BlockFamilies.COBBLESTONE,
                BlockFamilies.STONE,
                BlockFamilies.STONE_BRICK,
                BlockFamilies.MOSSY_STONE_BRICKS,
                BlockFamilies.SANDSTONE,
                BlockFamilies.RED_SANDSTONE,
                BlockFamilies.BRICKS,
                BlockFamilies.NETHER_BRICKS,
                BlockFamilies.DIORITE,
                BlockFamilies.POLISHED_DIORITE,
                BlockFamilies.ANDESITE,
                BlockFamilies.POLISHED_ANDESITE,
                BlockFamilies.GRANITE,
                BlockFamilies.POLISHED_GRANITE,
                BlockFamilies.COBBLED_DEEPSLATE,
                BlockFamilies.TUFF,
                BlockFamilies.MUD_BRICKS,
                BlockFamilies.PRISMARINE,
                BlockFamilies.END_STONE_BRICKS,
                BlockFamilies.PURPUR
        );
        blockFamilies.stream().map(BlockFamily::getBaseBlock).forEach(block -> {
            sidingBlocks.add(SidingBlock.ofBlock(block));
            mouldingBlocks.add(MouldingBlock.ofBlock(block));
            cornerBlocks.add(CornerBlock.ofBlock(block));
            columnBlocks.add(ColumnBlock.ofBlock(block));
            pedestalBlocks.add(PedestalBlock.ofBlock(block));
            tableBlocks.add(TableBlock.ofBlock(block));
        });
        for (int i = 0; i < sidingBlocks.size(); i++) {
            SidingBlock sidingBlock = sidingBlocks.get(i);
            MouldingBlock mouldingBlock = mouldingBlocks.get(i);
            CornerBlock cornerBlock = cornerBlocks.get(i);
            ColumnBlock columnBlock = columnBlocks.get(i);
            PedestalBlock pedestalBlock = pedestalBlocks.get(i);
            TableBlock tableBlock = tableBlocks.get(i);
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(sidingBlock.fullBlock);
            ResourceLocation sidingId = Id.of(blockId.getPath() + "_siding");
            ResourceLocation mouldingId = Id.of(blockId.getPath() + "_moulding");
            ResourceLocation cornerId = Id.of(blockId.getPath() + "_corner");
            ResourceLocation columnId = Id.of(blockId.getPath() + "_column");
            ResourceLocation pedestalId = Id.of(blockId.getPath() + "_pedestal");
            ResourceLocation tableId = Id.of(blockId.getPath() + "_table");
            Registry.register(BuiltInRegistries.BLOCK, sidingId, sidingBlock);
            Registry.register(BuiltInRegistries.BLOCK, mouldingId, mouldingBlock);
            Registry.register(BuiltInRegistries.BLOCK, cornerId, cornerBlock);
            Registry.register(BuiltInRegistries.BLOCK, columnId, columnBlock);
            Registry.register(BuiltInRegistries.BLOCK, pedestalId, pedestalBlock);
            Registry.register(BuiltInRegistries.BLOCK, tableId, tableBlock);
            Registry.register(BuiltInRegistries.ITEM, sidingId, new BlockItem(sidingBlock, new Item.Properties()));
            Registry.register(BuiltInRegistries.ITEM, mouldingId, new BlockItem(mouldingBlock, new Item.Properties()));
            Registry.register(BuiltInRegistries.ITEM, cornerId, new BlockItem(cornerBlock, new Item.Properties()));
            Registry.register(BuiltInRegistries.ITEM, columnId, new BlockItem(columnBlock, new Item.Properties()));
            Registry.register(BuiltInRegistries.ITEM, pedestalId, new BlockItem(pedestalBlock, new Item.Properties()));
            Registry.register(BuiltInRegistries.ITEM, tableId, new BlockItem(tableBlock, new Item.Properties()));
        }
    }
}
