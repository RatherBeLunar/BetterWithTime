package com.bwt.recipes;

import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
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
        if (results.size() == 1) {
            return results.get(0);
        }
        throw new IllegalCallerException("Too many results. Use getResults instead");
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
}