package com.bwt.recipes.cooking_pots;

import com.bwt.generation.SharedRecipeProvider;
import com.bwt.recipes.IngredientWithCount;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public abstract class AbstractCookingPotRecipe implements Recipe<CookingPotRecipeInput> {
    protected final AbstractCookingPotRecipeType<? extends AbstractCookingPotRecipe> type;
    protected final String group;
    protected final CookingPotRecipeCategory category;
    final List<IngredientWithCount> ingredients;
    protected final List<ItemStack> results;

    public AbstractCookingPotRecipe(AbstractCookingPotRecipeType<? extends AbstractCookingPotRecipe> type, String group, CookingPotRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
        this.type = type;
        this.group = group;
        this.category = category;
        this.ingredients = ingredients;
        this.results = results;
    }

    @Override
    public boolean matches(CookingPotRecipeInput input, Level level) {
        return ingredients.stream().allMatch(input::matches);
    }

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
    public RecipeType<? extends AbstractCookingPotRecipe> getType() {
        return this.type;
    }

    public CookingPotRecipeCategory getCategory() {
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
            case BLOCKS -> RecipeBookCategories.FURNACE_BLOCKS;
            case FOOD -> RecipeBookCategories.FURNACE_FOOD;
            case MISC, RECLAIM -> RecipeBookCategories.FURNACE_MISC;
        };
    }


    @Override
    public ItemStack assemble(CookingPotRecipeInput input, HolderLookup.Provider lookup) {
        return results.getFirst();
    }

    public static class Serializer implements RecipeSerializer<AbstractCookingPotRecipe> {
        public final MapCodec<AbstractCookingPotRecipe> CODEC;
        public final StreamCodec<RegistryFriendlyByteBuf, AbstractCookingPotRecipe> PACKET_CODEC;

        public Serializer(RecipeFactory<AbstractCookingPotRecipe> recipeFactory) {
            this.CODEC = RecordCodecBuilder.mapCodec(
                    instance->instance.group(
                            Codec.STRING.fieldOf("group")
                                    .forGetter(recipe -> recipe.group),
                            CookingPotRecipeCategory.CODEC.fieldOf("category")
                                    .orElse(CookingPotRecipeCategory.MISC)
                                    .forGetter(recipe -> recipe.category),
                            IngredientWithCount.Serializer.CODEC.codec()
                                    .listOf()
                                    .fieldOf("ingredients")
                                    .forGetter(recipe -> recipe.ingredients),
                            ItemStack.CODEC
                                    .listOf()
                                    .fieldOf("results")
                                    .forGetter(AbstractCookingPotRecipe::getResults)
                    ).apply(instance, recipeFactory::create)
            );
            this.PACKET_CODEC = StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, AbstractCookingPotRecipe::group,
                    CookingPotRecipeCategory.STREAM_CODEC, AbstractCookingPotRecipe::getCategory,
                    IngredientWithCount.Serializer.PACKET_CODEC.apply(ByteBufCodecs.list()), AbstractCookingPotRecipe::getIngredientsWithCount,
                    ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), AbstractCookingPotRecipe::getResults,
                    recipeFactory::create
            );
        }

        @Override
        public MapCodec<AbstractCookingPotRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AbstractCookingPotRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }

    public interface RecipeFactory<T extends AbstractCookingPotRecipe> {
        T create(String group, CookingPotRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results);
    }

    public abstract static class JsonBuilder<T extends AbstractCookingPotRecipe>
            implements RecipeBuilder {
        protected final SharedRecipeProvider recipeProvider;
        protected RecipeCategory category;
        protected CookingPotRecipeCategory cookingCategory;
        protected final NonNullList<IngredientWithCount> ingredients = NonNullList.create();
        protected final NonNullList<ItemStack> results = NonNullList.create();
        protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

        @Nullable
        protected String group;
        protected abstract RecipeFactory<T> getRecipeFactory();

        protected JsonBuilder(SharedRecipeProvider recipeProvider) {
            this.recipeProvider = recipeProvider;
        }

        public JsonBuilder<T> category(RecipeCategory category) {
            this.category = category;
            return this;
        }

        public JsonBuilder<T> cookingCategory(CookingPotRecipeCategory cookingCategory) {
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
            this.unlockedBy(RecipeProvider.getHasName(itemStack.getItem()), recipeProvider.has(itemStack.getItem()));
            return this.ingredient(IngredientWithCount.of(itemStack));
        }

        public JsonBuilder<T> ingredient(Item item, int count) {
            return this.ingredient(new ItemStack(item, count));
        }

        public JsonBuilder<T> ingredient(Item item) {
            return this.ingredient(item, 1);
        }

        public JsonBuilder<T> ingredient(TagKey<Item> itemTag, int count) {
            this.unlockedBy("has_" + itemTag.location().getPath(), recipeProvider.has(itemTag));
            return this.ingredient(recipeProvider.ingredientWithCountOf(itemTag, count));
        }

        public JsonBuilder<T> ingredient(TagKey<Item> itemTag) {
            return this.ingredient(itemTag, 1);
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
        public JsonBuilder<T> unlockedBy(String string, Criterion<?> advancementCriterion) {
            this.criteria.put(string, advancementCriterion);
            return this;
        }

        @Override
        public JsonBuilder<T> group(@Nullable String string) {
            this.group = string;
            return this;
        }

        @Override
        public Item getResult() {
            return results.getFirst().getItem();
        }

        @Override
        public void save(RecipeOutput recipeOutput, String recipePath) {
            this.save(recipeOutput, Id.of(recipePath));
        }

        public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {
            this.save(recipeOutput, ResourceKey.create(Registries.RECIPE, recipeId));
        }

        @Override
        public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> resourceKey) {
            this.validate(resourceKey);

            if (cookingCategory == null) {
                cookingCategory(getCookingPotRecipeCategory(results));
            }

            Advancement.Builder advancementBuilder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey)).rewards(AdvancementRewards.Builder.recipe(resourceKey)).requirements(AdvancementRequirements.Strategy.OR);
            this.criteria.forEach(advancementBuilder::addCriterion);
            AbstractCookingPotRecipe cookingPotRecipe = this.getRecipeFactory().create(
                    Objects.requireNonNullElse(this.group, ""),
                    this.cookingCategory,
                    this.ingredients,
                    this.results
            );
            recipeOutput.accept(resourceKey, cookingPotRecipe, advancementBuilder.build(resourceKey.location().withPrefix("recipes/" + this.category.getFolderName() + "/")));
        }

        private static CookingPotRecipeCategory getCookingPotRecipeCategory(NonNullList<ItemStack> results) {
            if (results.stream().anyMatch(result -> result.getItem() instanceof BlockItem)) {
                return CookingPotRecipeCategory.BLOCKS;
            }
            return CookingPotRecipeCategory.MISC;
        }

        private void validate(ResourceKey<Recipe<?>> recipeId) {
            if (this.criteria.isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + recipeId.location());
            }
        }

    }


    public enum CookingPotRecipeCategory implements StringRepresentable {
        FOOD(0, "food"),
        BLOCKS(1, "blocks"),
        MISC(2, "misc"),
        RECLAIM(3, "reclaim");

        private static final IntFunction<CookingPotRecipeCategory> BY_ID = ByIdMap.continuous(
                cookingPotRecipeCategory -> cookingPotRecipeCategory.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO
        );
        public static final Codec<CookingPotRecipeCategory> CODEC = StringRepresentable.fromEnum(CookingPotRecipeCategory::values);
        public static final StreamCodec<ByteBuf, CookingPotRecipeCategory> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, cookingPotRecipeCategory -> cookingPotRecipeCategory.id);
        private final int id;
        private final String name;

        CookingPotRecipeCategory(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getSerializedName() {
            return this.name;
        }
    }

}