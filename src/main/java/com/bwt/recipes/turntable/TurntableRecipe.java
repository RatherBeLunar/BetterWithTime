package com.bwt.recipes.turntable;

import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class TurntableRecipe implements Recipe<TurntableRecipeInput> {
    protected final String group;
    protected final CraftingBookCategory category;
    final BlockIngredient ingredient;
    final Block output;
    protected final NonNullList<ItemStack> drops;

    public TurntableRecipe(String group, CraftingBookCategory category, BlockIngredient ingredient, Block output, List<ItemStack> drops) {
        this.group = group;
        this.category = category;
        this.ingredient = ingredient;
        this.output = output;
        this.drops = NonNullList.of(ItemStack.EMPTY, drops.toArray(new ItemStack[0]));
    }

    @Override
    public RecipeSerializer<TurntableRecipe> getSerializer() {
        return BwtRecipes.TURNTABLE_RECIPE_SERIALIZER;
    }


    @Override
    public boolean matches(TurntableRecipeInput input, Level level) {
        return this.ingredient.test(input.block());
    }


    public BlockIngredient getIngredient() {
        return ingredient;
    }
    public Block getOutput() {
        return output;
    }

    public NonNullList<ItemStack> getDrops() {
        return NonNullList.of(ItemStack.EMPTY, drops.stream().map(ItemStack::copy).toList().toArray(new ItemStack[0]));
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public RecipeType<TurntableRecipe> getType() {
        return BwtRecipes.TURNTABLE_RECIPE_TYPE;
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
    public ItemStack assemble(TurntableRecipeInput input, HolderLookup.Provider lookup) {
        return output.asItem().getDefaultInstance();
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

    public static class Serializer implements RecipeSerializer<TurntableRecipe> {
        protected static final MapCodec<TurntableRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(TurntableRecipe::group),
                        CraftingBookCategory.CODEC.fieldOf("category")
                                .orElse(CraftingBookCategory.MISC)
                                .forGetter(TurntableRecipe::getCategory),
                        BlockIngredient.Serializer.CODEC
                                .fieldOf("ingredient")
                                .forGetter(TurntableRecipe::getIngredient),
                        BuiltInRegistries.BLOCK
                                .byNameCodec()
                                .fieldOf("output")
                                .forGetter(TurntableRecipe::getOutput),
                        ItemStack.STRICT_CODEC
                                .listOf()
                                .fieldOf("drops")
                                .forGetter(TurntableRecipe::getDrops)
                ).apply(instance, TurntableRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, TurntableRecipe> PACKET_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, TurntableRecipe::group,
                CraftingBookCategory.STREAM_CODEC, TurntableRecipe::getCategory,
                BlockIngredient.CONTENTS_STREAM_CODEC, TurntableRecipe::getIngredient,
                ByteBufCodecs.fromCodecWithRegistries(BuiltInRegistries.BLOCK.byNameCodec()), TurntableRecipe::getOutput,
                ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), TurntableRecipe::getDrops,
                TurntableRecipe::new
        );

        public Serializer() {}

        @Override
        public MapCodec<TurntableRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TurntableRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }

    public static class JsonBuilder implements RecipeBuilder {
        protected CraftingBookCategory category = CraftingBookCategory.MISC;
        protected BlockIngredient ingredient;
        protected Block output;
        protected String fromBlockName;
        protected final NonNullList<ItemStack> drops = NonNullList.create();
        @Nullable
        protected String group;

        public static JsonBuilder create(Block input, Block output) {
            JsonBuilder obj = new JsonBuilder();
            obj.ingredient = BlockIngredient.of(input);
            obj.fromBlockName = BuiltInRegistries.BLOCK.getKey(input).getPath();
            obj.output = output;
            return obj;
        }

        public static JsonBuilder create(HolderSet<Block> blocks, String fromBlockName, Block output) {
            JsonBuilder obj = new JsonBuilder();
            obj.ingredient = BlockIngredient.of(blocks);
            obj.fromBlockName = fromBlockName;
            obj.output = output;
            return obj;
        }

        public static JsonBuilder create(HolderSet<Block> blocks, TagKey<Block> inputTag, Block output) {
            return create(blocks, inputTag.location().getPath(), output);
        }

        public JsonBuilder category(CraftingBookCategory category) {
            this.category = category;
            return this;
        }

        public JsonBuilder drops(ItemStack... itemStacks) {
            this.drops.addAll(Arrays.asList(itemStacks));
            return this;
        }

        public JsonBuilder drops(Item item, int count) {
            return this.drops(new ItemStack(item, count));
        }

        public JsonBuilder drops(Item item) {
            return this.drops(item, 1);
        }

        public JsonBuilder result(ItemStack itemStack) {
            this.drops.add(itemStack);
            return this;
        }

        public JsonBuilder result(Item item, int count) {
            this.drops.add(new ItemStack(item, count));
            return this;
        }

        public JsonBuilder result(Item item) {
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
            return output.asItem();
        }

        @Override
        public void save(RecipeOutput exporter) {
            this.save(exporter, Id.of("turntable_" + fromBlockName));
        }

        @Override
        public void save(RecipeOutput exporter, String recipePath) {
            this.save(exporter, Id.of(recipePath));
        }

        public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {
            this.save(recipeOutput, ResourceKey.create(Registries.RECIPE, recipeId));
        }

        @Override
        public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> resourceKey) {
            Advancement.Builder advancementBuilder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey)).rewards(AdvancementRewards.Builder.recipe(resourceKey)).requirements(AdvancementRequirements.Strategy.OR);
            TurntableRecipe turntableRecipe = new TurntableRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredient,
                    this.output,
                    this.drops
            );
            recipeOutput.accept(resourceKey, turntableRecipe, advancementBuilder.build(resourceKey.location().withPrefix("recipes/" + this.category.getSerializedName() + "/")));
        }
    }
}
