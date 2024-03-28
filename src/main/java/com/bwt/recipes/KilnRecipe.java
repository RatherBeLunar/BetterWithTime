package com.bwt.recipes;

import com.bwt.blocks.BwtBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class KilnRecipe implements Recipe<Inventory> {
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
    public boolean matches(@Nullable Inventory inventory, World world) {
        return true;
    }

    public boolean matches(Block block) {
        return this.ingredient.test(block);
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
    public ItemStack craft(@Nullable Inventory inventory, DynamicRegistryManager registryManager) {
        return getResult(registryManager);
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
        return drops.get(0);
    }

    public static class Serializer implements RecipeSerializer<KilnRecipe> {
        private final KilnRecipe.RecipeFactory<KilnRecipe> recipeFactory;
        private final Codec<KilnRecipe> codec;

        public Serializer(KilnRecipe.RecipeFactory<KilnRecipe> recipeFactory) {
            this.recipeFactory = recipeFactory;
            this.codec = RecordCodecBuilder.create(
                    instance->instance.group(
                            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "group", "")
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
                            ItemStack.RECIPE_RESULT_CODEC
                                    .listOf()
                                    .fieldOf("drops")
                                    .forGetter(KilnRecipe::getDrops)
                    ).apply(instance, recipeFactory::create)
            );
        }

        @Override
        public Codec<KilnRecipe> codec() {
            return codec;
        }

        @Override
        public KilnRecipe read(PacketByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
            BlockIngredient ingredient = BlockIngredient.SERIALIZER.read(buf);
            int cookingTime = buf.readVarInt();
            int dropsSize = buf.readVarInt();
            DefaultedList<ItemStack> drops = DefaultedList.ofSize(dropsSize, ItemStack.EMPTY);
            drops.replaceAll(ignored -> buf.readItemStack());
            return this.recipeFactory.create(group, category, ingredient, cookingTime, drops);
        }

        @Override
        public void write(PacketByteBuf buf, KilnRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            BlockIngredient.SERIALIZER.write(buf, recipe.ingredient);
            buf.writeVarInt(recipe.cookingTime);
            buf.writeVarInt(recipe.drops.size());
            for (ItemStack stack : recipe.getDrops()) {
                buf.writeItemStack(stack);
            }
        }
    }

    public interface RecipeFactory<T extends KilnRecipe> {
        T create(String group, CraftingRecipeCategory category, BlockIngredient ingredient, int cookingTime, List<ItemStack> drops);
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

        KilnRecipe.RecipeFactory<KilnRecipe> getRecipeFactory() {
            return KilnRecipe::new;
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

        @Override
        public Item getOutputItem() {
            return drops.get(0).getItem();
        }

        @Override
        public void offerTo(RecipeExporter exporter) {
            this.offerTo(exporter, "kiln_cook_" + fromBlockName);
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            KilnRecipe kilnRecipe = this.getRecipeFactory().create(
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
