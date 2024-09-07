package com.bwt.recipes.mill_stone;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.IngredientWithCount;
import com.bwt.blocks.mill_stone.MillStoneBlockEntity;
import com.bwt.generation.EmiDefaultsGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class MillStoneRecipe implements Recipe<MillStoneRecipeInput> {
    protected final String group;
    protected final CraftingRecipeCategory category;
    final DefaultedList<IngredientWithCount> ingredients;
    protected final DefaultedList<ItemStack> results;

    public MillStoneRecipe(String group, CraftingRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
        this.group = group;
        this.category = category;
        this.ingredients = DefaultedList.copyOf(IngredientWithCount.EMPTY, ingredients.toArray(new IngredientWithCount[0]));
        this.results = DefaultedList.copyOf(ItemStack.EMPTY, results.toArray(new ItemStack[0]));
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(BwtBlocks.millStoneBlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.MILL_STONE_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(MillStoneRecipeInput input, World world) {
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
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.addAll(this.ingredients.stream().map(IngredientWithCount::toVanilla).toList());
        return defaultedList;
    }

    public DefaultedList<IngredientWithCount> getIngredientsWithCount() {
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

    public CraftingRecipeCategory getCategory() {
        return this.category;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return Recipe.super.isIgnoredInRecipeBook();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public ItemStack craft(MillStoneRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return getResult(lookup);
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup wrapperLookup) {
        return results.get(0);
    }

    public static class Serializer implements RecipeSerializer<MillStoneRecipe> {
        protected static final MapCodec<MillStoneRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingRecipeCategory.CODEC.fieldOf("category")
                                .orElse(CraftingRecipeCategory.MISC)
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
        public static final PacketCodec<RegistryByteBuf, MillStoneRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read
        );

        public Serializer() {
        }

        @Override
        public MapCodec<MillStoneRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, MillStoneRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        public static MillStoneRecipe read(RegistryByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
            int ingredientsSize = buf.readVarInt();
            DefaultedList<IngredientWithCount> ingredients = DefaultedList.ofSize(ingredientsSize, IngredientWithCount.EMPTY);
            ingredients.replaceAll(ignored -> IngredientWithCount.Serializer.read(buf));
            List<ItemStack> results = ItemStack.LIST_PACKET_CODEC.decode(buf);
            return new MillStoneRecipe(group, category, ingredients, results);
        }

        public static void write(RegistryByteBuf buf, MillStoneRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            buf.writeVarInt(recipe.ingredients.size());
            for (IngredientWithCount ingredient : recipe.ingredients) {
                IngredientWithCount.Serializer.write(buf, ingredient);
            }
            ItemStack.LIST_PACKET_CODEC.encode(buf, recipe.getResults());
        }
    }

    public static class JsonBuilder implements CraftingRecipeJsonBuilder {
        protected CraftingRecipeCategory category = CraftingRecipeCategory.MISC;
        protected DefaultedList<IngredientWithCount> ingredients = DefaultedList.of();
        protected DefaultedList<ItemStack> results = DefaultedList.of();
        protected final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();
        @Nullable
        protected String group;

        public static JsonBuilder create() {
            return new JsonBuilder();
        }

        public JsonBuilder category(CraftingRecipeCategory category) {
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
            this.criterion(RecipeProvider.hasItem(itemStack.getItem()), RecipeProvider.conditionsFromItem(itemStack.getItem()));
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
        public JsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
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
        public void addToDefaults(Identifier recipeId) {
            if(this.isDefaultRecipe) {
                EmiDefaultsGenerator.addBwtRecipe(recipeId);
            }
        }

        @Override
        public Item getOutputItem() {
            return results.get(0).getItem();
        }

        @Override
        public void offerTo(RecipeExporter exporter) {
            this.offerTo(exporter,
                    RecipeProvider.getItemPath(results.get(0).getItem())
                    + "_from_milling_"
                    + RecipeProvider.getItemPath(this.ingredients.get(0).getMatchingStacks().get(0).getItem())
            );
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            this.validate(recipeId);
            this.addToDefaults(recipeId);
            Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            this.criteria.forEach(advancementBuilder::criterion);
            MillStoneRecipe millStoneRecipe = new MillStoneRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredients,
                    this.results
            );
            exporter.accept(recipeId, millStoneRecipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/" + this.category.asString() + "/")));
        }

        private void validate(Identifier recipeId) {
            if (this.criteria.isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + recipeId);
            }
        }
    }
}
