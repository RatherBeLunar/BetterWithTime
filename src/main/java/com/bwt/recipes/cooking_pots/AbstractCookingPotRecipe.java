package com.bwt.recipes.cooking_pots;

import com.bwt.recipes.IngredientWithCount;
import com.bwt.generation.EmiDefaultsGenerator;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractCookingPotRecipe implements Recipe<CookingPotRecipeInput> {
    protected final AbstractCookingPotRecipeType type;
    protected final String group;
    protected final CookingPotRecipeCategory category;
    final NonNullList<IngredientWithCount> ingredients;
    protected final NonNullList<ItemStack> results;

    public AbstractCookingPotRecipe(AbstractCookingPotRecipeType type, String group, CookingPotRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
        this.type = type;
        this.group = group;
        this.category = category;
        this.ingredients = NonNullList.of(IngredientWithCount.EMPTY, ingredients.toArray(new IngredientWithCount[0]));
        this.results = NonNullList.of(ItemStack.EMPTY, results.toArray(new ItemStack[0]));
    }

    @Override
    public boolean matches(CookingPotRecipeInput input, Level level) {
        return ingredients.stream().allMatch(input::matches);
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
    public ItemStack assemble(CookingPotRecipeInput input, HolderLookup.Provider lookup) {
        return getResultItem(lookup);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return results.get(0);
    }

    public static class Serializer implements RecipeSerializer<AbstractCookingPotRecipe> {
        private final RecipeFactory<AbstractCookingPotRecipe> recipeFactory;
        public final MapCodec<AbstractCookingPotRecipe> CODEC;
        public final StreamCodec<RegistryFriendlyByteBuf, AbstractCookingPotRecipe> PACKET_CODEC;

        public Serializer(RecipeFactory<AbstractCookingPotRecipe> recipeFactory) {
            this.recipeFactory = recipeFactory;
            this.CODEC = RecordCodecBuilder.mapCodec(
                    instance->instance.group(
                            Codec.STRING.fieldOf("group")
                                    .forGetter(recipe -> recipe.group),
                            CookingPotRecipeCategory.CODEC.fieldOf("category")
                                    .orElse(CookingPotRecipeCategory.MISC)
                                    .forGetter(recipe -> recipe.category),
                            IngredientWithCount.Serializer.DISALLOW_EMPTY_CODEC.codec()
                                    .listOf()
                                    .fieldOf("ingredients")
                                    .forGetter(recipe -> recipe.ingredients),
                            ItemStack.CODEC
                                    .listOf()
                                    .fieldOf("results")
                                    .forGetter(AbstractCookingPotRecipe::getResults)
                    ).apply(instance, recipeFactory::create)
            );
            this.PACKET_CODEC = StreamCodec.of(
                    this::write, this::read
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

        protected AbstractCookingPotRecipe read(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            CookingPotRecipeCategory category = buf.readEnum(CookingPotRecipeCategory.class);
            int ingredientsSize = buf.readVarInt();
            NonNullList<IngredientWithCount> ingredients = NonNullList.withSize(ingredientsSize, IngredientWithCount.EMPTY);
            ingredients.replaceAll(ignored -> IngredientWithCount.Serializer.read(buf));
            List<ItemStack> results = ItemStack.LIST_STREAM_CODEC.decode(buf);
            return this.recipeFactory.create(group, category, ingredients, results);
        }

        protected void write(RegistryFriendlyByteBuf buf, AbstractCookingPotRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            buf.writeVarInt(recipe.ingredients.size());
            for (IngredientWithCount ingredient : recipe.ingredients) {
                IngredientWithCount.Serializer.write(buf, ingredient);
            }
            ItemStack.LIST_STREAM_CODEC.encode(buf, recipe.getResults());
        }
    }

    public interface RecipeFactory<T extends AbstractCookingPotRecipe> {
        T create(String group, CookingPotRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results);
    }

    public abstract static class JsonBuilder<T extends AbstractCookingPotRecipe>
            implements RecipeBuilder {
        protected RecipeCategory category;
        protected CookingPotRecipeCategory cookingCategory;
        protected final NonNullList<IngredientWithCount> ingredients = NonNullList.create();
        protected final NonNullList<ItemStack> results = NonNullList.create();
        protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

        @Nullable
        protected String group;
        protected abstract RecipeFactory<T> getRecipeFactory();

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
            this.unlockedBy(RecipeProvider.getHasName(itemStack.getItem()), RecipeProvider.has(itemStack.getItem()));
            return this.ingredient(IngredientWithCount.fromStack(itemStack));
        }

        public JsonBuilder<T> ingredient(Item item, int count) {
            return this.ingredient(new ItemStack(item, count));
        }

        public JsonBuilder<T> ingredient(Item item) {
            return this.ingredient(item, 1);
        }

        public JsonBuilder<T> ingredient(TagKey<Item> itemTag, int count) {
            this.unlockedBy("has_" + itemTag.location().getPath(), RecipeProvider.has(itemTag));
            return this.ingredient(IngredientWithCount.fromTag(itemTag, count));
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

        protected boolean isDefaultRecipe;
        public JsonBuilder<T> markDefault() {
            this.isDefaultRecipe = true;
            return this;
        }
        public void addToDefaults(ResourceLocation recipeId) {
            if (this.isDefaultRecipe) {
                EmiDefaultsGenerator.addBwtRecipe(recipeId.withPrefix("/"));
            }
        }

        @Override
        public Item getResult() {
            return results.get(0).getItem();
        }

        @Override
        public void save(RecipeOutput exporter, String recipePath) {
            this.save(exporter, Id.of(recipePath));
        }

        @Override
        public void save(RecipeOutput exporter, ResourceLocation recipeId) {
            this.validate(recipeId);
            this.addToDefaults(recipeId);

            if (cookingCategory == null) {
                cookingCategory(getCookingPotRecipeCategory(results));
            }

            Advancement.Builder advancementBuilder = exporter.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(AdvancementRequirements.Strategy.OR);
            this.criteria.forEach(advancementBuilder::addCriterion);
            AbstractCookingPotRecipe cookingPotRecipe = this.getRecipeFactory().create(
                    Objects.requireNonNullElse(this.group, ""),
                    this.cookingCategory,
                    this.ingredients,
                    this.results
            );
            exporter.accept(recipeId, cookingPotRecipe, advancementBuilder.build(recipeId.withPrefix("recipes/" + this.category.getFolderName() + "/")));
        }

        private static CookingPotRecipeCategory getCookingPotRecipeCategory(NonNullList<ItemStack> results) {
            if (results.stream().anyMatch(result -> result.getItem() instanceof BlockItem)) {
                return CookingPotRecipeCategory.BLOCKS;
            }
            return CookingPotRecipeCategory.MISC;
        }

        private void validate(ResourceLocation recipeId) {
            if (this.criteria.isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + recipeId);
            }
        }

    }


    public enum CookingPotRecipeCategory implements StringRepresentable {
        FOOD("food"),
        BLOCKS("blocks"),
        MISC("misc"),
        RECLAIM("reclaim");

        public static final StringRepresentable.EnumCodec<CookingPotRecipeCategory> CODEC = StringRepresentable.fromEnum(CookingPotRecipeCategory::values);
        private final String id;

        CookingPotRecipeCategory(final String id) {
            this.id = id;
        }

        public String getSerializedName() {
            return this.id;
        }
    }

}