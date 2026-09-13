package com.bwt.recipes.saw;

import com.bwt.blocks.BwtBlocks;
import com.bwt.generation.EmiDefaultsGenerator;
import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
    public ItemStack getToastSymbol() {
        return new ItemStack(BwtBlocks.sawBlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.SAW_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(SawRecipeInput input, Level level) {
        return this.ingredient.test(input.block());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }


    public BlockIngredient getIngredient() {
        return ingredient;
    }

    public List<ItemStack> getResults() {
        return results.stream().map(ItemStack::copy).collect(Collectors.toList());
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeType<?> getType() {
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
        return getResultItem(lookup);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return results.get(0);
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
        public static final StreamCodec<RegistryFriendlyByteBuf, SawRecipe> PACKET_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read
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

        public static SawRecipe read(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            BlockIngredient ingredient = BlockIngredient.Serializer.read(buf);
            List<ItemStack> drops = ItemStack.LIST_STREAM_CODEC.decode(buf);
            return new SawRecipe(group, category, ingredient, drops);
        }

        public static void write(RegistryFriendlyByteBuf buf, SawRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            BlockIngredient.Serializer.write(buf, recipe.ingredient);
            ItemStack.LIST_STREAM_CODEC.encode(buf, recipe.getResults());
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
            obj.ingredient = BlockIngredient.fromBlock(block);
            obj.fromBlockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
            return obj;
        }

        public static JsonBuilder create(TagKey<Block> blockTag) {
            JsonBuilder obj = new JsonBuilder();
            obj.ingredient = BlockIngredient.fromTag(blockTag);
            obj.fromBlockName = blockTag.location().getPath();
            return obj;
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
            return results.get(0).getItem();
        }

        @Override
        public void save(RecipeOutput exporter) {
            this.save(exporter, Id.of("saw_" + fromBlockName));
        }

        @Override
        public void save(RecipeOutput exporter, String recipePath) {
            this.save(exporter, Id.of(recipePath));
        }

        @Override
        public void save(RecipeOutput exporter, ResourceLocation recipeId) {
            addToDefaults(recipeId);

            Advancement.Builder advancementBuilder = exporter.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(AdvancementRequirements.Strategy.OR);
            SawRecipe sawRecipe = this.getRecipeFactory().create(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredient,
                    this.results
            );
            exporter.accept(recipeId, sawRecipe, advancementBuilder.build(recipeId.withPrefix("recipes/" + this.category.getSerializedName() + "/")));
        }
    }
}
