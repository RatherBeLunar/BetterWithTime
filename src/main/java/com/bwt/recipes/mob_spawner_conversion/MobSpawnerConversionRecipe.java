package com.bwt.recipes.mob_spawner_conversion;

import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public record MobSpawnerConversionRecipe(String group, CraftingBookCategory category, BlockIngredient ingredient, Block result) implements Recipe<MobSpawnerConversionRecipeInput> {

    @Override
    public RecipeSerializer<MobSpawnerConversionRecipe> getSerializer() {
        return BwtRecipes.MOB_SPAWNER_CONVERSION_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(MobSpawnerConversionRecipeInput input, Level level) {
        return this.ingredient.test(input.block());
    }

    @Override
    public RecipeType<MobSpawnerConversionRecipe> getType() {
        return BwtRecipes.MOB_SPAWNER_CONVERSION_RECIPE_TYPE;
    }

    @Override
    public boolean isSpecial() {
        return Recipe.super.isSpecial();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public ItemStack assemble(MobSpawnerConversionRecipeInput input, HolderLookup.Provider lookup) {
        return result.asItem().getDefaultInstance();
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
            case BUILDING -> RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
            case REDSTONE -> RecipeBookCategories.CRAFTING_REDSTONE;
            case EQUIPMENT -> RecipeBookCategories.CRAFTING_EQUIPMENT;
            case MISC -> RecipeBookCategories.CRAFTING_MISC;
        };
    }

    public static class Serializer implements RecipeSerializer<MobSpawnerConversionRecipe> {
        protected static final MapCodec<MobSpawnerConversionRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingBookCategory.CODEC.fieldOf("category")
                                .orElse(CraftingBookCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        BlockIngredient.Serializer.CODEC
                                .fieldOf("ingredient")
                                .forGetter(recipe -> recipe.ingredient),
                        BuiltInRegistries.BLOCK.byNameCodec()
                                .fieldOf("result")
                                .forGetter(MobSpawnerConversionRecipe::result)
                ).apply(instance, MobSpawnerConversionRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, MobSpawnerConversionRecipe> PACKET_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, MobSpawnerConversionRecipe::group,
                CraftingBookCategory.STREAM_CODEC, MobSpawnerConversionRecipe::category,
                BlockIngredient.CONTENTS_STREAM_CODEC, MobSpawnerConversionRecipe::ingredient,
                ByteBufCodecs.fromCodecWithRegistries(BuiltInRegistries.BLOCK.byNameCodec()), MobSpawnerConversionRecipe::result,
                MobSpawnerConversionRecipe::new
        );

        public Serializer() {
        }

        @Override
        public MapCodec<MobSpawnerConversionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MobSpawnerConversionRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }

    public static class JsonBuilder implements RecipeBuilder {
        protected CraftingBookCategory category = CraftingBookCategory.MISC;
        protected BlockIngredient ingredient;
        protected Block result;
        protected String fromBlockName;
        @Nullable
        protected String group;

        public static JsonBuilder create(Block input) {
            JsonBuilder obj = new JsonBuilder();
            obj.ingredient = BlockIngredient.of(input);
            obj.fromBlockName = BuiltInRegistries.BLOCK.getKey(input).getPath();
            return obj;
        }

        public static JsonBuilder create(HolderGetter<Block> blocks, TagKey<Block> inputTag) {
            JsonBuilder obj = new JsonBuilder();
            obj.ingredient = BlockIngredient.of(blocks.getOrThrow(inputTag));
            obj.fromBlockName = inputTag.location().getPath();
            return obj;
        }

        public JsonBuilder category(CraftingBookCategory category) {
            this.category = category;
            return this;
        }

        public JsonBuilder convertsTo(Block block) {
            this.result = block;
            return this;
        }

        @Override
        public JsonBuilder unlockedBy(String string, Criterion<?> advancementCriterion) {
            return this;
        }

        @Override
        public JsonBuilder group(@Nullable String string) {
            this.group = string;
            return this;
        }

        @Override
        public Item getResult() {
            return ingredient.getMatchingItems().findFirst().map(Holder::value).orElse(Items.AIR);
        }

        @Override
        public void save(RecipeOutput exporter) {
            this.save(exporter, "mob_spawner_conversion_from_" + fromBlockName + "_to_" + BuiltInRegistries.BLOCK.getKey(result).getPath());
        }

        @Override
        public void save(RecipeOutput exporter, String recipePath) {
            this.save(exporter, Id.of(recipePath));
        }

        public void save(RecipeOutput recipeOutput, Identifier recipeId) {
            this.save(recipeOutput, ResourceKey.create(Registries.RECIPE, recipeId));
        }

        @Override
        public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> resourceKey) {
            MobSpawnerConversionRecipe mobSpawnerConversionRecipe = new MobSpawnerConversionRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredient,
                    this.result
            );
            recipeOutput.accept(resourceKey, mobSpawnerConversionRecipe, null);
        }
    }
}
