package com.bwt.recipes;

import com.bwt.mixin.accessors.ShapedRecipeJsonBuilderAccessorMixin;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class SoulForgeShapedRecipe extends ShapedRecipe {
    public SoulForgeShapedRecipe(String group, CraftingRecipeCategory category, RawShapedRecipe raw, ItemStack result, boolean showNotification) {
        super(group, category, raw, result, showNotification);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.SOUL_FORGE_SHAPED_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BwtRecipes.SOUL_FORGE_RECIPE_TYPE;
    }

    public static class Serializer extends ShapedRecipe.Serializer {
    }

    public static class JsonBuilder extends ShapedRecipeJsonBuilder {
        public JsonBuilder(RecipeCategory category, ItemConvertible output, int count) {
            super(category, output, count);
        }

        public static JsonBuilder create(RecipeCategory category, ItemConvertible output) {
            return JsonBuilder.create(category, output, 1);
        }

        public static JsonBuilder create(RecipeCategory category, ItemConvertible output, int count) {
            return new JsonBuilder(category, output, count);
        }

        private RawShapedRecipe validate(Identifier recipeId) {
            ShapedRecipeJsonBuilderAccessorMixin accessor = ((ShapedRecipeJsonBuilderAccessorMixin) this);
            if (accessor.getCriteria().isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + recipeId);
            }
            return RawShapedRecipe.create(accessor.getInputs(), accessor.getPattern());
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            ShapedRecipeJsonBuilderAccessorMixin accessor = ((ShapedRecipeJsonBuilderAccessorMixin) this);
            RawShapedRecipe rawShapedRecipe = validate(recipeId);
            Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            accessor.getCriteria().forEach(builder::criterion);
            SoulForgeShapedRecipe shapedRecipe = new SoulForgeShapedRecipe(Objects.requireNonNullElse(accessor.getGroup(), ""), CraftingRecipeJsonBuilder.toCraftingCategory(accessor.getCategory()), rawShapedRecipe, new ItemStack(accessor.getOutput(), accessor.getCount()), accessor.getShowNotification());
            exporter.accept(recipeId, shapedRecipe, builder.build(recipeId.withPrefixedPath("recipes/" + accessor.getCategory().getName() + "/")));
        }
    }
}

