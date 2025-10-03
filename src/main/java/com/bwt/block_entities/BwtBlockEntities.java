package com.bwt.block_entities;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserBlockEntity;
import com.bwt.blocks.cauldron.CauldronBlockEntity;
import com.bwt.blocks.crucible.CrucibleBlockEntity;
import com.bwt.blocks.mech_hopper.MechHopperBlockEntity;
import com.bwt.blocks.mill_stone.MillStoneBlockEntity;
import com.bwt.blocks.pulley.PulleyBlockEntity;
import com.bwt.blocks.turntable.TurntableBlockEntity;
import com.bwt.blocks.unfired_pottery.UnfiredDecoratedPotBlockEntity;
import com.bwt.utils.Id;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BwtBlockEntities implements ModInitializer {
    public static final BlockEntityType<BlockDispenserBlockEntity> blockDispenserBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Id.of("block_dispenser_block_entity"),
            BlockEntityType.Builder.create(BlockDispenserBlockEntity::new, BwtBlocks.blockDispenserBlock).build()
    );
    public static final BlockEntityType<CauldronBlockEntity> cauldronBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Id.of("cauldron_block_entity"),
            BlockEntityType.Builder.create(CauldronBlockEntity::new, BwtBlocks.cauldronBlock).build()
    );
    public static final BlockEntityType<CrucibleBlockEntity> crucibleBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Id.of("crucible_block_entity"),
            BlockEntityType.Builder.create(CrucibleBlockEntity::new, BwtBlocks.crucibleBlock).build()
    );
    public static final BlockEntityType<MillStoneBlockEntity> millStoneBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Id.of("mill_stone_block_entity"),
            BlockEntityType.Builder.create(MillStoneBlockEntity::new, BwtBlocks.millStoneBlock).build()
    );
    public static final BlockEntityType<PulleyBlockEntity> pulleyBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Id.of("pulley_block_entity"),
            BlockEntityType.Builder.create(PulleyBlockEntity::new, BwtBlocks.pulleyBlock).build()
    );
    public static final BlockEntityType<MechHopperBlockEntity> mechHopperBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Id.of("mech_hopper_block_entity"),
            BlockEntityType.Builder.create(MechHopperBlockEntity::new, BwtBlocks.hopperBlock).build()
    );
    public static final BlockEntityType<TurntableBlockEntity> turntableBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Id.of("turntable_block_entity"),
            BlockEntityType.Builder.create(TurntableBlockEntity::new, BwtBlocks.turntableBlock).build()
    );
    public static final BlockEntityType<UnfiredDecoratedPotBlockEntity> unfiredDecoratedPotBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Id.of("unfired_decorated_pot_block_entity"),
            BlockEntityType.Builder.create(UnfiredDecoratedPotBlockEntity::new, BwtBlocks.unfiredDecoratedPotBlockWithSherds).build()
    );

    @Override
    public void onInitialize() {

    }
}
