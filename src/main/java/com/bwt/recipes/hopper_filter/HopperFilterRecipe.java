package com.bwt.recipes.hopper_filter;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.BwtRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record HopperFilterRecipe(
        String group,
        CraftingRecipeCategory category,
        Ingredient ingredient,
        Ingredient filter,
        int soulCount,
        ItemStack result,
        ItemStack byproduct
) implements Recipe<HopperFilterRecipeInput> {
    @Override
    public ItemStack createIcon() {
        return new ItemStack(BwtBlocks.hopperBlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.HOPPER_FILTER_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(HopperFilterRecipeInput input, World world) {
        return this.filter.test(input.filterItem().getDefaultStack()) && this.ingredient.test(input.itemStack());
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeType<?> getType() {
        return BwtRecipes.HOPPER_FILTER_RECIPE_TYPE;
    }

    public CraftingRecipeCategory getCategory() {
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
    public ItemStack craft(HopperFilterRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return getResult(lookup);
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup wrapperLookup) {
        return result;
    }

    public static class Serializer implements RecipeSerializer<HopperFilterRecipe> {
        protected static final MapCodec<HopperFilterRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingRecipeCategory.CODEC.fieldOf("category")
                                .orElse(CraftingRecipeCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        Ingredient.DISALLOW_EMPTY_CODEC
                                .fieldOf("ingredient")
                                .forGetter(HopperFilterRecipe::ingredient),
                        Ingredient.DISALLOW_EMPTY_CODEC
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
        public static final PacketCodec<RegistryByteBuf, HopperFilterRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                HopperFilterRecipe.Serializer::write, HopperFilterRecipe.Serializer::read
        );

        public Serializer() {
        }

        @Override
        public MapCodec<HopperFilterRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, HopperFilterRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        public static HopperFilterRecipe read(RegistryByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
            Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
            Ingredient filter = Ingredient.PACKET_CODEC.decode(buf);
            int soulCount = buf.readVarInt();
            ItemStack result = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
            ItemStack byproduct = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
            return new HopperFilterRecipe(group, category, ingredient, filter, soulCount, result, byproduct);
        }

        public static void write(RegistryByteBuf buf, HopperFilterRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            Ingredient.PACKET_CODEC.encode(buf, recipe.ingredient);
            Ingredient.PACKET_CODEC.encode(buf, recipe.filter);
            buf.writeVarInt(recipe.soulCount);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, recipe.result);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, recipe.byproduct);
        }
    }

    public static class JsonBuilder implements CraftingRecipeJsonBuilder {
        protected CraftingRecipeCategory category = CraftingRecipeCategory.MISC;
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

        public HopperFilterRecipe.JsonBuilder category(CraftingRecipeCategory category) {
            this.category = category;
            return this;
        }

        public HopperFilterRecipe.JsonBuilder ingredient(Ingredient ingredient) {
            this.ingredient = ingredient;
            return this;
        }

        public HopperFilterRecipe.JsonBuilder ingredient(ItemStack itemStack) {
            return this.ingredient(Ingredient.ofStacks(itemStack));
        }

        public HopperFilterRecipe.JsonBuilder ingredient(Item item) {
            return this.ingredient(item.getDefaultStack());
        }

        public HopperFilterRecipe.JsonBuilder filter(Ingredient filter) {
            this.filter = filter;
            return this;
        }

        public HopperFilterRecipe.JsonBuilder filter(Item filter) {
            this.filter = Ingredient.ofItems(filter);
            return this;
        }

        public HopperFilterRecipe.JsonBuilder filter(TagKey<Item> filter) {
            this.filter = Ingredient.fromTag(filter);
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
        public HopperFilterRecipe.JsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
            return this;
        }

        @Override
        public HopperFilterRecipe.JsonBuilder group(@Nullable String string) {
            this.group = string;
            return this;
        }

        @Override
        public Item getOutputItem() {
            return result.getItem();
        }

        @Override
        public void offerTo(RecipeExporter exporter) {
            this.offerTo(
                    exporter,
                    "bwt:filter_" + RecipeProvider.getItemPath(this.ingredient.getMatchingStacks()[0].getItem())
            );
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            HopperFilterRecipe hopperFilterRecipe = new HopperFilterRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredient,
                    this.filter,
                    this.soulCount,
                    this.result,
                    this.byproduct
            );
            exporter.accept(recipeId, hopperFilterRecipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/" + this.category.asString() + "/")));
        }
    }
}
