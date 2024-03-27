package com.bwt.items;

import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class SoulforgedSteelMaterial implements ToolMaterial {
    public static final SoulforgedSteelMaterial INSTANCE = new SoulforgedSteelMaterial();

    private SoulforgedSteelMaterial() {}

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
