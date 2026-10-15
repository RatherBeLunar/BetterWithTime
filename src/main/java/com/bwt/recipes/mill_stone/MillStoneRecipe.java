package com.bwt.recipes.mill_stone;

import com.bwt.generation.SharedRecipeProvider;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.IngredientsWithCountRecipe;
import com.bwt.recipes.IngredientWithCount;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MillStoneRecipe implements IngredientsWithCountRecipe<MillStoneRecipeInput> {
    protected final String group;
    protected final CraftingBookCategory category;
    final List<IngredientWithCount> ingredients;
    protected final List<ItemStack> results;

    public MillStoneRecipe(String group, CraftingBookCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
        this.group = group;
        this.category = category;
        this.ingredients = ingredients;
        this.results = results;
    }

    @Override
    public RecipeSerializer<MillStoneRecipe> getSerializer() {
        return BwtRecipes.MILL_STONE_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(MillStoneRecipeInput input, Level level) {
        for (IngredientWithCount ingredient : ingredients) {
            Optional<Integer> matchingCount = input.items().stream()
                    .filter(stack -> ingredient.ingredient().test(stack))
                    .map(ItemStack::getCount)
                    .reduce(Integer::sum);
            if (matchingCount.orElse(0) < ingredient.count()) {
                return false;
            }
        }
        return true;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> defaultedList = NonNullList.create();
        defaultedList.addAll(this.ingredients.stream().map(IngredientWithCount::toVanilla).toList());
        return defaultedList;
    }

    @Override
    public List<IngredientWithCount> getIngredientsWithCount() {
        return ingredients;
    }

    public List<ItemStack> getResults() {
        return results.stream().map(ItemStack::copy).collect(Collectors.toList());
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public RecipeType<MillStoneRecipe> getType() {
        return BwtRecipes.MILL_STONE_RECIPE_TYPE;
    }

    public CraftingBookCategory getCategory() {
        return this.category;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public ItemStack assemble(MillStoneRecipeInput input, HolderLookup.Provider lookup) {
        return results.getFirst();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return switch (this.category) {
            case BUILDING -> RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
            case REDSTONE -> RecipeBookCategories.CRAFTING_REDSTONE;
            case EQUIPMENT -> RecipeBookCategories.CRAFTING_EQUIPMENT;
            case MISC -> RecipeBookCategories.CRAFTING_MISC;
        };
    }

    public static class Serializer implements RecipeSerializer<MillStoneRecipe> {
        protected static final MapCodec<MillStoneRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingBookCategory.CODEC.fieldOf("category")
                                .orElse(CraftingBookCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        IngredientWithCount.Serializer.CODEC.codec()
                                .listOf()
                                .fieldOf("ingredients")
                                .forGetter(recipe -> recipe.ingredients),
                        ItemStack.CODEC
                                .listOf()
                                .fieldOf("results")
                                .forGetter(MillStoneRecipe::getResults)
                ).apply(instance, MillStoneRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, MillStoneRecipe> PACKET_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, MillStoneRecipe::group,
                CraftingBookCategory.STREAM_CODEC, MillStoneRecipe::getCategory,
                IngredientWithCount.Serializer.PACKET_CODEC.apply(ByteBufCodecs.list()), MillStoneRecipe::getIngredientsWithCount,
                ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), MillStoneRecipe::getResults,
                MillStoneRecipe::new
        );

        public Serializer() {
        }

        @Override
        public MapCodec<MillStoneRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MillStoneRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }

    public static class JsonBuilder implements RecipeBuilder {
        protected final SharedRecipeProvider recipeProvider;
        protected CraftingBookCategory category = CraftingBookCategory.MISC;
        protected final NonNullList<IngredientWithCount> ingredients = NonNullList.create();
        protected final NonNullList<ItemStack> results = NonNullList.create();
        protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
        @Nullable
        protected String group;

        protected JsonBuilder(SharedRecipeProvider recipeProvider) {
            this.recipeProvider = recipeProvider;
        }

        public static JsonBuilder create(SharedRecipeProvider recipeProvider) {
            return new JsonBuilder(recipeProvider);
        }

        public JsonBuilder category(CraftingBookCategory category) {
            this.category = category;
            return this;
        }

        public JsonBuilder ingredients(IngredientWithCount... ingredients) {
            for (IngredientWithCount ingredient : ingredients) {
                this.ingredient(ingredient);
            }
            return this;
        }

        public JsonBuilder ingredient(IngredientWithCount ingredient) {
            this.ingredients.add(ingredient);
            return this;
        }

        public JsonBuilder ingredient(ItemStack itemStack) {
            this.unlockedBy(RecipeProvider.getHasName(itemStack.getItem()), recipeProvider.has(itemStack.getItem()));
            return this.ingredient(IngredientWithCount.of(itemStack));
        }

        public JsonBuilder ingredient(Item item, int count) {
            return this.ingredient(new ItemStack(item, count));
        }

        public JsonBuilder ingredient(Item item) {
            return this.ingredient(item, 1);
        }

        public JsonBuilder results(ItemStack... itemStacks) {
            this.results.addAll(Arrays.asList(itemStacks));
            return this;
        }

        public JsonBuilder result(ItemStack itemStack) {
            this.results.add(itemStack);
            return this;
        }

        public JsonBuilder result(Item item, int count) {
            this.results.add(new ItemStack(item, count));
            return this;
        }

        public JsonBuilder result(Item item) {
            return this.result(item, 1);
        }

        @Override
        public JsonBuilder unlockedBy(String string, Criterion<?> advancementCriterion) {
            this.criteria.put(string, advancementCriterion);
            return this;
        }

        @Override
        public JsonBuilder group(@Nullable String string) {
            this.group = string;
            return this;
        }

        @Override
        public Item getResult() {
            return results.getFirst().getItem();
        }

        @Override
        public void save(RecipeOutput exporter) {
            this.save(exporter,
                    RecipeProvider.getItemName(results.getFirst().getItem())
                    + "_from_milling_"
                    + RecipeProvider.getItemName(this.ingredients.getFirst().getFirstMatchingItem())
            );
        }

        @Override
        public void save(RecipeOutput exporter, String recipePath) {
            this.save(exporter, Id.of(recipePath));
        }

        public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {
            this.save(recipeOutput, ResourceKey.create(Registries.RECIPE, recipeId));
        }

        @Override
        public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> resourceKey) {
            this.validate(resourceKey);
            Advancement.Builder advancementBuilder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey)).rewards(AdvancementRewards.Builder.recipe(resourceKey)).requirements(AdvancementRequirements.Strategy.OR);
            this.criteria.forEach(advancementBuilder::addCriterion);
            MillStoneRecipe millStoneRecipe = new MillStoneRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredients,
                    this.results
            );
            recipeOutput.accept(resourceKey, millStoneRecipe, advancementBuilder.build(resourceKey.location().withPrefix("recipes/" + this.category.getSerializedName() + "/")));
        }

        private void validate(ResourceKey<Recipe<?>> recipeId) {
            if (this.criteria.isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + recipeId.location());
            }
        }
    }
}
