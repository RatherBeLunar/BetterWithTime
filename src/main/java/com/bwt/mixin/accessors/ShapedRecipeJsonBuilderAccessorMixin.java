package com.bwt.mixin.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

@Mixin(ShapedRecipeBuilder.class)
public interface ShapedRecipeJsonBuilderAccessorMixin {
    @Accessor
    RecipeCategory getCategory();
    @Accessor
    Item getResult();
    @Accessor
    int getCount();
    @Accessor
    List<String> getRows();
    @Accessor
    Map<Character, Ingredient> getKey();
    @Accessor
    Map<String, Criterion<?>> getCriteria();
    @Accessor
    String getGroup();
    @Accessor
    boolean getShowNotification();
}
