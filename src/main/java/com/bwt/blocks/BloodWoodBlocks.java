package com.bwt.blocks;

import com.bwt.features.BwtConfiguredFeatures;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HangingSignItem;
import net.minecraft.item.Item;
import net.minecraft.item.SignItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.Direction;

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
    public Block signBlock;
    public Block wallSignBlock;
    public Block hangingSignBlock;
    public Block wallHangingSignBlock;
    public Block slabBlock;
    public Block stairsBlock;
    public Block doorBlock;
    public Block trapdoorBlock;


    public BloodWoodBlocks initialize() {
        blockSetType = BlockSetTypeBuilder.copyOf(BlockSetType.CRIMSON).register(Id.of("blood_wood"));
        woodType = WoodTypeBuilder.copyOf(WoodType.CRIMSON).register(Id.of("blood_wood"), blockSetType);

        logBlock = new BloodWoodLogBlock(AbstractBlock.Settings.create().mapColor(state -> state.get(BloodWoodLogBlock.AXIS) == Direction.Axis.Y ? MapColor.DARK_CRIMSON : MapColor.OFF_WHITE).instrument(NoteBlockInstrument.BASS).strength(2.0f).sounds(BlockSoundGroup.NETHER_STEM).burnable());
        strippedLogBlock = Blocks.createLogBlock(MapColor.DARK_CRIMSON, MapColor.OFF_WHITE, BlockSoundGroup.NETHER_STEM);
        woodBlock = new PillarBlock(AbstractBlock.Settings.copy(Blocks.CRIMSON_HYPHAE));
        strippedWoodBlock = new PillarBlock(AbstractBlock.Settings.copy(Blocks.CRIMSON_HYPHAE));

        leavesBlock = new BloodWoodLeavesBlock(
                AbstractBlock.Settings.create()
                        .mapColor(MapColor.DARK_GREEN)
                        .strength(0.2F)
                        .ticksRandomly()
                        .sounds(BlockSoundGroup.GRASS)
                        .nonOpaque()
                        .allowsSpawning(Blocks::canSpawnOnLeaves)
                        .suffocates(Blocks::never)
                        .blockVision(Blocks::never)
                        .burnable()
                        .pistonBehavior(PistonBehavior.DESTROY)
                        .solidBlock(Blocks::never)
        );
        saplingBlock = new BloodWoodSaplingBlock(
                new SaplingGenerator(
                        "bwt:blood_wood",
                        Optional.empty(),
                        Optional.of(BwtConfiguredFeatures.BLOOD_WOOD_KEY),
                        Optional.empty()
                ),
                AbstractBlock.Settings.copy(Blocks.OAK_SAPLING).mapColor(MapColor.RED)
        );
        saplingItem = new BlockItem(saplingBlock, new Item.Settings());
        pottedSaplingBlock = Blocks.createFlowerPotBlock(saplingBlock);

        planksBlock = new Block(AbstractBlock.Settings.copy(Blocks.CRIMSON_PLANKS));
        buttonBlock = Blocks.createWoodenButtonBlock(blockSetType);
        fenceBlock = new FenceBlock(AbstractBlock.Settings.copy(Blocks.CRIMSON_FENCE));
        fenceGateBlock = new FenceGateBlock(woodType, AbstractBlock.Settings.copy(Blocks.CRIMSON_FENCE_GATE));
        pressurePlateBlock = new PressurePlateBlock(blockSetType, AbstractBlock.Settings.copy(Blocks.CRIMSON_PRESSURE_PLATE));
        signBlock = new SignBlock(woodType, AbstractBlock.Settings.copy(Blocks.CRIMSON_SIGN));
        wallSignBlock = new WallSignBlock(woodType, AbstractBlock.Settings.copy(Blocks.CRIMSON_WALL_SIGN));
        hangingSignBlock = new HangingSignBlock(woodType, AbstractBlock.Settings.copy(Blocks.CRIMSON_HANGING_SIGN));
        wallHangingSignBlock = new WallHangingSignBlock(woodType, AbstractBlock.Settings.copy(Blocks.CRIMSON_WALL_HANGING_SIGN));
        slabBlock = new SlabBlock(AbstractBlock.Settings.copy(Blocks.CRIMSON_SLAB));
        stairsBlock = new StairsBlock(planksBlock.getDefaultState(), AbstractBlock.Settings.copy(Blocks.CRIMSON_STAIRS));
        doorBlock = new DoorBlock(blockSetType, AbstractBlock.Settings.copy(Blocks.CRIMSON_DOOR));
        trapdoorBlock = new TrapdoorBlock(blockSetType, AbstractBlock.Settings.copy(Blocks.CRIMSON_TRAPDOOR));

        blockFamily = BlockFamilies.register(planksBlock)
                .button(buttonBlock)
                .fence(fenceBlock)
                .fenceGate(fenceGateBlock)
                .pressurePlate(pressurePlateBlock)
                .sign(signBlock, wallSignBlock)
                .slab(slabBlock)
                .stairs(stairsBlock)
                .door(doorBlock)
                .trapdoor(trapdoorBlock)
                .group("wooden")
                .unlockCriterionName("has_planks")
                .build();
        return this;
    }

    public void register() {
        Registry.register(Registries.BLOCK, Id.of("blood_wood_log"), logBlock);
        Registry.register(Registries.ITEM, Id.of("blood_wood_log"), new BlockItem(logBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("stripped_blood_wood_log"), strippedLogBlock);
        Registry.register(Registries.ITEM, Id.of("stripped_blood_wood_log"), new BlockItem(strippedLogBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("blood_wood"), woodBlock);
        Registry.register(Registries.ITEM, Id.of("blood_wood"), new BlockItem(woodBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("stripped_blood_wood"), strippedWoodBlock);
        Registry.register(Registries.ITEM, Id.of("stripped_blood_wood"), new BlockItem(strippedWoodBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("blood_wood_leaves"), leavesBlock);
        Registry.register(Registries.ITEM, Id.of("blood_wood_leaves"), new BlockItem(leavesBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("blood_wood_sapling"), saplingBlock);
        Registry.register(Registries.ITEM, Id.of("blood_wood_sapling"), saplingItem);
        Registry.register(Registries.BLOCK, Id.of("potted_blood_wood_sapling"), pottedSaplingBlock);
        Registry.register(Registries.BLOCK, Id.of("blood_wood_planks"), planksBlock);
        Registry.register(Registries.ITEM, Id.of("blood_wood_planks"), new BlockItem(planksBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("blood_wood_button"), buttonBlock);
        Registry.register(Registries.ITEM, Id.of("blood_wood_button"), new BlockItem(buttonBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("blood_wood_fence"), fenceBlock);
        Registry.register(Registries.ITEM, Id.of("blood_wood_fence"), new BlockItem(fenceBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("blood_wood_fence_gate"), fenceGateBlock);
        Registry.register(Registries.ITEM, Id.of("blood_wood_fence_gate"), new BlockItem(fenceGateBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("blood_wood_pressure_plate"), pressurePlateBlock);
        Registry.register(Registries.ITEM, Id.of("blood_wood_pressure_plate"), new BlockItem(pressurePlateBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("blood_wood_sign"), signBlock);
        Registry.register(Registries.BLOCK, Id.of("blood_wood_wall_sign"), wallSignBlock);
        Registry.register(Registries.BLOCK, Id.of("blood_wood_hanging_sign"), hangingSignBlock);
        Registry.register(Registries.BLOCK, Id.of("blood_wood_wall_hanging_sign"), wallHangingSignBlock);
        Registry.register(Registries.ITEM, Id.of("blood_wood_sign"), new SignItem(new Item.Settings().maxCount(16), signBlock, wallSignBlock));
        Registry.register(Registries.ITEM, Id.of("blood_wood_hanging_sign"), new HangingSignItem(hangingSignBlock, wallHangingSignBlock, new Item.Settings().maxCount(16)));
        Registry.register(Registries.BLOCK, Id.of("blood_wood_slab"), slabBlock);
        Registry.register(Registries.ITEM, Id.of("blood_wood_slab"), new BlockItem(slabBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("blood_wood_stairs"), stairsBlock);
        Registry.register(Registries.ITEM, Id.of("blood_wood_stairs"), new BlockItem(stairsBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("blood_wood_door"), doorBlock);
        Registry.register(Registries.ITEM, Id.of("blood_wood_door"), new BlockItem(doorBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("blood_wood_trapdoor"), trapdoorBlock);
        Registry.register(Registries.ITEM, Id.of("blood_wood_trapdoor"), new BlockItem(trapdoorBlock, new Item.Settings()));
    }
}
