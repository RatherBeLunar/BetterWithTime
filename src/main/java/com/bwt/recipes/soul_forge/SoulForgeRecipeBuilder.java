package com.bwt.recipes.soul_forge;

import com.bwt.utils.Id;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class SoulForgeRecipeBuilder implements RecipeBuilder {
    protected final HolderGetter<Item> items;
    protected final RecipeCategory category;
    protected final ItemStack result;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    protected String group;
    protected boolean showNotification = true;

    protected SoulForgeRecipeBuilder(HolderGetter<Item> holderGetter, RecipeCategory recipeCategory, ItemStack itemStack) {
        this.items = holderGetter;
        this.category = recipeCategory;
        this.result = itemStack;
    }

    public SoulForgeRecipeBuilder unlockedBy(String string, Criterion<?> criterion) {
        this.criteria.put(string, criterion);
        return this;
    }

    public SoulForgeRecipeBuilder group(@Nullable String string) {
        this.group = string;
        return this;
    }

    public SoulForgeRecipeBuilder showNotification(boolean bl) {
        this.showNotification = bl;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }

    protected void ensureValid(ResourceKey<Recipe<?>> resourceKey) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + resourceKey.location());
        }
    }

    @Override
    public void save(RecipeOutput recipeOutput, String recipePath) {
        this.save(recipeOutput, Id.of(recipePath));
    }

    public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {
        this.save(recipeOutput, ResourceKey.create(Registries.RECIPE, recipeId));
    }
}
