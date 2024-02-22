package com.bwt.recipes;

import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractCookingPotRecipe implements Recipe<AbstractCookingPotBlockEntity.Inventory> {
    protected final AbstractCookingPotRecipeType type;
    protected final String group;
    protected final CookingRecipeCategory category;
    final DefaultedList<IngredientWithCount> ingredients;
    protected final DefaultedList<ItemStack> results;

    public AbstractCookingPotRecipe(AbstractCookingPotRecipeType type, String group, CookingRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
        this.type = type;
        this.group = group;
        this.category = category;
        this.ingredients = DefaultedList.copyOf(IngredientWithCount.EMPTY, ingredients.toArray(new IngredientWithCount[0]));
        this.results = DefaultedList.copyOf(ItemStack.EMPTY, results.toArray(new ItemStack[0]));
    }

    @Override
    public boolean matches(AbstractCookingPotBlockEntity.Inventory inventory, World world) {
        for (IngredientWithCount ingredient : ingredients) {
            Optional<Integer> matchingCount = inventory.getHeldStacks().stream()
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
        return this.type;
    }

    public CookingRecipeCategory getCategory() {
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
    public ItemStack craft(AbstractCookingPotBlockEntity.Inventory inventory, DynamicRegistryManager registryManager) {
        return getResult(registryManager);
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
        return results.get(0);
    }


    public static class Serializer implements RecipeSerializer<AbstractCookingPotRecipe> {
        private final AbstractCookingPotRecipe.RecipeFactory<AbstractCookingPotRecipe> recipeFactory;
        private final Codec<AbstractCookingPotRecipe> codec;

        public Serializer(AbstractCookingPotRecipe.RecipeFactory<AbstractCookingPotRecipe> recipeFactory) {
            this.recipeFactory = recipeFactory;
            this.codec = RecordCodecBuilder.create(
                    instance->instance.group(
                            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "group", "")
                                    .forGetter(recipe -> recipe.group),
                            CookingRecipeCategory.CODEC.fieldOf("category")
                                    .orElse(CookingRecipeCategory.MISC)
                                    .forGetter(recipe -> recipe.category),
                            IngredientWithCount.Serializer.DISALLOW_EMPTY_CODEC
                                    .listOf()
                                    .fieldOf("ingredients")
                                    .forGetter(recipe -> recipe.ingredients),
                            ItemStack.RECIPE_RESULT_CODEC
                                    .listOf()
                                    .fieldOf("results")
                                    .forGetter(AbstractCookingPotRecipe::getResults)
                    ).apply(instance, recipeFactory::create)
            );
        }

        @Override
        public Codec<AbstractCookingPotRecipe> codec() {
            return codec;
        }

        @Override
        public AbstractCookingPotRecipe read(PacketByteBuf buf) {
            String group = buf.readString();
            CookingRecipeCategory category = buf.readEnumConstant(CookingRecipeCategory.class);
            int ingredientsSize = buf.readVarInt();
            DefaultedList<IngredientWithCount> ingredients = DefaultedList.ofSize(ingredientsSize, IngredientWithCount.EMPTY);
            ingredients.replaceAll(ignored -> IngredientWithCount.SERIALIZER.read(buf));
            int resultsSize = buf.readVarInt();
            DefaultedList<ItemStack> results = DefaultedList.ofSize(resultsSize, ItemStack.EMPTY);
            results.replaceAll(ignored -> buf.readItemStack());
            return this.recipeFactory.create(group, category, ingredients, results);
        }

        @Override
        public void write(PacketByteBuf buf, AbstractCookingPotRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            buf.writeVarInt(recipe.ingredients.size());
            for (IngredientWithCount ingredient : recipe.ingredients) {
                IngredientWithCount.SERIALIZER.write(buf, ingredient);
            }
            buf.writeVarInt(recipe.results.size());
            for (ItemStack stack : recipe.getResults()) {
                buf.writeItemStack(stack);
            }
        }
    }

    public interface RecipeFactory<T extends AbstractCookingPotRecipe> {
        T create(String group, CookingRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results);
    }

    public abstract static class JsonBuilder<T extends AbstractCookingPotRecipe>
            implements CraftingRecipeJsonBuilder {
        protected RecipeCategory category;
        protected CookingRecipeCategory cookingCategory;
        protected DefaultedList<IngredientWithCount> ingredients = DefaultedList.of();
        protected DefaultedList<ItemStack> results = DefaultedList.of();
        protected final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();
        @Nullable
        protected String group;
        abstract AbstractCookingPotRecipe.RecipeFactory<T> getRecipeFactory();

        public JsonBuilder<T> category(RecipeCategory category) {
            this.category = category;
            return this;
        }

        public JsonBuilder<T> cookingCategory(CookingRecipeCategory cookingCategory) {
            this.cookingCategory = cookingCategory;
            return this;
        }

        public JsonBuilder<T> ingredients(IngredientWithCount... ingredients) {
            for (IngredientWithCount ingredient : ingredients) {
                this.ingredient(ingredient);
            }
            return this;
        }

        public JsonBuilder<T> ingredient(IngredientWithCount ingredient) {
            this.ingredients.add(ingredient);
            return this;
        }

        public JsonBuilder<T> ingredient(ItemStack itemStack) {
            this.criterion(RecipeProvider.hasItem(itemStack.getItem()), RecipeProvider.conditionsFromItem(itemStack.getItem()));
            return this.ingredient(IngredientWithCount.fromStack(itemStack));
        }

        public JsonBuilder<T> ingredient(Item item, int count) {
            return this.ingredient(new ItemStack(item, count));
        }

        public JsonBuilder<T> ingredient(Item item) {
            return this.ingredient(item, 1);
        }

        public JsonBuilder<T> results(ItemStack... itemStacks) {
            this.results.addAll(Arrays.asList(itemStacks));
            return this;
        }

        public JsonBuilder<T> result(ItemStack itemStack) {
            this.results.add(itemStack);
            return this;
        }

        public JsonBuilder<T> result(Item item, int count) {
            this.results.add(new ItemStack(item, count));
            return this;
        }

        public JsonBuilder<T> result(Item item) {
            return this.result(item, 1);
        }

        @Override
        public JsonBuilder<T> criterion(String string, AdvancementCriterion<?> advancementCriterion) {
            this.criteria.put(string, advancementCriterion);
            return this;
        }

        @Override
        public JsonBuilder<T> group(@Nullable String string) {
            this.group = string;
            return this;
        }

        @Override
        public Item getOutputItem() {
            return results.get(0).getItem();
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            this.validate(recipeId);
            if (cookingCategory == null) {
                cookingCategory(getCookingRecipeCategory(results));
            }

            Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            this.criteria.forEach(advancementBuilder::criterion);
            AbstractCookingPotRecipe cookingPotRecipe = this.getRecipeFactory().create(
                    Objects.requireNonNullElse(this.group, ""),
                    this.cookingCategory,
                    this.ingredients,
                    this.results
            );
            exporter.accept(recipeId, cookingPotRecipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
        }

        private static CookingRecipeCategory getCookingRecipeCategory(DefaultedList<ItemStack> results) {
            if (results.stream().anyMatch(result -> result.getItem() instanceof BlockItem)) {
                return CookingRecipeCategory.BLOCKS;
            }
            return CookingRecipeCategory.MISC;
        }

        private void validate(Identifier recipeId) {
            if (this.criteria.isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + recipeId);
            }
        }
    }
}