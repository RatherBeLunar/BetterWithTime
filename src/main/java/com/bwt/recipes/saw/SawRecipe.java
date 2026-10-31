package com.bwt.recipes.saw;

import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class SawRecipe implements Recipe<SawRecipeInput> {
    protected final String group;
    protected final CraftingBookCategory category;
    final BlockIngredient ingredient;
    protected final NonNullList<ItemStack> results;

    public SawRecipe(String group, CraftingBookCategory category, BlockIngredient ingredient, List<ItemStack> results) {
        this.group = group;
        this.category = category;
        this.ingredient = ingredient;
        this.results = NonNullList.of(ItemStack.EMPTY, results.toArray(new ItemStack[0]));
    }

    @Override
    public RecipeSerializer<SawRecipe> getSerializer() {
        return BwtRecipes.SAW_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(SawRecipeInput input, Level level) {
        return this.ingredient.test(input.block());
    }

    public BlockIngredient getIngredient() {
        return ingredient;
    }

    public List<ItemStack> getResults() {
        return results.stream().map(ItemStack::copy).collect(Collectors.toList());
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public RecipeType<SawRecipe> getType() {
        return BwtRecipes.SAW_RECIPE_TYPE;
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
    public ItemStack assemble(SawRecipeInput input, HolderLookup.Provider lookup) {
        return results.getFirst();
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

    public static class Serializer implements RecipeSerializer<SawRecipe> {
        protected static final MapCodec<SawRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingBookCategory.CODEC.fieldOf("category")
                                .orElse(CraftingBookCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        BlockIngredient.Serializer.CODEC
                                .fieldOf("ingredient")
                                .forGetter(recipe -> recipe.ingredient),
                        ItemStack.STRICT_CODEC
                                .listOf()
                                .fieldOf("drops")
                                .forGetter(SawRecipe::getResults)
                ).apply(instance, SawRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SawRecipe> PACKET_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, SawRecipe::group,
                CraftingBookCategory.STREAM_CODEC, SawRecipe::getCategory,
                BlockIngredient.CONTENTS_STREAM_CODEC, SawRecipe::getIngredient,
                ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), SawRecipe::getResults,
                SawRecipe::new
        );


        public Serializer() {}

        @Override
        public MapCodec<SawRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SawRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }

    public interface RecipeFactory<T extends SawRecipe> {
        T create(String group, CraftingBookCategory category, BlockIngredient ingredient, List<ItemStack> results);
    }

    public static class JsonBuilder implements RecipeBuilder {
        protected CraftingBookCategory category = CraftingBookCategory.MISC;
        protected BlockIngredient ingredient;
        protected String fromBlockName;
        protected final NonNullList<ItemStack> results = NonNullList.create();
        @Nullable
        protected String group;

        public static JsonBuilder create(Block block) {
            JsonBuilder obj = new JsonBuilder();
            obj.ingredient = BlockIngredient.of(block);
            obj.fromBlockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
            return obj;
        }

        public static JsonBuilder create(HolderGetter<Block> blocks, TagKey<Block> blockTag) {
            JsonBuilder obj = new JsonBuilder();
            obj.ingredient = BlockIngredient.of(blocks.getOrThrow(blockTag));
            obj.fromBlockName = blockTag.location().getPath();
            return obj;
        }

        public static void dropsSelf(Block block, RecipeOutput exporter) {
            create(block).result(block.asItem()).save(exporter);
        }

        RecipeFactory<SawRecipe> getRecipeFactory() {
            return SawRecipe::new;
        }

        public JsonBuilder category(CraftingBookCategory category) {
            this.category = category;
            return this;
        }

        public JsonBuilder results(ItemStack... itemStacks) {
            this.results.addAll(Arrays.asList(itemStacks));
            return this;
        }

        public JsonBuilder result(ItemStack itemStack) {
            this.results.add(itemStack);
            return this;
        }

        public JsonBuilder result(ItemLike item, int count) {
            this.results.add(new ItemStack(item, count));
            return this;
        }

        public JsonBuilder result(ItemLike item) {
            return this.result(item, 1);
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
            return results.getFirst().getItem();
        }

        @Override
        public void save(RecipeOutput exporter) {
            this.save(exporter, Id.of("saw_" + fromBlockName));
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
            SawRecipe sawRecipe = this.getRecipeFactory().create(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredient,
                    this.results
            );
            recipeOutput.accept(resourceKey, sawRecipe, advancementBuilder.build(resourceKey.identifier().withPrefix("recipes/" + this.category.getSerializedName() + "/")));
        }
    }
}
