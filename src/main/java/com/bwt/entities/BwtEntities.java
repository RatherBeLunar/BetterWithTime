package com.bwt.entities;

import com.bwt.entities.canvas.CanvasEntity;
import com.bwt.utils.Id;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BwtEntities implements ModInitializer {
    public static final EntityType<WindmillEntity> windmillEntity = Registry.register(
            Registries.ENTITY_TYPE,
            Id.of("windmill"),
            EntityType.Builder.create(
                    (EntityType.EntityFactory<WindmillEntity>) WindmillEntity::new,
                    SpawnGroup.MISC
            ).maxTrackingRange(10).build()
    );
    public static final EntityType<WaterWheelEntity> waterWheelEntity = Registry.register(
            Registries.ENTITY_TYPE,
            Id.of("water_wheel"),
            EntityType.Builder.create(
                (EntityType.EntityFactory<WaterWheelEntity>) WaterWheelEntity::new,
                SpawnGroup.MISC
            ).maxTrackingRange(10).build()
    );
    public static final EntityType<MovingRopeEntity> movingRopeEntity = Registry.register(
            Registries.ENTITY_TYPE,
            Id.of("moving_rope"),
            EntityType.Builder.create(
                    (EntityType.EntityFactory<MovingRopeEntity>) MovingRopeEntity::new,
                    SpawnGroup.MISC
            )
            .dimensions(0.98f, 0.98f)
            .build()
    );
    public static final EntityType<BroadheadArrowEntity> broadheadArrowEntity = Registry.register(
            Registries.ENTITY_TYPE,
            Id.of("broadhead_arrow"),
            EntityType.Builder.create(
                    (EntityType.EntityFactory<BroadheadArrowEntity>) BroadheadArrowEntity::new,
                    SpawnGroup.MISC
            )
            .dimensions(0.5f, 0.5f)
            .maxTrackingRange(4)
            .trackingTickInterval(20)
            .build()
    );
    public static final EntityType<RottedArrowEntity> rottedArrowEntity = Registry.register(
            Registries.ENTITY_TYPE,
            Id.of("rotted_arrow"),
            EntityType.Builder.create(
                    (EntityType.EntityFactory<RottedArrowEntity>) RottedArrowEntity::new,
                    SpawnGroup.MISC
            )
            .dimensions(0.5f, 0.5f)
            .maxTrackingRange(4)
            .trackingTickInterval(20)
            .build()
    );
    public static final EntityType<DynamiteEntity> dynamiteEntity = Registry.register(
            Registries.ENTITY_TYPE,
            Id.of("dynamite"),
            EntityType.Builder.create(
                    (EntityType.EntityFactory<DynamiteEntity>) DynamiteEntity::new,
                    SpawnGroup.MISC
            )
            .dimensions(0.25f, 0.40f)
            .maxTrackingRange(4)
            .trackingTickInterval(20)
            .build()
    );
    public static final EntityType<MiningChargeEntity> miningChargeEntity = Registry.register(
            Registries.ENTITY_TYPE,
            Id.of("mining_charge"),
            EntityType.Builder.create(
                    (EntityType.EntityFactory<MiningChargeEntity>) MiningChargeEntity::new,
                    SpawnGroup.MISC
            )
            .makeFireImmune()
            .dimensions(0.98f, 0.98f)
            .maxTrackingRange(10)
            .trackingTickInterval(10)
            .build()
    );
    public static final EntityType<SoulUrnProjectileEntity> soulUrnProjectileEntity = Registry.register(
            Registries.ENTITY_TYPE,
            Id.of("soul_urn"),
            EntityType.Builder.create(
                    (EntityType.EntityFactory<SoulUrnProjectileEntity>) SoulUrnProjectileEntity::new,
                    SpawnGroup.MISC
            )
            .dimensions(0.25f, 0.40f)
            .maxTrackingRange(6)
            .trackingTickInterval(20)
            .build()
    );
    public static final EntityType<CanvasEntity> canvasEntity = Registry.register(
            Registries.ENTITY_TYPE,
            "canvas",
            EntityType.Builder.create(
                    (EntityType.EntityFactory<CanvasEntity>) CanvasEntity::new,
                    SpawnGroup.MISC
            )
            .dimensions(0.5F, 0.5F)
            .maxTrackingRange(10)
            .trackingTickInterval(Integer.MAX_VALUE)
            .build()
    );

    @Override
    public void onInitialize() {
    }
}
