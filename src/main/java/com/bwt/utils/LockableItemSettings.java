package com.bwt.utils;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.world.item.equipment.Equippable;

import java.util.Objects;

public class LockableItemSettings extends Item.Properties {
    protected boolean locked = false;

    public LockableItemSettings lock() {
        this.locked = true;
        return this;
    }

    public LockableItemSettings food(FoodProperties foodProperties) {
        return this.food(foodProperties, Consumables.DEFAULT_FOOD);
    }

    @Override
    public LockableItemSettings food(FoodProperties foodComponent, Consumable consumable) {
        if (locked) return this;
        return (LockableItemSettings) super.food(foodComponent, consumable);
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

    public LockableItemSettings useCooldown(float f) {
        if (locked) return this;
        return (LockableItemSettings) super.useCooldown(f);
    }

    public LockableItemSettings enchantable(int i) {
        if (locked) return this;
        return (LockableItemSettings) super.enchantable(i);
    }

    public LockableItemSettings repairable(Item item) {
        if (locked) return this;
        return (LockableItemSettings) super.repairable(item);
    }

    public LockableItemSettings repairable(TagKey<Item> tagKey) {
        if (locked) return this;
        return (LockableItemSettings) super.repairable(tagKey);
    }

    public LockableItemSettings equippable(EquipmentSlot equipmentSlot) {
        if (locked) return this;
        return (LockableItemSettings) super.equippable(equipmentSlot);
    }

    public LockableItemSettings equippableUnswappable(EquipmentSlot equipmentSlot) {
        if (locked) return this;
        return (LockableItemSettings) super.equippableUnswappable(equipmentSlot);
    }

    public LockableItemSettings setId(ResourceKey<Item> resourceKey) {
        if (locked) return this;
        return (LockableItemSettings) super.setId(resourceKey);
    }

    public LockableItemSettings overrideDescription(String string) {
        if (locked) return this;
        return (LockableItemSettings) super.overrideDescription(string);
    }

    public LockableItemSettings useBlockDescriptionPrefix() {
        if (locked) return this;
        return (LockableItemSettings) super.useBlockDescriptionPrefix();
    }

    public LockableItemSettings useItemDescriptionPrefix() {
        if (locked) return this;
        return (LockableItemSettings) super.useItemDescriptionPrefix();
    }

    public LockableItemSettings overrideModel(ResourceLocation resourceLocation) {
        if (locked) return this;
        return (LockableItemSettings) super.overrideModel(resourceLocation);
    }

}
