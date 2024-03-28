package com.bwt.mixin.accessors;

import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(ShapelessRecipeJsonBuilder.class)
public interface ShapelessRecipeJsonBuilderAccessorMixin {
    @Accessor
    RecipeCategory getCategory();
    @Accessor
    Item getOutput();
    @Accessor
    int getCount();
    @Accessor
    DefaultedList<Ingredient> getInputs();
    @Accessor
    String getGroup();
    @Accessor
    Map<String, AdvancementCriterion<?>> getAdvancementBuilder();
    @Invoker("validate")
    void accessValidate(Identifier recipeId);
}
