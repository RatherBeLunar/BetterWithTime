package com.bwt.gamerules;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class BwtGameRules implements ModInitializer {
    public static GameRules.Key<GameRules.BooleanRule> VANILLA_HOPPERS_DISABLED;

    @Override
    public void onInitialize() {
        VANILLA_HOPPERS_DISABLED = GameRuleRegistry.register(
                "disableVanillaHopperTransfer",
                GameRules.Category.MISC,
                GameRuleFactory.createBooleanRule(true)
        );
    }
}
