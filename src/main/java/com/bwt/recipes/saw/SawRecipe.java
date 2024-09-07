package com.bwt.recipes.saw;

import com.bwt.blocks.BwtBlocks;
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
import java.util.stream.Collectors;

public class SawRecipe implements Recipe<SawRecipeInput> {
    protected final String group;
    protected final CraftingRecipeCategory category;
    final BlockIngredient ingredient;
    protected final DefaultedList<ItemStack> results;

    public SawRecipe(String group, CraftingRecipeCategory category, BlockIngredient ingredient, List<ItemStack> results) {
        this.group = group;
        this.category = category;
        this.ingredient = ingredient;
        this.results = DefaultedList.copyOf(ItemStack.EMPTY, results.toArray(new ItemStack[0]));
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(BwtBlocks.sawBlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.SAW_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(SawRecipeInput input, World world) {
        return this.ingredient.test(input.block());
    }

    @Override
    public boolean fits(int width, int height) {
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
    public ItemStack craft(SawRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return getResult(lookup);
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return results.get(0);
    }

    public static class Serializer implements RecipeSerializer<SawRecipe> {
        protected static final MapCodec<SawRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingRecipeCategory.CODEC.fieldOf("category")
                                .orElse(CraftingRecipeCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        BlockIngredient.Serializer.CODEC
                                .fieldOf("ingredient")
                                .forGetter(recipe -> recipe.ingredient),
                        ItemStack.VALIDATED_CODEC
                                .listOf()
                                .fieldOf("drops")
                                .forGetter(SawRecipe::getResults)
                ).apply(instance, SawRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, SawRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read
        );


        public Serializer() {}

        @Override
        public MapCodec<SawRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, SawRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        public static SawRecipe read(RegistryByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
            BlockIngredient ingredient = BlockIngredient.Serializer.read(buf);
            List<ItemStack> drops = ItemStack.LIST_PACKET_CODEC.decode(buf);
            return new SawRecipe(group, category, ingredient, drops);
        }

        public static void write(RegistryByteBuf buf, SawRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            BlockIngredient.Serializer.write(buf, recipe.ingredient);
            ItemStack.LIST_PACKET_CODEC.encode(buf, recipe.getResults());
        }
    }

    public interface RecipeFactory<T extends SawRecipe> {
        T create(String group, CraftingRecipeCategory category, BlockIngredient ingredient, List<ItemStack> results);
    }

    public static class JsonBuilder implements CraftingRecipeJsonBuilder {
        protected CraftingRecipeCategory category = CraftingRecipeCategory.MISC;
        protected BlockIngredient ingredient;
        protected String fromBlockName;
        protected DefaultedList<ItemStack> results = DefaultedList.of();
        @Nullable
        protected String group;

        public static JsonBuilder create(Block block) {
            JsonBuilder obj = new JsonBuilder();
            obj.ingredient = BlockIngredient.fromBlock(block);
            obj.fromBlockName = Registries.BLOCK.getId(block).getPath();
            return obj;
        }

        public static JsonBuilder create(TagKey<Block> blockTag) {
            JsonBuilder obj = new JsonBuilder();
            obj.ingredient = BlockIngredient.fromTag(blockTag);
            obj.fromBlockName = blockTag.id().getPath();
            return obj;
        }

        public static void dropsSelf(Block block, RecipeExporter exporter) {
            create(block).result(block.asItem()).offerTo(exporter);
        }

        RecipeFactory<SawRecipe> getRecipeFactory() {
            return SawRecipe::new;
        }

        public JsonBuilder category(CraftingRecipeCategory category) {
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

        public JsonBuilder result(ItemConvertible item, int count) {
            this.results.add(new ItemStack(item, count));
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

        @Override
        public Item getOutputItem() {
            return results.get(0).getItem();
        }

        @Override
        public void offerTo(RecipeExporter exporter) {
            this.offerTo(exporter, "bwt:saw_" + fromBlockName);
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            SawRecipe sawRecipe = this.getRecipeFactory().create(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredient,
                    this.results
            );
            exporter.accept(recipeId, sawRecipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/" + this.category.asString() + "/")));
        }
    }
}
