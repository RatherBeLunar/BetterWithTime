package com.bwt.recipes;

import com.bwt.mixin.accessors.ShapelessRecipeJsonBuilderAccessorMixin;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.Objects;

public class SoulForgeShapelessRecipe extends ShapelessRecipe {
    public SoulForgeShapelessRecipe(String group, CraftingRecipeCategory category, ItemStack result, DefaultedList<Ingredient> ingredients) {
        super(group, category, result, ingredients);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.SOUL_FORGE_SHAPELESS_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BwtRecipes.SOUL_FORGE_RECIPE_TYPE;
    }

    public static class Serializer extends ShapelessRecipe.Serializer {
    }

    public static class JsonBuilder extends ShapelessRecipeJsonBuilder {
        public JsonBuilder(RecipeCategory category, ItemConvertible output, int count) {
            super(category, output, count);
        }

        public static JsonBuilder create(RecipeCategory category, ItemConvertible output) {
            return JsonBuilder.create(category, output, 1);
        }

        public static JsonBuilder create(RecipeCategory category, ItemConvertible output, int count) {
            return new JsonBuilder(category, output, count);
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            ShapelessRecipeJsonBuilderAccessorMixin accessor = (ShapelessRecipeJsonBuilderAccessorMixin) this;

            accessor.accessValidate(recipeId);
            Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            accessor.getAdvancementBuilder().forEach(builder::criterion);
            ShapelessRecipe shapelessRecipe = new SoulForgeShapelessRecipe(Objects.requireNonNullElse(accessor.getGroup(), ""), CraftingRecipeJsonBuilder.toCraftingCategory(accessor.getCategory()), new ItemStack(accessor.getOutput(), accessor.getCount()), accessor.getInputs());
            exporter.accept(recipeId, shapelessRecipe, builder.build(recipeId.withPrefixedPath("recipes/" + accessor.getCategory().getName() + "/")));
        }
    }
}

