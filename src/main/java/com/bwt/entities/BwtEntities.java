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
    public static final EntityType<MovingRopeEntity> movingRopeEntity = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("bwt", "moving_rope"),
            FabricEntityTypeBuilder.create(
                    SpawnGroup.MISC,
                    (EntityType.EntityFactory<MovingRopeEntity>) MovingRopeEntity::new
            )
            .dimensions(EntityDimensions.fixed(0.98f, 0.98f))
            .build()
    );
    public static final EntityType<BroadheadArrowEntity> broadheadArrowEntity = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("bwt", "broadhead_arrow"),
            FabricEntityTypeBuilder.create(
                    SpawnGroup.MISC,
                    (EntityType.EntityFactory<BroadheadArrowEntity>) BroadheadArrowEntity::new
            )
            .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
            .trackRangeBlocks(4)
            .trackedUpdateRate(20)
            .build()
    );

    @Override
    public void onInitialize() {
    }
}
