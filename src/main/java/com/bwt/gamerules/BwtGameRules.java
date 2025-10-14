package com.bwt.gamerules;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class BwtGameRules implements ModInitializer {
    public static GameRules.Key<GameRules.IntRule> LENS_BEAM_RANGE;

    @Override
    public void onInitialize() {
        LENS_BEAM_RANGE = GameRuleRegistry.register(
                "lensBeamRange",
                GameRules.Category.MISC,
                GameRuleFactory.createIntRule(128)
        );
    }
}
