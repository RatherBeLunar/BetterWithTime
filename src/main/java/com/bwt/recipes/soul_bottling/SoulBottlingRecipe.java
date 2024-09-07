package com.bwt.recipes.soul_bottling;

import com.bwt.items.BwtItems;
import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record SoulBottlingRecipe(String group, CraftingRecipeCategory category, BlockIngredient bottle, int soulCount, ItemStack result) implements Recipe<SoulBottlingRecipeInput> {
    @Override
    public ItemStack createIcon() {
        return new ItemStack(BwtItems.soulUrnItem);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.SOUL_BOTTLING_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(SoulBottlingRecipeInput input, World world) {
        return bottle.test(input.block());
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
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
    public boolean isIgnoredInRecipeBook() {
        return Recipe.super.isIgnoredInRecipeBook();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public ItemStack craft(SoulBottlingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return getResult(lookup);
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup wrapperLookup) {
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
                        CraftingRecipeCategory.CODEC.fieldOf("category")
                                .orElse(CraftingRecipeCategory.MISC)
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
        public static final PacketCodec<RegistryByteBuf, SoulBottlingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                SoulBottlingRecipe.Serializer::write, SoulBottlingRecipe.Serializer::read
        );

        public Serializer() {
        }

        @Override
        public MapCodec<SoulBottlingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, SoulBottlingRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        public static SoulBottlingRecipe read(RegistryByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
            BlockIngredient bottle = BlockIngredient.Serializer.PACKET_CODEC.decode(buf);
            int soulCount = buf.readVarInt();
            ItemStack result = ItemStack.PACKET_CODEC.decode(buf);
            return new SoulBottlingRecipe(group, category, bottle, soulCount, result);
        }

        public static void write(RegistryByteBuf buf, SoulBottlingRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            BlockIngredient.Serializer.PACKET_CODEC.encode(buf, recipe.bottle);
            buf.writeVarInt(recipe.soulCount);
            ItemStack.PACKET_CODEC.encode(buf, recipe.getResult());
        }
    }

    public static class JsonBuilder implements CraftingRecipeJsonBuilder {
        protected CraftingRecipeCategory category = CraftingRecipeCategory.MISC;
        protected BlockIngredient bottle;
        protected int soulCount;
        protected ItemStack result = ItemStack.EMPTY;
        @Nullable
        protected String group;

        public static SoulBottlingRecipe.JsonBuilder create() {
            return new SoulBottlingRecipe.JsonBuilder();
        }

        public SoulBottlingRecipe.JsonBuilder category(CraftingRecipeCategory category) {
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
        public SoulBottlingRecipe.JsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
            return this;
        }

        @Override
        public SoulBottlingRecipe.JsonBuilder group(@Nullable String string) {
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
                    "bwt:" + RecipeProvider.getItemPath(this.result.getItem()) + "_from_soul_bottling"
            );
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            SoulBottlingRecipe soulBottlingRecipe = new SoulBottlingRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.bottle,
                    this.soulCount,
                    this.result
            );
            exporter.accept(recipeId, soulBottlingRecipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/" + this.category.asString() + "/")));
        }
    }
}
