package com.bwt.recipes.cooking_pots;

import com.bwt.recipes.IngredientWithCount;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlockEntity;
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
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractCookingPotRecipe implements Recipe<CookingPotRecipeInput> {
    protected final AbstractCookingPotRecipeType type;
    protected final String group;
    protected final CookingPotRecipeCategory category;
    final DefaultedList<IngredientWithCount> ingredients;
    protected final DefaultedList<ItemStack> results;

    public AbstractCookingPotRecipe(AbstractCookingPotRecipeType type, String group, CookingPotRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
        this.type = type;
        this.group = group;
        this.category = category;
        this.ingredients = DefaultedList.copyOf(IngredientWithCount.EMPTY, ingredients.toArray(new IngredientWithCount[0]));
        this.results = DefaultedList.copyOf(ItemStack.EMPTY, results.toArray(new ItemStack[0]));
    }

    @Override
    public boolean matches(CookingPotRecipeInput input, World world) {
        return ingredients.stream().allMatch(input::matches);
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

    public CookingPotRecipeCategory getCategory() {
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
    public ItemStack craft(CookingPotRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return getResult(lookup);
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return results.get(0);
    }

    public static class Serializer implements RecipeSerializer<AbstractCookingPotRecipe> {
        private final RecipeFactory<AbstractCookingPotRecipe> recipeFactory;
        public final MapCodec<AbstractCookingPotRecipe> CODEC;
        public final PacketCodec<RegistryByteBuf, AbstractCookingPotRecipe> PACKET_CODEC;

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
            this.PACKET_CODEC = PacketCodec.ofStatic(
                    this::write, this::read
            );
        }

        @Override
        public MapCodec<AbstractCookingPotRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, AbstractCookingPotRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        protected AbstractCookingPotRecipe read(RegistryByteBuf buf) {
            String group = buf.readString();
            CookingPotRecipeCategory category = buf.readEnumConstant(CookingPotRecipeCategory.class);
            int ingredientsSize = buf.readVarInt();
            DefaultedList<IngredientWithCount> ingredients = DefaultedList.ofSize(ingredientsSize, IngredientWithCount.EMPTY);
            ingredients.replaceAll(ignored -> IngredientWithCount.Serializer.read(buf));
            List<ItemStack> results = ItemStack.LIST_PACKET_CODEC.decode(buf);
            return this.recipeFactory.create(group, category, ingredients, results);
        }

        protected void write(RegistryByteBuf buf, AbstractCookingPotRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            buf.writeVarInt(recipe.ingredients.size());
            for (IngredientWithCount ingredient : recipe.ingredients) {
                IngredientWithCount.Serializer.write(buf, ingredient);
            }
            ItemStack.LIST_PACKET_CODEC.encode(buf, recipe.getResults());
        }
    }

    public interface RecipeFactory<T extends AbstractCookingPotRecipe> {
        T create(String group, CookingPotRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results);
    }

    public abstract static class JsonBuilder<T extends AbstractCookingPotRecipe>
            implements CraftingRecipeJsonBuilder {
        protected RecipeCategory category;
        protected CookingPotRecipeCategory cookingCategory;
        protected DefaultedList<IngredientWithCount> ingredients = DefaultedList.of();
        protected DefaultedList<ItemStack> results = DefaultedList.of();
        protected final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

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
            this.criterion(RecipeProvider.hasItem(itemStack.getItem()), RecipeProvider.conditionsFromItem(itemStack.getItem()));
            return this.ingredient(IngredientWithCount.fromStack(itemStack));
        }

        public JsonBuilder<T> ingredient(Item item, int count) {
            return this.ingredient(new ItemStack(item, count));
        }

        public JsonBuilder<T> ingredient(Item item) {
            return this.ingredient(item, 1);
        }

        public JsonBuilder<T> ingredient(TagKey<Item> itemTag, int count) {
            this.criterion("has_" + itemTag.id().getPath(), RecipeProvider.conditionsFromTag(itemTag));
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
        public JsonBuilder<T> criterion(String string, AdvancementCriterion<?> advancementCriterion) {
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
        public void addToDefaults(Identifier recipeId) {
            if (this.isDefaultRecipe) {
                EmiDefaultsGenerator.addBwtRecipe(recipeId);
            }
        }

        @Override
        public Item getOutputItem() {
            return results.get(0).getItem();
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            this.validate(recipeId);
            this.addToDefaults(recipeId);

            if (cookingCategory == null) {
                cookingCategory(getCookingPotRecipeCategory(results));
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

        private static CookingPotRecipeCategory getCookingPotRecipeCategory(DefaultedList<ItemStack> results) {
            if (results.stream().anyMatch(result -> result.getItem() instanceof BlockItem)) {
                return CookingPotRecipeCategory.BLOCKS;
            }
            return CookingPotRecipeCategory.MISC;
        }

        private void validate(Identifier recipeId) {
            if (this.criteria.isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + recipeId);
            }
        }

    }


    public enum CookingPotRecipeCategory implements StringIdentifiable {
        FOOD("food"),
        BLOCKS("blocks"),
        MISC("misc"),
        RECLAIM("reclaim");

        public static final StringIdentifiable.EnumCodec<CookingPotRecipeCategory> CODEC = StringIdentifiable.createCodec(CookingPotRecipeCategory::values);
        private final String id;

        private CookingPotRecipeCategory(final String id) {
            this.id = id;
        }

        public String asString() {
            return this.id;
        }
    }

}