package com.bwt.block_entities;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserBlockEntity;
import com.bwt.blocks.cauldron.CauldronBlockEntity;
import com.bwt.blocks.crucible.CrucibleBlockEntity;
import com.bwt.blocks.mech_hopper.MechHopperBlockEntity;
import com.bwt.blocks.mill_stone.MillStoneBlockEntity;
import com.bwt.blocks.pulley.PulleyBlockEntity;
import com.bwt.blocks.soul_forge.SoulForgeBlockEntity;
import com.bwt.blocks.turntable.TurntableBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BwtBlockEntities implements ModInitializer {
    public static final BlockEntityType<BlockDispenserBlockEntity> blockDispenserBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("bwt", "block_dispenser_block_entity"),
            BlockEntityType.Builder.create(BlockDispenserBlockEntity::new, BwtBlocks.blockDispenserBlock).build()
    );
    public static final BlockEntityType<CauldronBlockEntity> cauldronBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("bwt", "cauldron_block_entity"),
            BlockEntityType.Builder.create(CauldronBlockEntity::new, BwtBlocks.cauldronBlock).build()
    );
    public static final BlockEntityType<CrucibleBlockEntity> crucibleBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("bwt", "crucible_block_entity"),
            BlockEntityType.Builder.create(CrucibleBlockEntity::new, BwtBlocks.crucibleBlock).build()
    );
    public static final BlockEntityType<MillStoneBlockEntity> millStoneBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("bwt", "mill_stone_block_entity"),
            BlockEntityType.Builder.create(MillStoneBlockEntity::new, BwtBlocks.millStoneBlock).build()
    );
    public static final BlockEntityType<PulleyBlockEntity> pulleyBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("bwt", "pulley_block_entity"),
            BlockEntityType.Builder.create(PulleyBlockEntity::new, BwtBlocks.pulleyBlock).build()
    );
    public static final BlockEntityType<MechHopperBlockEntity> mechHopperBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("bwt", "mech_hopper_block_entity"),
            BlockEntityType.Builder.create(MechHopperBlockEntity::new, BwtBlocks.hopperBlock).build()
    );
    public static final BlockEntityType<SoulForgeBlockEntity> soulForgeBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("bwt", "soul_forge_block_entity"),
            BlockEntityType.Builder.create(SoulForgeBlockEntity::new, BwtBlocks.soulForgeBlock).build()
    );
    public static final BlockEntityType<TurntableBlockEntity> turntableBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("bwt", "turntable_block_entity"),
            BlockEntityType.Builder.create(TurntableBlockEntity::new, BwtBlocks.turntableBlock).build()
    );

    @Override
    public void onInitialize() {

    }
}
