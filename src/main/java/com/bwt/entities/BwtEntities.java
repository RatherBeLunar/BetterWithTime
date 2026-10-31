package com.bwt.entities;

import com.bwt.utils.Id;
import com.bwt.utils.RegistrationBase;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class BwtEntities extends RegistrationBase implements ModInitializer {
    public static final EntityType<WindmillEntity> windmillEntity = registerEntity(
            "windmill",
            EntityType.Builder.of(
                    (EntityType.EntityFactory<WindmillEntity>) WindmillEntity::new,
                    MobCategory.MISC
            ).clientTrackingRange(10)
    );
    public static final EntityType<WaterWheelEntity> waterWheelEntity = registerEntity(
            "water_wheel",
            EntityType.Builder.of(
                (EntityType.EntityFactory<WaterWheelEntity>) WaterWheelEntity::new,
                MobCategory.MISC
            ).clientTrackingRange(10)
    );
    public static final EntityType<MovingRopeEntity> movingRopeEntity = registerEntity(
            "moving_rope",
            EntityType.Builder.of(
                    (EntityType.EntityFactory<MovingRopeEntity>) MovingRopeEntity::new,
                    MobCategory.MISC
            )
            .sized(0.98f, 0.98f)
    );
    public static final EntityType<BroadheadArrowEntity> broadheadArrowEntity = registerEntity(
            "broadhead_arrow",
            EntityType.Builder.of(
                    (EntityType.EntityFactory<BroadheadArrowEntity>) BroadheadArrowEntity::new,
                    MobCategory.MISC
            )
            .sized(0.5f, 0.5f)
            .clientTrackingRange(4)
            .updateInterval(20)
    );
    public static final EntityType<RottedArrowEntity> rottedArrowEntity = registerEntity(
            "rotted_arrow",
            EntityType.Builder.of(
                    (EntityType.EntityFactory<RottedArrowEntity>) RottedArrowEntity::new,
                    MobCategory.MISC
            )
            .sized(0.5f, 0.5f)
            .clientTrackingRange(4)
            .updateInterval(20)
    );
    public static final EntityType<DynamiteEntity> dynamiteEntity = registerEntity(
            "dynamite",
            EntityType.Builder.of(
                    (EntityType.EntityFactory<DynamiteEntity>) DynamiteEntity::new,
                    MobCategory.MISC
            )
            .sized(0.25f, 0.40f)
            .clientTrackingRange(4)
            .updateInterval(20)
    );
    public static final EntityType<MiningChargeEntity> miningChargeEntity = registerEntity(
            "mining_charge",
            EntityType.Builder.of(
                    (EntityType.EntityFactory<MiningChargeEntity>) MiningChargeEntity::new,
                    MobCategory.MISC
            )
            .fireImmune()
            .sized(0.98f, 0.98f)
            .clientTrackingRange(10)
            .updateInterval(10)
    );
    public static final EntityType<SoulUrnProjectileEntity> soulUrnProjectileEntity = registerEntity(
            "soul_urn",
            EntityType.Builder.of(
                    (EntityType.EntityFactory<SoulUrnProjectileEntity>) SoulUrnProjectileEntity::new,
                    MobCategory.MISC
            )
            .sized(0.25f, 0.40f)
            .clientTrackingRange(6)
            .updateInterval(20)
    );
    public static final EntityType<CanvasEntity> canvasEntity = registerEntity(
            "canvas",
            EntityType.Builder.of(
                    (EntityType.EntityFactory<CanvasEntity>) CanvasEntity::new,
                    MobCategory.MISC
            )
            .noLootTable()
            .sized(0.5F, 0.5F)
            .clientTrackingRange(10)
            .updateInterval(Integer.MAX_VALUE)
    );

    @Override
    public void onInitialize() {
    }
}
