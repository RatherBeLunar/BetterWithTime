package com.bwt.recipes.soul_forge;

import com.bwt.generation.EmiDefaultsGenerator;
import com.bwt.mixin.accessors.ShapelessRecipeJsonBuilderAccessorMixin;
import com.bwt.recipes.BwtRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;

public class SoulForgeShapelessRecipe extends ShapelessRecipe implements SoulForgeRecipe {

    public SoulForgeShapelessRecipe(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients) {
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
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.getIngredients().size();
    }

    public ItemStack getResult() {
        return getResultItem(null);
    }

    public static class Serializer implements RecipeSerializer<SoulForgeShapelessRecipe> {
        private static final MapCodec<SoulForgeShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(ShapelessRecipe::getGroup),
                        CraftingBookCategory.CODEC.fieldOf("category")
                                .orElse(CraftingBookCategory.MISC)
                                .forGetter(ShapelessRecipe::category),
                        ItemStack.STRICT_CODEC.fieldOf("result")
                                .forGetter(SoulForgeShapelessRecipe::getResult),
                        Ingredient.CODEC_NONEMPTY
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
                                    return DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients2));
                                }, DataResult::success)
                                .forGetter(ShapelessRecipe::getIngredients)
                ).apply(instance, SoulForgeShapelessRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SoulForgeShapelessRecipe> PACKET_CODEC = StreamCodec.of(
                SoulForgeShapelessRecipe.Serializer::write,
                SoulForgeShapelessRecipe.Serializer::read
        );

        @Override
        public MapCodec<SoulForgeShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SoulForgeShapelessRecipe> streamCodec() {
            return PACKET_CODEC;
        }

        private static SoulForgeShapelessRecipe read(RegistryFriendlyByteBuf buf) {
            String string = buf.readUtf();
            CraftingBookCategory craftingRecipeCategory = buf.readEnum(CraftingBookCategory.class);
            int i = buf.readVarInt();
            NonNullList<Ingredient> defaultedList = NonNullList.withSize(i, Ingredient.EMPTY);
            defaultedList.replaceAll(empty -> Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(buf);
            return new SoulForgeShapelessRecipe(string, craftingRecipeCategory, itemStack, defaultedList);
        }

        private static void write(RegistryFriendlyByteBuf buf, SoulForgeShapelessRecipe recipe) {
            buf.writeUtf(recipe.getGroup());
            buf.writeEnum(recipe.category());
            buf.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
            }
            ItemStack.STREAM_CODEC.encode(buf, recipe.getResult());
        }
    }

    public static class JsonBuilder extends ShapelessRecipeBuilder {
        public JsonBuilder(RecipeCategory category, ItemLike output, int count) {
            super(category, output, count);
        }

        public static JsonBuilder shapeless(RecipeCategory category, ItemLike output) {
            return JsonBuilder.shapeless(category, output, 1);
        }

        public static JsonBuilder shapeless(RecipeCategory category, ItemLike output, int count) {
            return new JsonBuilder(category, output, count);
        }

        protected boolean isDefaultRecipe;
        public JsonBuilder markDefault() {
            this.isDefaultRecipe = true;
            return this;
        }
        public void addToDefaults(ResourceLocation recipeId) {
            if (this.isDefaultRecipe) {
                EmiDefaultsGenerator.addBwtRecipe(recipeId.withPrefix("/"));
            }
        }

        @Override
        public void save(RecipeOutput exporter, ResourceLocation recipeId) {
            addToDefaults(recipeId);

            ShapelessRecipeJsonBuilderAccessorMixin accessor = (ShapelessRecipeJsonBuilderAccessorMixin) this;

            accessor.accessValidate(recipeId);
            Advancement.Builder builder = exporter.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(AdvancementRequirements.Strategy.OR);
            accessor.getCriteria().forEach(builder::addCriterion);
            SoulForgeShapelessRecipe shapelessRecipe = new SoulForgeShapelessRecipe(Objects.requireNonNullElse(accessor.getGroup(), ""), RecipeBuilder.determineBookCategory(accessor.getCategory()), new ItemStack(accessor.getResult(), accessor.getCount()), accessor.getIngredients());
            exporter.accept(recipeId, shapelessRecipe, builder.build(recipeId.withPrefix("recipes/" + accessor.getCategory().getFolderName() + "/")));
        }
    }
}

