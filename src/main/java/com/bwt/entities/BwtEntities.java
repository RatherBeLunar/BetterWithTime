package com.bwt.entities;

import com.bwt.utils.rectangular_entity.EntityRectDimensions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BwtEntities implements ModInitializer {
    public static final EntityType<WindmillEntity> windmillEntity = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("bwt", "windmill"),
            FabricEntityTypeBuilder.create(
                SpawnGroup.MISC,
                (EntityType.EntityFactory<WindmillEntity>) WindmillEntity::new
            )
                .dimensions(EntityRectDimensions.fixed(WindmillEntity.width, WindmillEntity.height, WindmillEntity.length))
                .build()
    );
    public static final EntityType<WaterWheelEntity> waterWheelEntity = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("bwt", "water_wheel"),
            FabricEntityTypeBuilder.create(
                SpawnGroup.MISC,
                (EntityType.EntityFactory<WaterWheelEntity>) WaterWheelEntity::new
            )
                .dimensions(EntityRectDimensions.fixed(WaterWheelEntity.width, WaterWheelEntity.height, WaterWheelEntity.length))
                .build()
    );
    public static final EntityType<MovingAnchorEntity> movingAnchorEntity = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("bwt", "moving_anchor"),
            FabricEntityTypeBuilder.create(
                    SpawnGroup.MISC,
                    (EntityType.EntityFactory<MovingAnchorEntity>) MovingAnchorEntity::new
            )
            .dimensions(EntityDimensions.fixed(MovingAnchorEntity.width, MovingAnchorEntity.height))
            .build()
    );

    @Override
    public void onInitialize() {
    }
}
