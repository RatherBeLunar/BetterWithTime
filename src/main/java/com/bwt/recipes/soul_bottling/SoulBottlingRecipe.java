package com.bwt.recipes.soul_bottling;

import com.bwt.generation.EmiDefaultsGenerator;
import com.bwt.items.BwtItems;
import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public record SoulBottlingRecipe(String group, CraftingBookCategory category, BlockIngredient bottle, int soulCount, ItemStack result) implements Recipe<SoulBottlingRecipeInput> {
    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(BwtItems.soulUrnItem);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.SOUL_BOTTLING_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(SoulBottlingRecipeInput input, Level level) {
        return bottle.test(input.block());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> defaultedList = NonNullList.create();
        defaultedList.add(bottle.toVanilla());
        return defaultedList;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeType<?> getType() {
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
        return getResultItem(lookup);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider wrapperLookup) {
        return result;
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
        public static final StreamCodec<RegistryFriendlyByteBuf, SoulBottlingRecipe> PACKET_CODEC = StreamCodec.of(
                SoulBottlingRecipe.Serializer::write, SoulBottlingRecipe.Serializer::read
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

        public static SoulBottlingRecipe read(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            BlockIngredient bottle = BlockIngredient.Serializer.PACKET_CODEC.decode(buf);
            int soulCount = buf.readVarInt();
            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            return new SoulBottlingRecipe(group, category, bottle, soulCount, result);
        }

        public static void write(RegistryFriendlyByteBuf buf, SoulBottlingRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            BlockIngredient.Serializer.PACKET_CODEC.encode(buf, recipe.bottle);
            buf.writeVarInt(recipe.soulCount);
            ItemStack.STREAM_CODEC.encode(buf, recipe.getResult());
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

        public SoulBottlingRecipe.JsonBuilder category(CraftingBookCategory category) {
            this.category = category;
            return this;
        }

        public SoulBottlingRecipe.JsonBuilder bottle(BlockIngredient bottle) {
            this.bottle = bottle;
            return this;
        }

        public SoulBottlingRecipe.JsonBuilder bottle(Block bottle) {
            return this.bottle(BlockIngredient.fromBlock(bottle));
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

        @Override
        public void save(RecipeOutput exporter, ResourceLocation recipeId) {
            addToDefaults(recipeId);

            Advancement.Builder advancementBuilder = exporter.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(AdvancementRequirements.Strategy.OR);
            SoulBottlingRecipe soulBottlingRecipe = new SoulBottlingRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.bottle,
                    this.soulCount,
                    this.result
            );
            exporter.accept(recipeId, soulBottlingRecipe, advancementBuilder.build(recipeId.withPrefix("recipes/" + this.category.getSerializedName() + "/")));
        }
    }
}
