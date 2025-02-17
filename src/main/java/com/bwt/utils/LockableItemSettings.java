package com.bwt.utils;

import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.util.Rarity;

public class LockableItemSettings extends Item.Settings {
    protected boolean locked = false;

    public LockableItemSettings lock() {
        this.locked = true;
        return this;
    }

    @Override
    public LockableItemSettings food(FoodComponent foodComponent) {
        if (locked) return this;
        return (LockableItemSettings) super.food(foodComponent);
    }

    @Override
    public LockableItemSettings maxCount(int maxCount) {
        if (locked) return this;
        return (LockableItemSettings) super.maxCount(maxCount);
    }

    @Override
    public LockableItemSettings maxDamage(int maxDamage) {
        if (locked) return this;
        return (LockableItemSettings) super.maxDamage(maxDamage);
    }

    @Override
    public LockableItemSettings recipeRemainder(Item recipeRemainder) {
        if (locked) return this;
        return (LockableItemSettings) super.recipeRemainder(recipeRemainder);
    }

    @Override
    public LockableItemSettings rarity(Rarity rarity) {
        if (locked) return this;
        return (LockableItemSettings) super.rarity(rarity);
    }

    @Override
    public LockableItemSettings fireproof() {
        if (locked) return this;
        return (LockableItemSettings) super.fireproof();
    }

    @Override
    public LockableItemSettings jukeboxPlayable(RegistryKey<JukeboxSong> songKey) {
        if (locked) return this;
        return (LockableItemSettings) super.jukeboxPlayable(songKey);
    }

    @Override
    public LockableItemSettings requires(FeatureFlag... features) {
        if (locked) return this;
        return (LockableItemSettings) super.requires(features);
    }

    @Override
    public <T> LockableItemSettings component(ComponentType<T> type, T value) {
        if (locked) return this;
        return (LockableItemSettings) super.component(type, value);
    }

    @Override
    public LockableItemSettings attributeModifiers(AttributeModifiersComponent attributeModifiersComponent) {
        if (locked) return this;
        return (LockableItemSettings) super.attributeModifiers(attributeModifiersComponent);
    }
}
