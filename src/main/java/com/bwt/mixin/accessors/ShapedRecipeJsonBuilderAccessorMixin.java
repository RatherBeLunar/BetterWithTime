package com.bwt.mixin.accessors;

import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(ShapedRecipeJsonBuilder.class)
public interface ShapedRecipeJsonBuilderAccessorMixin {
    @Accessor
    RecipeCategory getCategory();
    @Accessor
    Item getOutput();
    @Accessor
    int getCount();
    @Accessor
    List<String> getPattern();
    @Accessor
    Map<Character, Ingredient> getInputs();
    @Accessor
    Map<String, AdvancementCriterion<?>> getCriteria();
    @Accessor
    String getGroup();
    @Accessor
    boolean getShowNotification();
}
