package com.bwt.mixin.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

@Mixin(ShapelessRecipeBuilder.class)
public interface ShapelessRecipeJsonBuilderAccessorMixin {
    @Accessor
    RecipeCategory getCategory();
    @Accessor
    Item getResult();
    @Accessor
    int getCount();
    @Accessor
    NonNullList<Ingredient> getIngredients();
    @Accessor
    String getGroup();
    @Accessor
    Map<String, Criterion<?>> getCriteria();
    @Invoker("ensureValid")
    void accessValidate(ResourceLocation recipeId);
}
