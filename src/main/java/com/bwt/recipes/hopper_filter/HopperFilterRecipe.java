package com.bwt.recipes.hopper_filter;

import com.bwt.recipes.BwtRecipes;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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

public record HopperFilterRecipe(
        String group,
        CraftingBookCategory category,
        Ingredient ingredient,
        Ingredient filter,
        int soulCount,
        ItemStack result,
        ItemStack byproduct
) implements Recipe<HopperFilterRecipeInput> {
    @Override
    public RecipeSerializer<HopperFilterRecipe> getSerializer() {
        return BwtRecipes.HOPPER_FILTER_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(HopperFilterRecipeInput input, Level level) {
        return this.filter.test(input.filterItem().getDefaultInstance()) && this.ingredient.test(input.itemStack());
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public RecipeType<HopperFilterRecipe> getType() {
        return BwtRecipes.HOPPER_FILTER_RECIPE_TYPE;
    }

    public CraftingBookCategory getCategory() {
        return this.category;
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
    public ItemStack assemble(HopperFilterRecipeInput input, HolderLookup.Provider lookup) {
        return result;
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

    public static class Serializer implements RecipeSerializer<HopperFilterRecipe> {
        protected static final MapCodec<HopperFilterRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingBookCategory.CODEC.fieldOf("category")
                                .orElse(CraftingBookCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        Ingredient.CODEC
                                .fieldOf("ingredient")
                                .forGetter(HopperFilterRecipe::ingredient),
                        Ingredient.CODEC
                                .fieldOf("filter")
                                .forGetter(HopperFilterRecipe::filter),
                        Codec.INT.fieldOf("soulCount")
                                .forGetter(HopperFilterRecipe::soulCount),
                        ItemStack.OPTIONAL_CODEC
                                .fieldOf("result")
                                .forGetter(HopperFilterRecipe::result),
                        ItemStack.OPTIONAL_CODEC
                                .fieldOf("byproduct")
                                .forGetter(HopperFilterRecipe::byproduct)
                ).apply(instance, HopperFilterRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, HopperFilterRecipe> PACKET_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, HopperFilterRecipe::group,
                CraftingBookCategory.STREAM_CODEC, HopperFilterRecipe::getCategory,
                Ingredient.CONTENTS_STREAM_CODEC, HopperFilterRecipe::ingredient,
                Ingredient.CONTENTS_STREAM_CODEC, HopperFilterRecipe::filter,
                ByteBufCodecs.INT, HopperFilterRecipe::soulCount,
                ItemStack.OPTIONAL_STREAM_CODEC, HopperFilterRecipe::result,
                ItemStack.OPTIONAL_STREAM_CODEC, HopperFilterRecipe::byproduct,
                HopperFilterRecipe::new
        );

        public Serializer() {
        }

        @Override
        public MapCodec<HopperFilterRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HopperFilterRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }

    public static class JsonBuilder implements RecipeBuilder {
        protected CraftingBookCategory category = CraftingBookCategory.MISC;
        protected Ingredient ingredient;
        protected Ingredient filter;
        protected int soulCount;
        protected ItemStack result = ItemStack.EMPTY;
        protected ItemStack byproduct = ItemStack.EMPTY;
        @Nullable
        protected String group;

        public static HopperFilterRecipe.JsonBuilder create() {
            return new HopperFilterRecipe.JsonBuilder();
        }

        public HopperFilterRecipe.JsonBuilder category(CraftingBookCategory category) {
            this.category = category;
            return this;
        }

        public HopperFilterRecipe.JsonBuilder ingredient(Ingredient ingredient) {
            this.ingredient = ingredient;
            return this;
        }

        public HopperFilterRecipe.JsonBuilder ingredient(ItemStack itemStack) {
            return this.ingredient(Ingredient.of(itemStack.getItem()));
        }

        public HopperFilterRecipe.JsonBuilder ingredient(Item item) {
            return this.ingredient(item.getDefaultInstance());
        }

        public HopperFilterRecipe.JsonBuilder filter(Ingredient filter) {
            this.filter = filter;
            return this;
        }

        public HopperFilterRecipe.JsonBuilder filter(Item filter) {
            this.filter = Ingredient.of(filter);
            return this;
        }

        public HopperFilterRecipe.JsonBuilder soulCount(int soulCount) {
            this.soulCount = soulCount;
            return this;
        }

        public HopperFilterRecipe.JsonBuilder result(ItemStack itemStack) {
            this.result = itemStack;
            return this;
        }

        public HopperFilterRecipe.JsonBuilder result(Item item, int count) {
            return this.result(new ItemStack(item, count));
        }

        public HopperFilterRecipe.JsonBuilder result(Item item) {
            return this.result(item, 1);
        }

        public HopperFilterRecipe.JsonBuilder byproduct(ItemStack itemStack) {
            this.byproduct = itemStack;
            return this;
        }

        public HopperFilterRecipe.JsonBuilder byproduct(Item item, int count) {
            return this.byproduct(new ItemStack(item, count));
        }

        public HopperFilterRecipe.JsonBuilder byproduct(Item item) {
            return this.byproduct(item, 1);
        }

        @Override
        public HopperFilterRecipe.JsonBuilder unlockedBy(String string, Criterion<?> advancementCriterion) {
            return this;
        }

        @Override
        public HopperFilterRecipe.JsonBuilder group(@Nullable String string) {
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
                    Id.of("filter_" + RecipeProvider.getItemName(this.ingredient.items().map(Holder::value).findFirst().orElse(Items.AIR)))
            );
        }

        @Override
        public void save(RecipeOutput exporter, String recipePath) {
            this.save(exporter, Id.of(recipePath));
        }

        public void save(RecipeOutput exporter, Identifier recipeId) {
            save(exporter, ResourceKey.create(Registries.RECIPE, recipeId));
        }

        @Override
        public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> resourceKey) {
            Advancement.Builder advancementBuilder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey)).rewards(AdvancementRewards.Builder.recipe(resourceKey)).requirements(AdvancementRequirements.Strategy.OR);
            HopperFilterRecipe hopperFilterRecipe = new HopperFilterRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredient,
                    this.filter,
                    this.soulCount,
                    this.result,
                    this.byproduct
            );
            recipeOutput.accept(resourceKey, hopperFilterRecipe, advancementBuilder.build(resourceKey.identifier().withPrefix("recipes/" + this.category.getSerializedName() + "/")));
        }
    }
}
