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
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BwtBlockEntities implements ModInitializer {
    public static final BlockEntityType<BlockDispenserBlockEntity> blockDispenserBlockEntity = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Id.of("block_dispenser_block_entity"),
            BlockEntityType.Builder.of(BlockDispenserBlockEntity::new, BwtBlocks.blockDispenserBlock).build()
    );
    public static final BlockEntityType<CauldronBlockEntity> cauldronBlockEntity = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Id.of("cauldron_block_entity"),
            BlockEntityType.Builder.of(CauldronBlockEntity::new, BwtBlocks.cauldronBlock).build()
    );
    public static final BlockEntityType<CrucibleBlockEntity> crucibleBlockEntity = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Id.of("crucible_block_entity"),
            BlockEntityType.Builder.of(CrucibleBlockEntity::new, BwtBlocks.crucibleBlock).build()
    );
    public static final BlockEntityType<MillStoneBlockEntity> millStoneBlockEntity = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Id.of("mill_stone_block_entity"),
            BlockEntityType.Builder.of(MillStoneBlockEntity::new, BwtBlocks.millStoneBlock).build()
    );
    public static final BlockEntityType<PulleyBlockEntity> pulleyBlockEntity = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Id.of("pulley_block_entity"),
            BlockEntityType.Builder.of(PulleyBlockEntity::new, BwtBlocks.pulleyBlock).build()
    );
    public static final BlockEntityType<MechHopperBlockEntity> mechHopperBlockEntity = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Id.of("mech_hopper_block_entity"),
            BlockEntityType.Builder.of(MechHopperBlockEntity::new, BwtBlocks.hopperBlock).build()
    );
    public static final BlockEntityType<TurntableBlockEntity> turntableBlockEntity = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Id.of("turntable_block_entity"),
            BlockEntityType.Builder.of(TurntableBlockEntity::new, BwtBlocks.turntableBlock).build()
    );
    public static final BlockEntityType<UnfiredDecoratedPotBlockEntity> unfiredDecoratedPotBlockEntity = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Id.of("unfired_decorated_pot_block_entity"),
            BlockEntityType.Builder.of(UnfiredDecoratedPotBlockEntity::new, BwtBlocks.unfiredDecoratedPotBlockWithSherds).build()
    );

    @Override
    public void onInitialize() {

    }
}
