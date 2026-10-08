package com.bwt.blocks;

import com.bwt.utils.Id;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import com.bwt.utils.RegistrationBase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
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

    public static void registerMaterialBlock(
            ArrayList<SidingBlock> sidingBlocks,
            ArrayList<MouldingBlock> mouldingBlocks,
            ArrayList<CornerBlock> cornerBlocks,
            ArrayList<ColumnBlock> columnBlocks,
            ArrayList<PedestalBlock> pedestalBlocks,
            ArrayList<TableBlock> tableBlocks,
            Block planksBlock,
            boolean wood
    ) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(planksBlock);
        String sidingId = blockId.getPath() + "_siding";
        String mouldingId = blockId.getPath() + "_moulding";
        String cornerId = blockId.getPath() + "_corner";
        String columnId = blockId.getPath() + "_column";
        String pedestalId = blockId.getPath() + "_pedestal";
        String tableId = blockId.getPath() + "_table";
        BlockBehaviour.Properties blockProperties = Properties.ofFullCopy(planksBlock);
        sidingBlocks.add(RegistrationBase.registerBlockWithItem(sidingId, properties -> SidingBlock.ofBlock(properties, planksBlock, wood), blockProperties));
        mouldingBlocks.add(RegistrationBase.registerBlockWithItem(mouldingId, properties -> MouldingBlock.ofBlock(properties, planksBlock, wood), blockProperties));
        cornerBlocks.add(RegistrationBase.registerBlockWithItem(cornerId, properties -> CornerBlock.ofBlock(properties, planksBlock, wood), blockProperties));
        columnBlocks.add(RegistrationBase.registerBlockWithItem(columnId, properties -> ColumnBlock.ofBlock(properties, planksBlock, wood), blockProperties));
        pedestalBlocks.add(RegistrationBase.registerBlockWithItem(pedestalId, properties -> PedestalBlock.ofBlock(properties, planksBlock, wood), blockProperties));
        tableBlocks.add(RegistrationBase.registerBlockWithItem(tableId, properties -> TableBlock.ofBlock(properties, planksBlock, wood), blockProperties));
    }

    public static void registerMaterialBlocks(
            ArrayList<SidingBlock> sidingBlocks,
            ArrayList<MouldingBlock> mouldingBlocks,
            ArrayList<CornerBlock> cornerBlocks,
            ArrayList<ColumnBlock> columnBlocks,
            ArrayList<PedestalBlock> pedestalBlocks,
            ArrayList<TableBlock> tableBlocks
    ) {
        var woodTypeBlocks = Stream.concat(
            WoodType.values()
                    .map(woodType -> BuiltInRegistries.BLOCK.getOptional(Id.mc(woodType.name() + "_planks")))
                    .filter(Optional::isPresent)
                    .map(Optional::get),
            Stream.of(Blocks.BAMBOO_MOSAIC)
        ).map(block -> new Tuple<>(block, true));

        var nonWoodFamilyBlocks = Stream.of(
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
        ).map(BlockFamily::getBaseBlock).map(block -> new Tuple<>(block, false));

        Stream.concat(woodTypeBlocks, nonWoodFamilyBlocks).forEach(value -> {
            Block block = value.getA();
            boolean isWood = value.getB();

            registerMaterialBlock(
                    sidingBlocks,
                    mouldingBlocks,
                    cornerBlocks,
                    columnBlocks,
                    pedestalBlocks,
                    tableBlocks,
                    block,
                    isWood
            );
        });
    }
}
