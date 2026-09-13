package com.bwt.utils;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class LockableItemSettings extends Item.Properties {
    protected boolean locked = false;

    public LockableItemSettings lock() {
        this.locked = true;
        return this;
    }

    @Override
    public LockableItemSettings food(FoodProperties foodComponent) {
        if (locked) return this;
        return (LockableItemSettings) super.food(foodComponent);
    }

    @Override
    public LockableItemSettings stacksTo(int maxCount) {
        if (locked) return this;
        return (LockableItemSettings) super.stacksTo(maxCount);
    }

    @Override
    public LockableItemSettings durability(int maxDamage) {
        if (locked) return this;
        return (LockableItemSettings) super.durability(maxDamage);
    }

    @Override
    public LockableItemSettings craftRemainder(Item recipeRemainder) {
        if (locked) return this;
        return (LockableItemSettings) super.craftRemainder(recipeRemainder);
    }

    @Override
    public LockableItemSettings rarity(Rarity rarity) {
        if (locked) return this;
        return (LockableItemSettings) super.rarity(rarity);
    }

    @Override
    public LockableItemSettings fireResistant() {
        if (locked) return this;
        return (LockableItemSettings) super.fireResistant();
    }

    @Override
    public LockableItemSettings jukeboxPlayable(ResourceKey<JukeboxSong> songKey) {
        if (locked) return this;
        return (LockableItemSettings) super.jukeboxPlayable(songKey);
    }

    @Override
    public LockableItemSettings requiredFeatures(FeatureFlag... features) {
        if (locked) return this;
        return (LockableItemSettings) super.requiredFeatures(features);
    }

    @Override
    public <T> LockableItemSettings component(DataComponentType<T> type, T value) {
        if (locked) return this;
        return (LockableItemSettings) super.component(type, value);
    }

    @Override
    public LockableItemSettings attributes(ItemAttributeModifiers attributeModifiersComponent) {
        if (locked) return this;
        return (LockableItemSettings) super.attributes(attributeModifiersComponent);
    }
}
