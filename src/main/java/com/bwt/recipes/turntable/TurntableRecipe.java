package com.bwt.recipes.turntable;

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
    public ItemStack getToastSymbol() {
        return new ItemStack(BwtBlocks.turntableBlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.TURNTABLE_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(TurntableRecipeInput input, Level level) {
        return this.ingredient.test(input.block());
    }


    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
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
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeType<?> getType() {
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
        return getResultItem(lookup);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return output.asItem().getDefaultInstance();
    }

    public static class Serializer implements RecipeSerializer<TurntableRecipe> {
        protected static final MapCodec<TurntableRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingBookCategory.CODEC.fieldOf("category")
                                .orElse(CraftingBookCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        BlockIngredient.Serializer.CODEC
                                .fieldOf("ingredient")
                                .forGetter(recipe -> recipe.ingredient),
                        ResourceLocation.CODEC
                                .fieldOf("output")
                                .forGetter(recipe -> BuiltInRegistries.BLOCK.getKey(recipe.output)),
                        ItemStack.STRICT_CODEC
                                .listOf()
                                .fieldOf("drops")
                                .forGetter(TurntableRecipe::getDrops)
                ).apply(instance, (group, category, ingredient, outputId, drops) -> new TurntableRecipe(group, category, ingredient, BuiltInRegistries.BLOCK.get(outputId), drops))
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, TurntableRecipe> PACKET_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read
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

        public static TurntableRecipe read(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            BlockIngredient ingredient = BlockIngredient.Serializer.read(buf);
            Block output = BuiltInRegistries.BLOCK.get(buf.readResourceLocation());
            List<ItemStack> drops = ItemStack.LIST_STREAM_CODEC.decode(buf);
            return new TurntableRecipe(group, category, ingredient, output, drops);
        }

        public static void write(RegistryFriendlyByteBuf buf, TurntableRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            BlockIngredient.Serializer.write(buf, recipe.ingredient);
            buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(recipe.output));
            ItemStack.LIST_STREAM_CODEC.encode(buf, recipe.getDrops());
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
            obj.ingredient = BlockIngredient.fromBlock(input);
            obj.fromBlockName = BuiltInRegistries.BLOCK.getKey(input).getPath();
            obj.output = output;
            return obj;
        }

        public static JsonBuilder create(TagKey<Block> inputTag, Block output) {
            JsonBuilder obj = new JsonBuilder();
            obj.ingredient = BlockIngredient.fromTag(inputTag);
            obj.fromBlockName = inputTag.location().getPath();
            obj.output = output;
            return obj;
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

        @Override
        public void save(RecipeOutput exporter, ResourceLocation recipeId) {
            this.addToDefaults(recipeId);
            Advancement.Builder advancementBuilder = exporter.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(AdvancementRequirements.Strategy.OR);
            TurntableRecipe turntableRecipe = new TurntableRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredient,
                    this.output,
                    this.drops
            );
            exporter.accept(recipeId, turntableRecipe, advancementBuilder.build(recipeId.withPrefix("recipes/" + this.category.getSerializedName() + "/")));
        }
    }
}
