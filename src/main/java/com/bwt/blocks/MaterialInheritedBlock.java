package com.bwt.blocks;

import com.bwt.utils.Id;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.WoodType;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public abstract class MaterialInheritedBlock extends Block {
    public Block fullBlock;

    public MaterialInheritedBlock(Settings settings, Block fullBlock) {
        super(settings);
        this.fullBlock = fullBlock;
    }

    public boolean isWood() {
        return Registries.BLOCK.getId(fullBlock).getPath().contains("planks");
    }

    public static void registerMaterialBlocks(
            ArrayList<SidingBlock> sidingBlocks,
            ArrayList<MouldingBlock> mouldingBlocks,
            ArrayList<CornerBlock> cornerBlocks,
            ArrayList<ColumnBlock> columnBlocks,
            ArrayList<PedestalBlock> pedestalBlocks,
            ArrayList<TableBlock> tableBlocks
    ) {
        WoodType.stream().forEach(woodType -> {
            Block fullBlock = Registries.BLOCK.get(Id.mc(woodType.name() + "_planks"));
            Block slabBlock = Registries.BLOCK.get(Id.mc(woodType.name() + "_slab"));
            sidingBlocks.add(SidingBlock.ofBlock(fullBlock, slabBlock));
            mouldingBlocks.add(MouldingBlock.ofBlock(fullBlock, slabBlock));
            cornerBlocks.add(CornerBlock.ofBlock(fullBlock, slabBlock));
            columnBlocks.add(ColumnBlock.ofBlock(fullBlock, slabBlock));
            pedestalBlocks.add(PedestalBlock.ofBlock(fullBlock, slabBlock));
            tableBlocks.add(TableBlock.ofBlock(fullBlock, slabBlock));
        });
        List<BlockFamily> blockFamilies = List.of(
                BlockFamilies.STONE,
                BlockFamilies.STONE_BRICK,
                BlockFamilies.MOSSY_STONE_BRICK,
                BlockFamilies.SANDSTONE,
                BlockFamilies.RED_SANDSTONE,
                BlockFamilies.BRICK,
                BlockFamilies.NETHER_BRICK,
                BlockFamilies.DIORITE,
                BlockFamilies.POLISHED_DIORITE,
                BlockFamilies.ANDESITE,
                BlockFamilies.POLISHED_ANDESITE,
                BlockFamilies.GRANITE,
                BlockFamilies.POLISHED_GRANITE,
                BlockFamilies.COBBLED_DEEPSLATE,
                BlockFamilies.TUFF,
                BlockFamilies.MUD_BRICK,
                BlockFamilies.PRISMARINE,
                BlockFamilies.END_STONE_BRICK,
                BlockFamilies.PURPUR
        );
        blockFamilies.forEach(blockFamily -> {
            Block block = blockFamily.getBaseBlock();
            Block slabBlock = blockFamily.getVariant(BlockFamily.Variant.SLAB);
            sidingBlocks.add(SidingBlock.ofBlock(block, slabBlock));
            mouldingBlocks.add(MouldingBlock.ofBlock(block, slabBlock));
            cornerBlocks.add(CornerBlock.ofBlock(block, slabBlock));
            columnBlocks.add(ColumnBlock.ofBlock(block, slabBlock));
            pedestalBlocks.add(PedestalBlock.ofBlock(block, slabBlock));
            tableBlocks.add(TableBlock.ofBlock(block, slabBlock));
        });
        for (int i = 0; i < sidingBlocks.size(); i++) {
            SidingBlock sidingBlock = sidingBlocks.get(i);
            MouldingBlock mouldingBlock = mouldingBlocks.get(i);
            CornerBlock cornerBlock = cornerBlocks.get(i);
            ColumnBlock columnBlock = columnBlocks.get(i);
            PedestalBlock pedestalBlock = pedestalBlocks.get(i);
            TableBlock tableBlock = tableBlocks.get(i);
            Identifier blockId = Registries.BLOCK.getId(sidingBlock.fullBlock);
            Identifier sidingId = Id.of(blockId.getPath() + "_siding");
            Identifier mouldingId = Id.of(blockId.getPath() + "_moulding");
            Identifier cornerId = Id.of(blockId.getPath() + "_corner");
            Identifier columnId = Id.of(blockId.getPath() + "_column");
            Identifier pedestalId = Id.of(blockId.getPath() + "_pedestal");
            Identifier tableId = Id.of(blockId.getPath() + "_table");
            Registry.register(Registries.BLOCK, sidingId, sidingBlock);
            Registry.register(Registries.BLOCK, mouldingId, mouldingBlock);
            Registry.register(Registries.BLOCK, cornerId, cornerBlock);
            Registry.register(Registries.BLOCK, columnId, columnBlock);
            Registry.register(Registries.BLOCK, pedestalId, pedestalBlock);
            Registry.register(Registries.BLOCK, tableId, tableBlock);
            Registry.register(Registries.ITEM, sidingId, new BlockItem(sidingBlock, new Item.Settings()));
            Registry.register(Registries.ITEM, mouldingId, new BlockItem(mouldingBlock, new Item.Settings()));
            Registry.register(Registries.ITEM, cornerId, new BlockItem(cornerBlock, new Item.Settings()));
            Registry.register(Registries.ITEM, columnId, new BlockItem(columnBlock, new Item.Settings()));
            Registry.register(Registries.ITEM, pedestalId, new BlockItem(pedestalBlock, new Item.Settings()));
            Registry.register(Registries.ITEM, tableId, new BlockItem(tableBlock, new Item.Settings()));
        }
    }
}
