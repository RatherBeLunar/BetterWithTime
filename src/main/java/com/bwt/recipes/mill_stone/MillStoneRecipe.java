package com.bwt.recipes.mill_stone;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.IngredientWithCount;
import com.bwt.generation.EmiDefaultsGenerator;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class MillStoneRecipe implements Recipe<MillStoneRecipeInput> {
    protected final String group;
    protected final CraftingBookCategory category;
    final NonNullList<IngredientWithCount> ingredients;
    protected final NonNullList<ItemStack> results;

    public MillStoneRecipe(String group, CraftingBookCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
        this.group = group;
        this.category = category;
        this.ingredients = NonNullList.of(IngredientWithCount.EMPTY, ingredients.toArray(new IngredientWithCount[0]));
        this.results = NonNullList.of(ItemStack.EMPTY, results.toArray(new ItemStack[0]));
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(BwtBlocks.millStoneBlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
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

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> defaultedList = NonNullList.create();
        defaultedList.addAll(this.ingredients.stream().map(IngredientWithCount::toVanilla).toList());
        return defaultedList;
    }

    public NonNullList<IngredientWithCount> getIngredientsWithCount() {
        return ingredients;
    }

    public List<ItemStack> getResults() {
        return results.stream().map(ItemStack::copy).collect(Collectors.toList());
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeType<?> getType() {
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
        return getResultItem(lookup);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider wrapperLookup) {
        return results.get(0);
    }

    public static class Serializer implements RecipeSerializer<MillStoneRecipe> {
        protected static final MapCodec<MillStoneRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingBookCategory.CODEC.fieldOf("category")
                                .orElse(CraftingBookCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        IngredientWithCount.Serializer.DISALLOW_EMPTY_CODEC.codec()
                                .listOf()
                                .fieldOf("ingredients")
                                .forGetter(recipe -> recipe.ingredients),
                        ItemStack.CODEC
                                .listOf()
                                .fieldOf("results")
                                .forGetter(MillStoneRecipe::getResults)
                ).apply(instance, MillStoneRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, MillStoneRecipe> PACKET_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read
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

        public static MillStoneRecipe read(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            int ingredientsSize = buf.readVarInt();
            NonNullList<IngredientWithCount> ingredients = NonNullList.withSize(ingredientsSize, IngredientWithCount.EMPTY);
            ingredients.replaceAll(ignored -> IngredientWithCount.Serializer.read(buf));
            List<ItemStack> results = ItemStack.LIST_STREAM_CODEC.decode(buf);
            return new MillStoneRecipe(group, category, ingredients, results);
        }

        public static void write(RegistryFriendlyByteBuf buf, MillStoneRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            buf.writeVarInt(recipe.ingredients.size());
            for (IngredientWithCount ingredient : recipe.ingredients) {
                IngredientWithCount.Serializer.write(buf, ingredient);
            }
            ItemStack.LIST_STREAM_CODEC.encode(buf, recipe.getResults());
        }
    }

    public static class JsonBuilder implements RecipeBuilder {
        protected CraftingBookCategory category = CraftingBookCategory.MISC;
        protected final NonNullList<IngredientWithCount> ingredients = NonNullList.create();
        protected final NonNullList<ItemStack> results = NonNullList.create();
        protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
        @Nullable
        protected String group;

        public static JsonBuilder create() {
            return new JsonBuilder();
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
            this.unlockedBy(RecipeProvider.getHasName(itemStack.getItem()), RecipeProvider.has(itemStack.getItem()));
            return this.ingredient(IngredientWithCount.fromStack(itemStack));
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

        protected boolean isDefaultRecipe;
        public JsonBuilder markDefault() {
            this.isDefaultRecipe = true;
            return this;
        }
        public void addToDefaults(ResourceLocation recipeId) {
            if(this.isDefaultRecipe) {
                EmiDefaultsGenerator.addBwtRecipe(recipeId.withPrefix("/"));
            }
        }

        @Override
        public Item getResult() {
            return results.get(0).getItem();
        }

        @Override
        public void save(RecipeOutput exporter) {
            this.save(exporter,
                    RecipeProvider.getItemName(results.get(0).getItem())
                    + "_from_milling_"
                    + RecipeProvider.getItemName(this.ingredients.get(0).getMatchingStacks().get(0).getItem())
            );
        }

        @Override
        public void save(RecipeOutput exporter, String recipePath) {
            this.save(exporter, Id.of(recipePath));
        }

        @Override
        public void save(RecipeOutput exporter, ResourceLocation recipeId) {
            this.validate(recipeId);
            this.addToDefaults(recipeId);
            Advancement.Builder advancementBuilder = exporter.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(AdvancementRequirements.Strategy.OR);
            this.criteria.forEach(advancementBuilder::addCriterion);
            MillStoneRecipe millStoneRecipe = new MillStoneRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredients,
                    this.results
            );
            exporter.accept(recipeId, millStoneRecipe, advancementBuilder.build(recipeId.withPrefix("recipes/" + this.category.getSerializedName() + "/")));
        }

        private void validate(ResourceLocation recipeId) {
            if (this.criteria.isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + recipeId);
            }
        }
    }
}
