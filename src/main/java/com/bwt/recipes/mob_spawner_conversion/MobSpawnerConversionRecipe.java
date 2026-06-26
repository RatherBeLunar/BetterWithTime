package com.bwt.recipes.mob_spawner_conversion;

import com.bwt.generation.EmiDefaultsGenerator;
import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MobSpawnerConversionRecipe implements Recipe<MobSpawnerConversionRecipeInput> {

    protected final String group;
    protected final CraftingRecipeCategory category;
    protected final BlockIngredient ingredient;
    protected final Block result;

    public MobSpawnerConversionRecipe(String group, CraftingRecipeCategory category, BlockIngredient ingredient, Block result) {
        this.group = group;
        this.category = category;
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Blocks.SPAWNER);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.MOB_SPAWNER_CONVERSION_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(MobSpawnerConversionRecipeInput input, World world) {
        return this.ingredient.test(input.block());
    }

    @Override
    public boolean fits(int width, int height) {
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
    public ItemStack craft(MobSpawnerConversionRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return getResult(lookup);
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return result.asItem().getDefaultStack();
    }

    public static class Serializer implements RecipeSerializer<MobSpawnerConversionRecipe> {
        protected static final MapCodec<MobSpawnerConversionRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingRecipeCategory.CODEC.fieldOf("category")
                                .orElse(CraftingRecipeCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        BlockIngredient.Serializer.CODEC
                                .fieldOf("ingredient")
                                .forGetter(recipe -> recipe.ingredient),
                        Registries.BLOCK.getCodec()
                                .fieldOf("result")
                                .forGetter(MobSpawnerConversionRecipe::getResult)
                ).apply(instance, MobSpawnerConversionRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, MobSpawnerConversionRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                MobSpawnerConversionRecipe.Serializer::write, MobSpawnerConversionRecipe.Serializer::read
        );

        public Serializer() {}

        @Override
        public MapCodec<MobSpawnerConversionRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, MobSpawnerConversionRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        protected static MobSpawnerConversionRecipe read(RegistryByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
            BlockIngredient ingredient = BlockIngredient.Serializer.read(buf);
            Block result = PacketCodecs.registryCodec(Registries.BLOCK.getCodec()).decode(buf);
            return new MobSpawnerConversionRecipe(group, category, ingredient, result);
        }

        protected static void write(RegistryByteBuf buf, MobSpawnerConversionRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            BlockIngredient.Serializer.write(buf, recipe.ingredient);
            PacketCodecs.registryCodec(Registries.BLOCK.getCodec()).encode(buf, recipe.result);
        }
    }

    public static class JsonBuilder implements CraftingRecipeJsonBuilder {
        protected CraftingRecipeCategory category = CraftingRecipeCategory.MISC;
        protected BlockIngredient ingredient;
        protected Block result;
        protected String fromBlockName;
        @Nullable
        protected String group;
        protected boolean isDefaultRecipe;

        public static MobSpawnerConversionRecipe.JsonBuilder create(Block input) {
            MobSpawnerConversionRecipe.JsonBuilder obj = new MobSpawnerConversionRecipe.JsonBuilder();
            obj.ingredient = BlockIngredient.fromBlock(input);
            obj.fromBlockName = Registries.BLOCK.getId(input).getPath();
            return obj;
        }

        public static MobSpawnerConversionRecipe.JsonBuilder create(TagKey<Block> inputTag) {
            MobSpawnerConversionRecipe.JsonBuilder obj = new MobSpawnerConversionRecipe.JsonBuilder();
            obj.ingredient = BlockIngredient.fromTag(inputTag);
            obj.fromBlockName = inputTag.id().getPath();
            return obj;
        }

        public MobSpawnerConversionRecipe.JsonBuilder category(CraftingRecipeCategory category) {
            this.category = category;
            return this;
        }

        public MobSpawnerConversionRecipe.JsonBuilder convertsTo(Block block) {
            this.result = block;
            return this;
        }

        @Override
        public MobSpawnerConversionRecipe.JsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
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

        public void addToDefaults(Identifier recipeId) {
            if(this.isDefaultRecipe) {
                EmiDefaultsGenerator.addBwtRecipe(recipeId);
            }
        }

        @Override
        public Item getOutputItem() {
            return ingredient.getMatchingStacks().get(0).getItem();
        }

        @Override
        public void offerTo(RecipeExporter exporter) {
            this.offerTo(exporter, Id.of("mob_spawner_conversion_from_" + fromBlockName + "_to_" + Registries.BLOCK.getId(result).getPath()));
        }

        @Override
        public void offerTo(RecipeExporter exporter, String recipePath) {
            this.offerTo(exporter, Id.of(recipePath));
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
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
