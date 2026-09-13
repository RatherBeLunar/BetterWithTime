package com.bwt.entities;

import com.bwt.utils.Id;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class BwtEntities implements ModInitializer {
    public static final EntityType<WindmillEntity> windmillEntity = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Id.of("windmill"),
            EntityType.Builder.of(
                    (EntityType.EntityFactory<WindmillEntity>) WindmillEntity::new,
                    MobCategory.MISC
            ).clientTrackingRange(10).build()
    );
    public static final EntityType<WaterWheelEntity> waterWheelEntity = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Id.of("water_wheel"),
            EntityType.Builder.of(
                (EntityType.EntityFactory<WaterWheelEntity>) WaterWheelEntity::new,
                MobCategory.MISC
            ).clientTrackingRange(10).build()
    );
    public static final EntityType<MovingRopeEntity> movingRopeEntity = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Id.of("moving_rope"),
            EntityType.Builder.of(
                    (EntityType.EntityFactory<MovingRopeEntity>) MovingRopeEntity::new,
                    MobCategory.MISC
            )
            .sized(0.98f, 0.98f)
            .build()
    );
    public static final EntityType<BroadheadArrowEntity> broadheadArrowEntity = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Id.of("broadhead_arrow"),
            EntityType.Builder.of(
                    (EntityType.EntityFactory<BroadheadArrowEntity>) BroadheadArrowEntity::new,
                    MobCategory.MISC
            )
            .sized(0.5f, 0.5f)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build()
    );
    public static final EntityType<RottedArrowEntity> rottedArrowEntity = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Id.of("rotted_arrow"),
            EntityType.Builder.of(
                    (EntityType.EntityFactory<RottedArrowEntity>) RottedArrowEntity::new,
                    MobCategory.MISC
            )
            .sized(0.5f, 0.5f)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build()
    );
    public static final EntityType<DynamiteEntity> dynamiteEntity = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Id.of("dynamite"),
            EntityType.Builder.of(
                    (EntityType.EntityFactory<DynamiteEntity>) DynamiteEntity::new,
                    MobCategory.MISC
            )
            .sized(0.25f, 0.40f)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build()
    );
    public static final EntityType<MiningChargeEntity> miningChargeEntity = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Id.of("mining_charge"),
            EntityType.Builder.of(
                    (EntityType.EntityFactory<MiningChargeEntity>) MiningChargeEntity::new,
                    MobCategory.MISC
            )
            .fireImmune()
            .sized(0.98f, 0.98f)
            .clientTrackingRange(10)
            .updateInterval(10)
            .build()
    );
    public static final EntityType<SoulUrnProjectileEntity> soulUrnProjectileEntity = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Id.of("soul_urn"),
            EntityType.Builder.of(
                    (EntityType.EntityFactory<SoulUrnProjectileEntity>) SoulUrnProjectileEntity::new,
                    MobCategory.MISC
            )
            .sized(0.25f, 0.40f)
            .clientTrackingRange(6)
            .updateInterval(20)
            .build()
    );
    public static final EntityType<CanvasEntity> canvasEntity = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            "canvas",
            EntityType.Builder.of(
                    (EntityType.EntityFactory<CanvasEntity>) CanvasEntity::new,
                    MobCategory.MISC
            )
            .sized(0.5F, 0.5F)
            .clientTrackingRange(10)
            .updateInterval(Integer.MAX_VALUE)
            .build()
    );

    @Override
    public void onInitialize() {
    }
}
