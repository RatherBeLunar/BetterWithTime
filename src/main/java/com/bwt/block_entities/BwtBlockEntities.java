package com.bwt.block_entities;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserBlockEntity;
import com.bwt.blocks.cauldron.CauldronBlockEntity;
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

    @Override
    public void onInitialize() {

    }
}
