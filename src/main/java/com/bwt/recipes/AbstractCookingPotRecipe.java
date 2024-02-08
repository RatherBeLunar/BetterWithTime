package com.bwt.recipes;

import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

public abstract class AbstractCookingPotRecipe<I extends AbstractCookingPotBlockEntity> implements Recipe<I> {
    protected final RecipeType<? extends AbstractCookingPotRecipe<I>> type;
    protected final String group;
    protected final CookingRecipeCategory category;
    final DefaultedList<IngredientWithCount> ingredients;
    protected final ItemStack result;

    public AbstractCookingPotRecipe(RecipeType<? extends AbstractCookingPotRecipe<I>> type, String group, CookingRecipeCategory category, DefaultedList<IngredientWithCount> ingredients, ItemStack result) {
        this.type = type;
        this.group = group;
        this.category = category;
        this.ingredients = ingredients;
        this.result = result;
    }

    @Override
    public boolean matches(I inventory, World world) {
        for (IngredientWithCount ingredient : ingredients) {
            if (!inventory.containsAny(ingredient::test)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack craft(I inventory, DynamicRegistryManager registryManager) {
        return this.result.copy();
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

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
        return this.result;
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


    public static class Serializer<R extends AbstractCookingPotRecipe<?>> implements RecipeSerializer<R> {
        private final AbstractCookingPotRecipe.RecipeFactory<R> recipeFactory;
        private final Codec<R> codec;

        public Serializer(R.RecipeFactory<R> recipeFactory) {
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
                                    .xmap(
                                            list -> DefaultedList.copyOf(IngredientWithCount.EMPTY, list.toArray(new IngredientWithCount[0])),
                                            x -> x
                                    )
                                    .forGetter(recipe -> recipe.ingredients),
                            ItemStack.RECIPE_RESULT_CODEC.fieldOf("result")
                                    .forGetter(recipe -> recipe.result)
                    ).apply(instance, recipeFactory::create)
            );
        }

        @Override
        public Codec<R> codec() {
            return codec;
        }

        @Override
        public R read(PacketByteBuf buf) {
            String group = buf.readString();
            CookingRecipeCategory category = buf.readEnumConstant(CookingRecipeCategory.class);
            int i = buf.readVarInt();
            DefaultedList<IngredientWithCount> ingredients = DefaultedList.ofSize(i, IngredientWithCount.EMPTY);
            ingredients.replaceAll(ignored -> IngredientWithCount.SERIALIZER.read(buf));
            ItemStack result = buf.readItemStack();
            return this.recipeFactory.create(group, category, ingredients, result);
        }

        @Override
        public void write(PacketByteBuf buf, AbstractCookingPotRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            buf.writeVarInt(recipe.ingredients.size());
            for (Object ingredient : recipe.ingredients) {
                IngredientWithCount.SERIALIZER.write(buf, ((IngredientWithCount) ingredient));
            }
            buf.writeItemStack(recipe.result);
        }
    }

    public interface RecipeFactory<T extends AbstractCookingPotRecipe<? extends Inventory>> {
        T create(String group, CookingRecipeCategory category, DefaultedList<IngredientWithCount> ingredients, ItemStack result);
    }
}