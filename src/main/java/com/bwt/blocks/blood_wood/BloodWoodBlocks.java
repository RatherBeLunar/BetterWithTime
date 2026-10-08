package com.bwt.blocks.blood_wood;

import com.bwt.blocks.*;
import com.bwt.features.BwtConfiguredFeatures;
import com.bwt.utils.Id;
import com.bwt.utils.RegistrationBase;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;

import net.minecraft.core.Direction;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.*;
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

    public BloodWoodLogBlock logBlock;
    public RotatedPillarBlock strippedLogBlock;
    public RotatedPillarBlock woodBlock;
    public RotatedPillarBlock strippedWoodBlock;
    public BloodWoodLeavesBlock leavesBlock;
    public BloodWoodSaplingBlock saplingBlock;
    public FlowerPotBlock pottedSaplingBlock;

    public Block planksBlock;
    public ButtonBlock buttonBlock;
    public FenceBlock fenceBlock;
    public FenceGateBlock fenceGateBlock;
    public PressurePlateBlock pressurePlateBlock;
    public SlabBlock slabBlock;
    public StairBlock stairsBlock;
    public DoorBlock doorBlock;
    public TrapDoorBlock trapdoorBlock;

    public SidingBlock sidingBlock;
    public MouldingBlock mouldingBlock;
    public CornerBlock cornerBlock;
    public PedestalBlock pedestalBlock;
    public ColumnBlock columnBlock;
    public TableBlock tableBlock;


    public BloodWoodBlocks initialize() {
        blockSetType = BlockSetTypeBuilder.copyOf(BlockSetType.CRIMSON).register(Id.of("blood_wood"));
        woodType = WoodTypeBuilder.copyOf(WoodType.CRIMSON).register(Id.of("blood_wood"), blockSetType);

        logBlock = RegistrationBase.registerBlockWithItem(
                "blood_wood_log",
                BloodWoodLogBlock::new,
                BlockBehaviour.Properties.of()
                        .mapColor(state -> state.getValue(BloodWoodLogBlock.AXIS) == Direction.Axis.Y ? MapColor.CRIMSON_HYPHAE : MapColor.QUARTZ)
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(2.0f)
                        .sound(SoundType.STEM)
        );
        strippedLogBlock = RegistrationBase.registerBlockWithItem(
                "stripped_blood_wood_log",
                RotatedPillarBlock::new,
                Blocks.netherStemProperties(MapColor.QUARTZ)
        );
        woodBlock = RegistrationBase.registerBlockWithItem(
                "blood_wood_wood",
                RotatedPillarBlock::new,
                BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_HYPHAE)
                        .ignitedByLava()
        );
        strippedWoodBlock = RegistrationBase.registerBlockWithItem(
                "stripped_blood_wood",
                RotatedPillarBlock::new,
                BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_HYPHAE)
        );

        leavesBlock = RegistrationBase.registerBlockWithItem(
                "blood_wood_leaves",
                BloodWoodLeavesBlock::new,
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
        saplingBlock = RegistrationBase.registerBlockWithItem(
                "blood_wood_sapling",
                properties -> new BloodWoodSaplingBlock(
                    new TreeGrower(
                            Id.of("blood_wood").toString(),
                            Optional.empty(),
                            Optional.of(BwtConfiguredFeatures.BLOOD_WOOD_KEY),
                            Optional.empty()
                    ),
                    properties
                ),
                BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING).mapColor(MapColor.COLOR_RED)
        );
        pottedSaplingBlock = RegistrationBase.registerBlock(
                "potted_blood_wood_sapling",
                properties -> new FlowerPotBlock(saplingBlock, properties), Blocks.flowerPotProperties()
        );

        planksBlock = RegistrationBase.registerBlockWithItem(
                "blood_wood_planks",
                BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_PLANKS)
        );
        buttonBlock = RegistrationBase.registerBlockWithItem(
                "blood_wood_button",
                properties -> new ButtonBlock(blockSetType, 30, properties),
                Blocks.buttonProperties()
        );
        fenceBlock = RegistrationBase.registerBlockWithItem(
                "blood_wood_fence",
                FenceBlock::new,
                BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_FENCE)
        );
        fenceGateBlock = RegistrationBase.registerBlockWithItem(
                "blood_wood_fence_gate",
                properties -> new FenceGateBlock(woodType, properties),
                BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_FENCE_GATE)
        );
        pressurePlateBlock = RegistrationBase.registerBlockWithItem(
                "blood_wood_pressure_plate",
                properties -> new PressurePlateBlock(blockSetType, properties),
                BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_PRESSURE_PLATE)
        );
        slabBlock = RegistrationBase.registerBlockWithItem(
                "blood_wood_slab",
                SlabBlock::new,
                BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_SLAB)
        );
        stairsBlock = RegistrationBase.registerBlockWithItem(
                "blood_wood_stairs",
                properties -> new StairBlock(planksBlock.defaultBlockState(), properties),
                BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_STAIRS)
        );
        doorBlock = RegistrationBase.registerBlockWithItem(
                "blood_wood_door",
                properties -> new DoorBlock(blockSetType, properties),
                BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_DOOR)
        );
        trapdoorBlock = RegistrationBase.registerBlockWithItem(
                "blood_wood_trapdoor",
                properties -> new TrapDoorBlock(blockSetType, properties),
                BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_TRAPDOOR)
        );

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
}
