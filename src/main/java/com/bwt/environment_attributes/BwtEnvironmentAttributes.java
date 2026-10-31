package com.bwt.environment_attributes;

import com.bwt.utils.Id;
import com.bwt.utils.WindmillBehavior;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.attribute.AttributeType;
import net.minecraft.world.attribute.AttributeTypes;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.level.levelgen.GenerationStep;

public class BwtEnvironmentAttributes implements ModInitializer {
    public static AttributeType<WindmillBehavior> WINDMILL_BEHAVIOR_ATTRIBUTE = AttributeType.ofNotInterpolated(WindmillBehavior.CODEC);
    public static EnvironmentAttribute<Boolean> BLOOD_WOOD_SPREADS = EnvironmentAttribute
            .builder(AttributeTypes.BOOLEAN)
            .defaultValue(false)
            .syncable()
            .build();
    public static EnvironmentAttribute<Boolean> NETHER_GROTH_SPREADS = EnvironmentAttribute
            .builder(AttributeTypes.BOOLEAN)
            .defaultValue(false)
            .syncable()
            .build();
    public static EnvironmentAttribute<WindmillBehavior> WINDMILL_BEHAVIOR = EnvironmentAttribute
            .builder(WINDMILL_BEHAVIOR_ATTRIBUTE)
            .defaultValue(WindmillBehavior.NO_WIND)
            .syncable()
            .build();

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.ATTRIBUTE_TYPE, Id.of("windmill_behavior"), WINDMILL_BEHAVIOR_ATTRIBUTE);
        Registry.register(BuiltInRegistries.ENVIRONMENT_ATTRIBUTE, Id.of("gameplay/blood_wood_spreads"), BLOOD_WOOD_SPREADS);
        Registry.register(BuiltInRegistries.ENVIRONMENT_ATTRIBUTE, Id.of("gameplay/nether_groth_spreads"), NETHER_GROTH_SPREADS);
        Registry.register(BuiltInRegistries.ENVIRONMENT_ATTRIBUTE, Id.of("gameplay/windmill_behavior"), WINDMILL_BEHAVIOR);

        BiomeModifications.create(Id.of("biome_features"))
                .add(ModificationPhase.ADDITIONS, BiomeSelectors.foundInOverworld(), context -> context.getAttributes().set(WINDMILL_BEHAVIOR, WindmillBehavior.NORMAL))
                .add(ModificationPhase.ADDITIONS, BiomeSelectors.foundInTheNether(), context -> context.getAttributes().set(WINDMILL_BEHAVIOR, WindmillBehavior.TOO_MUCH_WIND))
                .add(ModificationPhase.ADDITIONS, BiomeSelectors.foundInTheEnd(), context -> context.getAttributes().set(WINDMILL_BEHAVIOR, WindmillBehavior.NO_WIND));
    }
}
