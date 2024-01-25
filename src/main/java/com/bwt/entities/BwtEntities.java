package com.bwt.entities;

import com.bwt.blocks.BwtBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BwtEntities implements ModInitializer {
    public static final BlockEntityType<WindmillBlockEntity> windmillBlockEntity = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("tutorial", "windmill_block_entity"),
            FabricBlockEntityTypeBuilder.create(WindmillBlockEntity::new, BwtBlocks.axlePowerSourceBlock).build()
    );

    @Override
    public void onInitialize() {
    }
}
