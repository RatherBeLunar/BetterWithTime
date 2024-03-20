package com.bwt.block_entities;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserBlockEntity;
import com.bwt.blocks.cauldron.CauldronBlockEntity;
import com.bwt.blocks.crucible.CrucibleBlockEntity;
import com.bwt.blocks.mech_hopper.MechHopperBlockEntity;
import com.bwt.blocks.mill_stone.MillStoneBlockEntity;
import com.bwt.blocks.pulley.PulleyBlockEntity;
import com.bwt.blocks.turntable.TurntableBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BwtBlockEntities implements ModInitializer {
    public static final BlockEntityType<BlockDispenserBlockEntity> blockDispenserBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("bwt", "block_dispenser_block_entity"),
            FabricBlockEntityTypeBuilder.create(BlockDispenserBlockEntity::new, BwtBlocks.blockDispenserBlock).build()
    );
    public static final BlockEntityType<CauldronBlockEntity> cauldronBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("bwt", "cauldron_block_entity"),
            FabricBlockEntityTypeBuilder.create(CauldronBlockEntity::new, BwtBlocks.cauldronBlock).build()
    );
    public static final BlockEntityType<CrucibleBlockEntity> crucibleBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("bwt", "crucible_block_entity"),
            FabricBlockEntityTypeBuilder.create(CrucibleBlockEntity::new, BwtBlocks.crucibleBlock).build()
    );
    public static final BlockEntityType<MillStoneBlockEntity> millStoneBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("bwt", "mill_stone_block_entity"),
            FabricBlockEntityTypeBuilder.create(MillStoneBlockEntity::new, BwtBlocks.millStoneBlock).build()
    );
    public static final BlockEntityType<PulleyBlockEntity> pulleyBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("bwt", "pulley_block_entity"),
            FabricBlockEntityTypeBuilder.create(PulleyBlockEntity::new, BwtBlocks.pulleyBlock).build()
    );
    public static final BlockEntityType<MechHopperBlockEntity> mechHopperBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("bwt", "mech_hopper_block_entity"),
            FabricBlockEntityTypeBuilder.create(MechHopperBlockEntity::new, BwtBlocks.hopperBlock).build()
    );
    public static final BlockEntityType<TurntableBlockEntity> turntableBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("bwt", "turntable_block_entity"),
            FabricBlockEntityTypeBuilder.create(TurntableBlockEntity::new, BwtBlocks.turntableBlock).build()
    );

    @Override
    public void onInitialize() {

    }
}
