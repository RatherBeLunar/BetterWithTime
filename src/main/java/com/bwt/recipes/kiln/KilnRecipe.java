package com.bwt.recipes.kiln;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.generation.EmiDefaultsGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class KilnRecipe implements Recipe<KilnRecipeInput> {
    public static int DEFAULT_COOKING_TIME = 1;

    protected final String group;
    protected final CraftingRecipeCategory category;
    protected final BlockIngredient ingredient;
    protected final int cookingTime;
    protected final DefaultedList<ItemStack> drops;

    public KilnRecipe(String group, CraftingRecipeCategory category, BlockIngredient ingredient, int cookingTime, List<ItemStack> drops) {
        this.group = group;
        this.category = category;
        this.ingredient = ingredient;
        this.cookingTime = cookingTime;
        this.drops = DefaultedList.copyOf(ItemStack.EMPTY, drops.toArray(new ItemStack[0]));
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(BwtBlocks.kilnBlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.KILN_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(KilnRecipeInput input, World world) {
        return this.ingredient.test(input.block());
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }


    public BlockIngredient getIngredient() {
        return ingredient;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public DefaultedList<ItemStack> getDrops() {
        return DefaultedList.copyOf(ItemStack.EMPTY, drops.stream().map(ItemStack::copy).toList().toArray(new ItemStack[]{}));
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeType<?> getType() {
        return BwtRecipes.KILN_RECIPE_TYPE;
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
    public ItemStack craft(KilnRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return getResult(lookup);
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return drops.get(0);
    }

    public static class Serializer implements RecipeSerializer<KilnRecipe> {
        protected static final MapCodec<KilnRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingRecipeCategory.CODEC.fieldOf("category")
                                .orElse(CraftingRecipeCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        BlockIngredient.Serializer.CODEC
                                .fieldOf("ingredient")
                                .forGetter(recipe -> recipe.ingredient),
                        Codec.INT.fieldOf("cookingTime")
                                .orElse(KilnRecipe.DEFAULT_COOKING_TIME)
                                .forGetter(recipe -> recipe.cookingTime),
                        ItemStack.VALIDATED_CODEC
                                .listOf()
                                .fieldOf("drops")
                                .forGetter(KilnRecipe::getDrops)
                ).apply(instance, KilnRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, KilnRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read
        );

        public Serializer() {}

        @Override
        public MapCodec<KilnRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, KilnRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        protected static KilnRecipe read(RegistryByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
            BlockIngredient ingredient = BlockIngredient.Serializer.read(buf);
            int cookingTime = buf.readVarInt();
            List<ItemStack> drops = ItemStack.LIST_PACKET_CODEC.decode(buf);
            return new KilnRecipe(group, category, ingredient, cookingTime, drops);
        }

        protected static void write(RegistryByteBuf buf, KilnRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            BlockIngredient.Serializer.write(buf, recipe.ingredient);
            buf.writeVarInt(recipe.cookingTime);
            ItemStack.LIST_PACKET_CODEC.encode(buf, recipe.getDrops());
        }
    }

    public static class JsonBuilder implements CraftingRecipeJsonBuilder {
        protected CraftingRecipeCategory category = CraftingRecipeCategory.MISC;
        protected BlockIngredient ingredient;
        protected int cookingTime;
        protected String fromBlockName;
        protected DefaultedList<ItemStack> drops = DefaultedList.of();
        @Nullable
        protected String group;

        public static JsonBuilder create(Block input) {
            JsonBuilder obj = new JsonBuilder();
            obj.ingredient = BlockIngredient.fromBlock(input);
            obj.fromBlockName = Registries.BLOCK.getId(input).getPath();
            obj.cookingTime = KilnRecipe.DEFAULT_COOKING_TIME;
            return obj;
        }

        public static JsonBuilder create(TagKey<Block> inputTag) {
            JsonBuilder obj = new JsonBuilder();
            obj.ingredient = BlockIngredient.fromTag(inputTag);
            obj.fromBlockName = inputTag.id().getPath();
            obj.cookingTime = KilnRecipe.DEFAULT_COOKING_TIME;
            return obj;
        }

        public JsonBuilder category(CraftingRecipeCategory category) {
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

        public JsonBuilder drops(ItemConvertible item, int count) {
            return this.drops(new ItemStack(item, count));
        }

        public JsonBuilder drops(ItemConvertible item) {
            return this.drops(item, 1);
        }

        public JsonBuilder result(ItemStack itemStack) {
            this.drops.add(itemStack);
            return this;
        }

        public JsonBuilder result(ItemConvertible item, int count) {
            this.drops.add(new ItemStack(item, count));
            return this;
        }

        public JsonBuilder result(ItemConvertible item) {
            return this.result(item, 1);
        }

        @Override
        public JsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
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
        public void addToDefaults(Identifier recipeId) {
            if(this.isDefaultRecipe) {
                EmiDefaultsGenerator.addBwtRecipe(recipeId);
            }
        }

        @Override
        public Item getOutputItem() {
            return drops.get(0).getItem();
        }

        @Override
        public void offerTo(RecipeExporter exporter) {
            this.offerTo(exporter, "bwt:kiln_cook_" + fromBlockName);
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            this.addToDefaults(recipeId);
            Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            KilnRecipe kilnRecipe = new KilnRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredient,
                    this.cookingTime,
                    this.drops
            );
            exporter.accept(recipeId, kilnRecipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/" + this.category.asString() + "/")));
        }
    }
}
