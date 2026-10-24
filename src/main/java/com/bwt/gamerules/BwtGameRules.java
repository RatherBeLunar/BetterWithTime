package com.bwt.gamerules;

import com.bwt.utils.Id;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.gamerules.GameRule;

public class BwtGameRules implements ModInitializer {
    public static final GameRule<Integer> LENS_BEAM_RANGE = GameRuleBuilder.forInteger(128).build();

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.GAME_RULE, Id.of("lens_beam_range"), LENS_BEAM_RANGE);
    }
}
