package com.bwt.recipes.turntable;

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

public class TurntableRecipe implements Recipe<TurntableRecipeInput> {
    protected final String group;
    protected final CraftingRecipeCategory category;
    final BlockIngredient ingredient;
    final Block output;
    protected final DefaultedList<ItemStack> drops;

    public TurntableRecipe(String group, CraftingRecipeCategory category, BlockIngredient ingredient, Block output, List<ItemStack> drops) {
        this.group = group;
        this.category = category;
        this.ingredient = ingredient;
        this.output = output;
        this.drops = DefaultedList.copyOf(ItemStack.EMPTY, drops.toArray(new ItemStack[0]));
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(BwtBlocks.turntableBlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.TURNTABLE_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(TurntableRecipeInput input, World world) {
        return this.ingredient.test(input.block());
    }


    @Override
    public boolean fits(int width, int height) {
        return true;
    }


    public BlockIngredient getIngredient() {
        return ingredient;
    }
    public Block getOutput() {
        return output;
    }

    public DefaultedList<ItemStack> getDrops() {
        return DefaultedList.copyOf(ItemStack.EMPTY, drops.stream().map(ItemStack::copy).toList().toArray(new ItemStack[0]));
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeType<?> getType() {
        return BwtRecipes.TURNTABLE_RECIPE_TYPE;
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
    public ItemStack craft(TurntableRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return getResult(lookup);
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return output.asItem().getDefaultStack();
    }

    public static class Serializer implements RecipeSerializer<TurntableRecipe> {
        protected static final MapCodec<TurntableRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance->instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingRecipeCategory.CODEC.fieldOf("category")
                                .orElse(CraftingRecipeCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        BlockIngredient.Serializer.CODEC
                                .fieldOf("ingredient")
                                .forGetter(recipe -> recipe.ingredient),
                        Identifier.CODEC
                                .fieldOf("output")
                                .forGetter(recipe -> Registries.BLOCK.getId(recipe.output)),
                        ItemStack.VALIDATED_CODEC
                                .listOf()
                                .fieldOf("drops")
                                .forGetter(TurntableRecipe::getDrops)
                ).apply(instance, (group, category, ingredient, outputId, drops) -> new TurntableRecipe(group, category, ingredient, Registries.BLOCK.get(outputId), drops))
        );
        public static final PacketCodec<RegistryByteBuf, TurntableRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read
        );

        public Serializer() {}

        @Override
        public MapCodec<TurntableRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, TurntableRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        public static TurntableRecipe read(RegistryByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
            BlockIngredient ingredient = BlockIngredient.Serializer.read(buf);
            Block output = Registries.BLOCK.get(buf.readIdentifier());
            List<ItemStack> drops = ItemStack.LIST_PACKET_CODEC.decode(buf);
            return new TurntableRecipe(group, category, ingredient, output, drops);
        }

        public static void write(RegistryByteBuf buf, TurntableRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            BlockIngredient.Serializer.write(buf, recipe.ingredient);
            buf.writeIdentifier(Registries.BLOCK.getId(recipe.output));
            ItemStack.LIST_PACKET_CODEC.encode(buf, recipe.getDrops());
        }
    }

    public static class JsonBuilder implements CraftingRecipeJsonBuilder {
        protected CraftingRecipeCategory category = CraftingRecipeCategory.MISC;
        protected BlockIngredient ingredient;
        protected Block output;
        protected String fromBlockName;
        protected DefaultedList<ItemStack> drops = DefaultedList.of();
        @Nullable
        protected String group;

        public static JsonBuilder create(Block input, Block output) {
            JsonBuilder obj = new JsonBuilder();
            obj.ingredient = BlockIngredient.fromBlock(input);
            obj.fromBlockName = Registries.BLOCK.getId(input).getPath();
            obj.output = output;
            return obj;
        }

        public static JsonBuilder create(TagKey<Block> inputTag, Block output) {
            JsonBuilder obj = new JsonBuilder();
            obj.ingredient = BlockIngredient.fromTag(inputTag);
            obj.fromBlockName = inputTag.id().getPath();
            obj.output = output;
            return obj;
        }

        public JsonBuilder category(CraftingRecipeCategory category) {
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
            return output.asItem();
        }

        @Override
        public void offerTo(RecipeExporter exporter) {
            this.offerTo(exporter, "bwt:turntable_" + fromBlockName);
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            this.addToDefaults(recipeId);
            Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            TurntableRecipe turntableRecipe = new TurntableRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredient,
                    this.output,
                    this.drops
            );
            exporter.accept(recipeId, turntableRecipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/" + this.category.asString() + "/")));
        }
    }
}
