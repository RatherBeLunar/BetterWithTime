package com.bwt.recipes.soul_forge;

import com.bwt.mixin.accessors.ShapelessRecipeJsonBuilderAccessorMixin;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.SoulForgeRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.Objects;

public class SoulForgeShapelessRecipe extends ShapelessRecipe implements SoulForgeRecipe {

    public SoulForgeShapelessRecipe(String group, CraftingRecipeCategory category, ItemStack result, DefaultedList<Ingredient> ingredients) {
        super(group, category, result, ingredients);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.SOUL_FORGE_SHAPELESS_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BwtRecipes.SOUL_FORGE_RECIPE_TYPE;
    }

    @Override
    public boolean fits(int width, int height) {
        return width == 4 && height == 4;
    }

    public ItemStack getResult() {
        return getResult(null);
    }

    public static class Serializer implements RecipeSerializer<SoulForgeShapelessRecipe> {
        private static final MapCodec<SoulForgeShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(ShapelessRecipe::getGroup),
                        CraftingRecipeCategory.CODEC.fieldOf("category")
                                .orElse(CraftingRecipeCategory.MISC)
                                .forGetter(ShapelessRecipe::getCategory),
                        ItemStack.VALIDATED_CODEC.fieldOf("result")
                                .forGetter(SoulForgeShapelessRecipe::getResult),
                        Ingredient.DISALLOW_EMPTY_CODEC
                                .listOf()
                                .fieldOf("ingredients")
                                .flatXmap(ingredients -> {
                                    Ingredient[] ingredients2 = ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
                                    if (ingredients2.length == 0) {
                                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                                    }
                                    if (ingredients2.length > 16) {
                                        return DataResult.error(() -> "Too many ingredients for shapeless recipe");
                                    }
                                    return DataResult.success(DefaultedList.copyOf(Ingredient.EMPTY, ingredients2));
                                }, DataResult::success)
                                .forGetter(ShapelessRecipe::getIngredients)
                ).apply(instance, SoulForgeShapelessRecipe::new));
        public static final PacketCodec<RegistryByteBuf, SoulForgeShapelessRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                SoulForgeShapelessRecipe.Serializer::write,
                SoulForgeShapelessRecipe.Serializer::read
        );

        @Override
        public MapCodec<SoulForgeShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, SoulForgeShapelessRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static SoulForgeShapelessRecipe read(RegistryByteBuf buf) {
            String string = buf.readString();
            CraftingRecipeCategory craftingRecipeCategory = buf.readEnumConstant(CraftingRecipeCategory.class);
            int i = buf.readVarInt();
            DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);
            defaultedList.replaceAll(empty -> Ingredient.PACKET_CODEC.decode(buf));
            ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
            return new SoulForgeShapelessRecipe(string, craftingRecipeCategory, itemStack, defaultedList);
        }

        private static void write(RegistryByteBuf buf, SoulForgeShapelessRecipe recipe) {
            buf.writeString(recipe.getGroup());
            buf.writeEnumConstant(recipe.getCategory());
            buf.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                Ingredient.PACKET_CODEC.encode(buf, ingredient);
            }
            ItemStack.PACKET_CODEC.encode(buf, recipe.getResult());
        }
    }

    public static class JsonBuilder extends ShapelessRecipeJsonBuilder {
        public JsonBuilder(RecipeCategory category, ItemConvertible output, int count) {
            super(category, output, count);
        }

        public static JsonBuilder create(RecipeCategory category, ItemConvertible output) {
            return JsonBuilder.create(category, output, 1);
        }

        public static JsonBuilder create(RecipeCategory category, ItemConvertible output, int count) {
            return new JsonBuilder(category, output, count);
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            ShapelessRecipeJsonBuilderAccessorMixin accessor = (ShapelessRecipeJsonBuilderAccessorMixin) this;

            accessor.accessValidate(recipeId);
            Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            accessor.getAdvancementBuilder().forEach(builder::criterion);
            SoulForgeShapelessRecipe shapelessRecipe = new SoulForgeShapelessRecipe(Objects.requireNonNullElse(accessor.getGroup(), ""), CraftingRecipeJsonBuilder.toCraftingCategory(accessor.getCategory()), new ItemStack(accessor.getOutput(), accessor.getCount()), accessor.getInputs());
            exporter.accept(recipeId, shapelessRecipe, builder.build(recipeId.withPrefixedPath("recipes/" + accessor.getCategory().getName() + "/")));
        }
    }
}

