package com.bwt.blocks.blood_wood;

import com.bwt.blocks.*;
import com.bwt.features.BwtConfiguredFeatures;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;

import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.ArrayList;
import java.util.Optional;

public class BloodWoodBlocks {
    public BlockSetType blockSetType;
    public WoodType woodType;
    public BlockFamily blockFamily;

    public Block logBlock;
    public Block strippedLogBlock;
    public Block woodBlock;
    public Block strippedWoodBlock;
    public Block leavesBlock;
    public Block saplingBlock;
    public BlockItem saplingItem;
    public Block pottedSaplingBlock;

    public Block planksBlock;
    public Block buttonBlock;
    public Block fenceBlock;
    public Block fenceGateBlock;
    public Block pressurePlateBlock;
    public Block slabBlock;
    public Block stairsBlock;
    public Block doorBlock;
    public Block trapdoorBlock;

    public SidingBlock sidingBlock;
    public MouldingBlock mouldingBlock;
    public CornerBlock cornerBlock;
    public PedestalBlock pedestalBlock;
    public ColumnBlock columnBlock;
    public TableBlock tableBlock;


    public BloodWoodBlocks initialize() {
        blockSetType = BlockSetTypeBuilder.copyOf(BlockSetType.CRIMSON).register(Id.of("blood_wood"));
        woodType = WoodTypeBuilder.copyOf(WoodType.CRIMSON).register(Id.of("blood_wood"), blockSetType);

        logBlock = new BloodWoodLogBlock(BlockBehaviour.Properties.of().mapColor(state -> state.getValue(BloodWoodLogBlock.AXIS) == Direction.Axis.Y ? MapColor.CRIMSON_HYPHAE : MapColor.QUARTZ).instrument(NoteBlockInstrument.BASS).strength(2.0f).sound(SoundType.STEM).ignitedByLava());
        strippedLogBlock = Blocks.log(MapColor.CRIMSON_HYPHAE, MapColor.QUARTZ, SoundType.STEM);
        woodBlock = new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_HYPHAE));
        strippedWoodBlock = new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_HYPHAE));

        leavesBlock = new BloodWoodLeavesBlock(
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.PLANT)
                        .strength(0.2F)
                        .randomTicks()
                        .sound(SoundType.GRASS)
                        .noOcclusion()
                        .isValidSpawn(Blocks::ocelotOrParrot)
                        .isSuffocating(Blocks::never)
                        .isViewBlocking(Blocks::never)
                        .ignitedByLava()
                        .pushReaction(PushReaction.DESTROY)
                        .isRedstoneConductor(Blocks::never)
        );
        saplingBlock = new BloodWoodSaplingBlock(
                new TreeGrower(
                        Id.of("blood_wood").toString(),
                        Optional.empty(),
                        Optional.of(BwtConfiguredFeatures.BLOOD_WOOD_KEY),
                        Optional.empty()
                ),
                BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING).mapColor(MapColor.COLOR_RED)
        );
        saplingItem = new BlockItem(saplingBlock, new Item.Properties());
        pottedSaplingBlock = Blocks.flowerPot(saplingBlock);

        planksBlock = new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_PLANKS));
        buttonBlock = Blocks.woodenButton(blockSetType);
        fenceBlock = new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_FENCE));
        fenceGateBlock = new FenceGateBlock(woodType, BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_FENCE_GATE));
        pressurePlateBlock = new PressurePlateBlock(blockSetType, BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_PRESSURE_PLATE));
        slabBlock = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_SLAB));
        stairsBlock = new StairBlock(planksBlock.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_STAIRS));
        doorBlock = new DoorBlock(blockSetType, BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_DOOR));
        trapdoorBlock = new TrapDoorBlock(blockSetType, BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_TRAPDOOR));

        blockFamily = BlockFamilies.familyBuilder(planksBlock)
                .button(buttonBlock)
                .fence(fenceBlock)
                .fenceGate(fenceGateBlock)
                .pressurePlate(pressurePlateBlock)
                .slab(slabBlock)
                .stairs(stairsBlock)
                .door(doorBlock)
                .trapdoor(trapdoorBlock)
                .recipeGroupPrefix("wooden")
                .recipeUnlockedBy("has_planks")
                .getFamily();
        return this;
    }

    public void initializeMiniBlocks(
            ArrayList<SidingBlock> sidingBlocks,
            ArrayList<MouldingBlock> mouldingBlocks,
            ArrayList<CornerBlock> cornerBlocks,
            ArrayList<ColumnBlock> columnBlocks,
            ArrayList<PedestalBlock> pedestalBlocks,
            ArrayList<TableBlock> tableBlocks
    ) {
        this.sidingBlock = sidingBlocks.stream().filter(block -> block.fullBlock.equals(this.planksBlock)).findFirst().orElse(null);
        this.mouldingBlock = mouldingBlocks.stream().filter(block -> block.fullBlock.equals(this.planksBlock)).findFirst().orElse(null);
        this.cornerBlock = cornerBlocks.stream().filter(block -> block.fullBlock.equals(this.planksBlock)).findFirst().orElse(null);
        this.pedestalBlock = pedestalBlocks.stream().filter(block -> block.fullBlock.equals(this.planksBlock)).findFirst().orElse(null);
        this.columnBlock = columnBlocks.stream().filter(block -> block.fullBlock.equals(this.planksBlock)).findFirst().orElse(null);
        this.tableBlock = tableBlocks.stream().filter(block -> block.fullBlock.equals(this.planksBlock)).findFirst().orElse(null);
    }

    public void register() {
        Registry.register(BuiltInRegistries.BLOCK, Id.of("blood_wood_log"), logBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("blood_wood_log"), new BlockItem(logBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("stripped_blood_wood_log"), strippedLogBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("stripped_blood_wood_log"), new BlockItem(strippedLogBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("blood_wood_wood"), woodBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("blood_wood_wood"), new BlockItem(woodBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("stripped_blood_wood"), strippedWoodBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("stripped_blood_wood"), new BlockItem(strippedWoodBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("blood_wood_leaves"), leavesBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("blood_wood_leaves"), new BlockItem(leavesBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("blood_wood_sapling"), saplingBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("blood_wood_sapling"), saplingItem);
        Registry.register(BuiltInRegistries.BLOCK, Id.of("potted_blood_wood_sapling"), pottedSaplingBlock);
        Registry.register(BuiltInRegistries.BLOCK, Id.of("blood_wood_planks"), planksBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("blood_wood_planks"), new BlockItem(planksBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("blood_wood_button"), buttonBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("blood_wood_button"), new BlockItem(buttonBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("blood_wood_fence"), fenceBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("blood_wood_fence"), new BlockItem(fenceBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("blood_wood_fence_gate"), fenceGateBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("blood_wood_fence_gate"), new BlockItem(fenceGateBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("blood_wood_pressure_plate"), pressurePlateBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("blood_wood_pressure_plate"), new BlockItem(pressurePlateBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("blood_wood_slab"), slabBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("blood_wood_slab"), new BlockItem(slabBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("blood_wood_stairs"), stairsBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("blood_wood_stairs"), new BlockItem(stairsBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("blood_wood_door"), doorBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("blood_wood_door"), new BlockItem(doorBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("blood_wood_trapdoor"), trapdoorBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("blood_wood_trapdoor"), new BlockItem(trapdoorBlock, new Item.Properties()));
    }
}
