package com.bwt.recipes.kiln;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.generation.EmiDefaultsGenerator;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class KilnRecipe implements Recipe<KilnRecipeInput> {
    public static final int DEFAULT_COOKING_TIME = 1;

    protected final String group;
    protected final CraftingBookCategory category;
    protected final BlockIngredient ingredient;
    protected final int cookingTime;
    protected final NonNullList<ItemStack> drops;

    public KilnRecipe(String group, CraftingBookCategory category, BlockIngredient ingredient, int cookingTime, List<ItemStack> drops) {
        this.group = group;
        this.category = category;
        this.ingredient = ingredient;
        this.cookingTime = cookingTime;
        this.drops = NonNullList.of(ItemStack.EMPTY, drops.toArray(new ItemStack[0]));
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(BwtBlocks.kilnBlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.KILN_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(KilnRecipeInput input, Level level) {
        return this.ingredient.test(input.block());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }


    public BlockIngredient getIngredient() {
        return ingredient;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public NonNullList<ItemStack> getDrops() {
        return NonNullList.of(ItemStack.EMPTY, drops.stream().map(ItemStack::copy).toList().toArray(new ItemStack[]{}));
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeType<?> getType() {
        return BwtRecipes.KILN_RECIPE_TYPE;
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
    public ItemStack assemble(KilnRecipeInput input, HolderLookup.Provider lookup) {
        return getResultItem(lookup);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return drops.get(0);
    }

    public static class Serializer implements RecipeSerializer<KilnRecipe> {
        protected static final MapCodec<KilnRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingBookCategory.CODEC.fieldOf("category")
                                .orElse(CraftingBookCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        BlockIngredient.Serializer.CODEC
                                .fieldOf("ingredient")
                                .forGetter(recipe -> recipe.ingredient),
                        Codec.INT.fieldOf("cookingTime")
                                .orElse(KilnRecipe.DEFAULT_COOKING_TIME)
                                .forGetter(recipe -> recipe.cookingTime),
                        ItemStack.STRICT_CODEC
                                .listOf()
                                .fieldOf("drops")
                                .forGetter(KilnRecipe::getDrops)
                ).apply(instance, KilnRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, KilnRecipe> PACKET_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read
        );

        public Serializer() {}

        @Override
        public MapCodec<KilnRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, KilnRecipe> streamCodec() {
            return PACKET_CODEC;
        }

        protected static KilnRecipe read(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            BlockIngredient ingredient = BlockIngredient.Serializer.read(buf);
            int cookingTime = buf.readVarInt();
            List<ItemStack> drops = ItemStack.LIST_STREAM_CODEC.decode(buf);
            return new KilnRecipe(group, category, ingredient, cookingTime, drops);
        }

        protected static void write(RegistryFriendlyByteBuf buf, KilnRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            BlockIngredient.Serializer.write(buf, recipe.ingredient);
            buf.writeVarInt(recipe.cookingTime);
            ItemStack.LIST_STREAM_CODEC.encode(buf, recipe.getDrops());
        }
    }

    public static class JsonBuilder implements RecipeBuilder {
        protected CraftingBookCategory category = CraftingBookCategory.MISC;
        protected BlockIngredient ingredient;
        protected int cookingTime;
        protected String fromBlockName;
        protected final NonNullList<ItemStack> drops = NonNullList.create();
        @Nullable
        protected String group;

        public static JsonBuilder create(Block input) {
            JsonBuilder obj = new JsonBuilder();
            obj.ingredient = BlockIngredient.fromBlock(input);
            obj.fromBlockName = BuiltInRegistries.BLOCK.getKey(input).getPath();
            obj.cookingTime = KilnRecipe.DEFAULT_COOKING_TIME;
            return obj;
        }

        public static JsonBuilder create(TagKey<Block> inputTag) {
            JsonBuilder obj = new JsonBuilder();
            obj.ingredient = BlockIngredient.fromTag(inputTag);
            obj.fromBlockName = inputTag.location().getPath();
            obj.cookingTime = KilnRecipe.DEFAULT_COOKING_TIME;
            return obj;
        }

        public JsonBuilder category(CraftingBookCategory category) {
            this.category = category;
            return this;
        }

        public JsonBuilder cookingTime(int cookingTime) {
            this.cookingTime = cookingTime;
            return this;
        }

        public JsonBuilder drops(ItemStack... itemStacks) {
            this.drops.addAll(Arrays.asList(itemStacks));
            return this;
        }

        public JsonBuilder drops(ItemLike item, int count) {
            return this.drops(new ItemStack(item, count));
        }

        public JsonBuilder drops(ItemLike item) {
            return this.drops(item, 1);
        }

        public JsonBuilder result(ItemStack itemStack) {
            this.drops.add(itemStack);
            return this;
        }

        public JsonBuilder result(ItemLike item, int count) {
            this.drops.add(new ItemStack(item, count));
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

        protected boolean isDefaultRecipe;
        public JsonBuilder markDefault() {
            this.isDefaultRecipe = true;
            return this;
        }
        public void addToDefaults(ResourceLocation recipeId) {
            if(this.isDefaultRecipe) {
                EmiDefaultsGenerator.addBwtRecipe(recipeId.withPrefix("/"));
            }
        }

        @Override
        public Item getResult() {
            return drops.get(0).getItem();
        }

        @Override
        public void save(RecipeOutput exporter) {
            this.save(exporter, Id.of("kiln_cook_" + fromBlockName));
        }

        @Override
        public void save(RecipeOutput exporter, String recipePath) {
            this.save(exporter, Id.of(recipePath));
        }

        @Override
        public void save(RecipeOutput exporter, ResourceLocation recipeId) {
            this.addToDefaults(recipeId);
            Advancement.Builder advancementBuilder = exporter.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(AdvancementRequirements.Strategy.OR);
            KilnRecipe kilnRecipe = new KilnRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredient,
                    this.cookingTime,
                    this.drops
            );
            exporter.accept(recipeId, kilnRecipe, advancementBuilder.build(recipeId.withPrefix("recipes/" + this.category.getSerializedName() + "/")));
        }
    }
}
