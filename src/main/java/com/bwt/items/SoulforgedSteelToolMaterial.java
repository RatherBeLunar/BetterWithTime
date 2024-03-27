package com.bwt.items;

import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class SoulforgedSteelToolMaterial implements ToolMaterial {
    public static final SoulforgedSteelToolMaterial INSTANCE = new SoulforgedSteelToolMaterial();

    private SoulforgedSteelToolMaterial() {}

    @Override
    public int getDurability() {
        return 2250;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 9f;
    }

    @Override
    public float getAttackDamage() {
        return 4f;
    }

    @Override
    public int getMiningLevel() {
        return MiningLevels.NETHERITE;
    }

    @Override
    public int getEnchantability() {
        return 15;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(BwtItems.soulforgedSteelItem);
    }
}
