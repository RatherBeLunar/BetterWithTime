package com.bwt.recipes.soul_bottling;

import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public record SoulBottlingRecipe(String group, CraftingBookCategory category, BlockIngredient bottle, int soulCount, ItemStack result) implements Recipe<SoulBottlingRecipeInput> {
    @Override
    public RecipeSerializer<SoulBottlingRecipe> getSerializer() {
        return BwtRecipes.SOUL_BOTTLING_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(SoulBottlingRecipeInput input, Level level) {
        return bottle.test(input.block());
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public RecipeType<SoulBottlingRecipe> getType() {
        return BwtRecipes.SOUL_BOTTLING_RECIPE_TYPE;
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
    public ItemStack assemble(SoulBottlingRecipeInput input, HolderLookup.Provider lookup) {
        return getResult();
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

    public ItemStack getResult() {
        return result.copy();
    }

    public static class Serializer implements RecipeSerializer<SoulBottlingRecipe> {
        protected static final MapCodec<SoulBottlingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(SoulBottlingRecipe::group),
                        CraftingBookCategory.CODEC.fieldOf("category")
                                .orElse(CraftingBookCategory.MISC)
                                .forGetter(SoulBottlingRecipe::category),
                        BlockIngredient.Serializer.CODEC
                                .fieldOf("bottle")
                                .forGetter(SoulBottlingRecipe::bottle),
                        Codec.INT.fieldOf("soulCount")
                                .forGetter(SoulBottlingRecipe::soulCount),
                        ItemStack.OPTIONAL_CODEC
                                .fieldOf("result")
                                .forGetter(SoulBottlingRecipe::result)
                ).apply(instance, SoulBottlingRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SoulBottlingRecipe> PACKET_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, SoulBottlingRecipe::group,
                CraftingBookCategory.STREAM_CODEC, SoulBottlingRecipe::category,
                BlockIngredient.CONTENTS_STREAM_CODEC, SoulBottlingRecipe::bottle,
                ByteBufCodecs.INT, SoulBottlingRecipe::soulCount,
                ItemStack.STREAM_CODEC, SoulBottlingRecipe::getResult,
                SoulBottlingRecipe::new
        );

        public Serializer() {
        }

        @Override
        public MapCodec<SoulBottlingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SoulBottlingRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }

    public static class JsonBuilder implements RecipeBuilder {
        protected CraftingBookCategory category = CraftingBookCategory.MISC;
        protected BlockIngredient bottle;
        protected int soulCount;
        protected ItemStack result = ItemStack.EMPTY;
        @Nullable
        protected String group;

        public static SoulBottlingRecipe.JsonBuilder create() {
            return new SoulBottlingRecipe.JsonBuilder();
        }

        public SoulBottlingRecipe.JsonBuilder category(CraftingBookCategory category) {
            this.category = category;
            return this;
        }

        public SoulBottlingRecipe.JsonBuilder bottle(BlockIngredient bottle) {
            this.bottle = bottle;
            return this;
        }

        public SoulBottlingRecipe.JsonBuilder bottle(Block bottle) {
            return this.bottle(BlockIngredient.of(bottle));
        }

        public SoulBottlingRecipe.JsonBuilder soulCount(int soulCount) {
            this.soulCount = soulCount;
            return this;
        }

        public SoulBottlingRecipe.JsonBuilder result(ItemStack itemStack) {
            this.result = itemStack;
            return this;
        }

        public SoulBottlingRecipe.JsonBuilder result(Item item, int count) {
            return this.result(new ItemStack(item, count));
        }

        public SoulBottlingRecipe.JsonBuilder result(Item item) {
            return this.result(item, 1);
        }

        @Override
        public SoulBottlingRecipe.JsonBuilder unlockedBy(String string, Criterion<?> advancementCriterion) {
            return this;
        }

        @Override
        public SoulBottlingRecipe.JsonBuilder group(@Nullable String string) {
            this.group = string;
            return this;
        }

        @Override
        public Item getResult() {
            return result.getItem();
        }

        @Override
        public void save(RecipeOutput exporter) {
            this.save(
                    exporter,
                    Id.of(RecipeProvider.getItemName(this.result.getItem()) + "_from_soul_bottling")
            );
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
            Advancement.Builder advancementBuilder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey)).rewards(AdvancementRewards.Builder.recipe(resourceKey)).requirements(AdvancementRequirements.Strategy.OR);
            SoulBottlingRecipe soulBottlingRecipe = new SoulBottlingRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.bottle,
                    this.soulCount,
                    this.result
            );
            recipeOutput.accept(resourceKey, soulBottlingRecipe, advancementBuilder.build(resourceKey.identifier().withPrefix("recipes/" + this.category.getSerializedName() + "/")));
        }
    }
}
