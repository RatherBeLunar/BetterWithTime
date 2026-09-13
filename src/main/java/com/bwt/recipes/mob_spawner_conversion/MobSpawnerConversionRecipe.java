package com.bwt.recipes.mob_spawner_conversion;

import com.bwt.generation.EmiDefaultsGenerator;
import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
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
import net.minecraft.world.level.block.Blocks;

public class MobSpawnerConversionRecipe implements Recipe<MobSpawnerConversionRecipeInput> {

    protected final String group;
    protected final CraftingBookCategory category;
    protected final BlockIngredient ingredient;
    protected final Block result;

    public MobSpawnerConversionRecipe(String group, CraftingBookCategory category, BlockIngredient ingredient, Block result) {
        this.group = group;
        this.category = category;
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.SPAWNER);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.MOB_SPAWNER_CONVERSION_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(MobSpawnerConversionRecipeInput input, Level level) {
        return this.ingredient.test(input.block());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    public BlockIngredient getIngredient() {
        return ingredient;
    }

    public Block getResult() {
        return result;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeType<?> getType() {
        return BwtRecipes.MOB_SPAWNER_CONVERSION_RECIPE_TYPE;
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
    public ItemStack assemble(MobSpawnerConversionRecipeInput input, HolderLookup.Provider lookup) {
        return getResultItem(lookup);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return result.asItem().getDefaultInstance();
    }

    public static class Serializer implements RecipeSerializer<MobSpawnerConversionRecipe> {
        protected static final MapCodec<MobSpawnerConversionRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingBookCategory.CODEC.fieldOf("category")
                                .orElse(CraftingBookCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        BlockIngredient.Serializer.CODEC
                                .fieldOf("ingredient")
                                .forGetter(recipe -> recipe.ingredient),
                        BuiltInRegistries.BLOCK.byNameCodec()
                                .fieldOf("result")
                                .forGetter(MobSpawnerConversionRecipe::getResult)
                ).apply(instance, MobSpawnerConversionRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, MobSpawnerConversionRecipe> PACKET_CODEC = StreamCodec.of(
                MobSpawnerConversionRecipe.Serializer::write, MobSpawnerConversionRecipe.Serializer::read
        );

        public Serializer() {}

        @Override
        public MapCodec<MobSpawnerConversionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MobSpawnerConversionRecipe> streamCodec() {
            return PACKET_CODEC;
        }

        protected static MobSpawnerConversionRecipe read(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            BlockIngredient ingredient = BlockIngredient.Serializer.read(buf);
            Block result = ByteBufCodecs.fromCodecWithRegistries(BuiltInRegistries.BLOCK.byNameCodec()).decode(buf);
            return new MobSpawnerConversionRecipe(group, category, ingredient, result);
        }

        protected static void write(RegistryFriendlyByteBuf buf, MobSpawnerConversionRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            BlockIngredient.Serializer.write(buf, recipe.ingredient);
            ByteBufCodecs.fromCodecWithRegistries(BuiltInRegistries.BLOCK.byNameCodec()).encode(buf, recipe.result);
        }
    }

    public static class JsonBuilder implements RecipeBuilder {
        protected CraftingBookCategory category = CraftingBookCategory.MISC;
        protected BlockIngredient ingredient;
        protected Block result;
        protected String fromBlockName;
        @Nullable
        protected String group;
        protected boolean isDefaultRecipe;

        public static MobSpawnerConversionRecipe.JsonBuilder create(Block input) {
            MobSpawnerConversionRecipe.JsonBuilder obj = new MobSpawnerConversionRecipe.JsonBuilder();
            obj.ingredient = BlockIngredient.fromBlock(input);
            obj.fromBlockName = BuiltInRegistries.BLOCK.getKey(input).getPath();
            return obj;
        }

        public static MobSpawnerConversionRecipe.JsonBuilder create(TagKey<Block> inputTag) {
            MobSpawnerConversionRecipe.JsonBuilder obj = new MobSpawnerConversionRecipe.JsonBuilder();
            obj.ingredient = BlockIngredient.fromTag(inputTag);
            obj.fromBlockName = inputTag.location().getPath();
            return obj;
        }

        public MobSpawnerConversionRecipe.JsonBuilder category(CraftingBookCategory category) {
            this.category = category;
            return this;
        }

        public MobSpawnerConversionRecipe.JsonBuilder convertsTo(Block block) {
            this.result = block;
            return this;
        }

        @Override
        public MobSpawnerConversionRecipe.JsonBuilder unlockedBy(String string, Criterion<?> advancementCriterion) {
            return this;
        }

        @Override
        public MobSpawnerConversionRecipe.JsonBuilder group(@Nullable String string) {
            this.group = string;
            return this;
        }

        public MobSpawnerConversionRecipe.JsonBuilder markDefault() {
            this.isDefaultRecipe = true;
            return this;
        }

        public void addToDefaults(ResourceLocation recipeId) {
            if(this.isDefaultRecipe) {
                EmiDefaultsGenerator.addBwtRecipe(recipeId);
            }
        }

        @Override
        public Item getResult() {
            return ingredient.getMatchingStacks().get(0).getItem();
        }

        @Override
        public void save(RecipeOutput exporter) {
            this.save(exporter, Id.of("mob_spawner_conversion_from_" + fromBlockName + "_to_" + BuiltInRegistries.BLOCK.getKey(result).getPath()));
        }

        @Override
        public void save(RecipeOutput exporter, String recipePath) {
            this.save(exporter, Id.of(recipePath));
        }

        @Override
        public void save(RecipeOutput exporter, ResourceLocation recipeId) {
            this.addToDefaults(recipeId);
            MobSpawnerConversionRecipe mobSpawnerConversionRecipe = new MobSpawnerConversionRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredient,
                    this.result
            );
            exporter.accept(recipeId, mobSpawnerConversionRecipe, null);
        }
    }
}
