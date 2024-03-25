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

public class TurntableRecipe implements Recipe<Inventory> {
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
    public ItemStack craft(@Nullable Inventory inventory, DynamicRegistryManager registryManager) {
        return getResult(registryManager);
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
        return output.asItem().getDefaultStack();
    }

    public static class Serializer implements RecipeSerializer<TurntableRecipe> {
        private final TurntableRecipe.RecipeFactory<TurntableRecipe> recipeFactory;
        private final Codec<TurntableRecipe> codec;

        public Serializer(TurntableRecipe.RecipeFactory<TurntableRecipe> recipeFactory) {
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
                            Identifier.CODEC
                                    .fieldOf("output")
                                    .forGetter(recipe -> Registries.BLOCK.getId(recipe.output)),
                            ItemStack.RECIPE_RESULT_CODEC
                                    .listOf()
                                    .fieldOf("drops")
                                    .forGetter(TurntableRecipe::getDrops)
                    ).apply(instance, (group, category, ingredient, outputId, drops) -> recipeFactory.create(group, category, ingredient, Registries.BLOCK.get(outputId), drops))
            );
        }

        @Override
        public Codec<TurntableRecipe> codec() {
            return codec;
        }

        @Override
        public TurntableRecipe read(PacketByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
            BlockIngredient ingredient = BlockIngredient.SERIALIZER.read(buf);
            Block output = Registries.BLOCK.get(buf.readIdentifier());
            int dropsSize = buf.readVarInt();
            DefaultedList<ItemStack> drops = DefaultedList.ofSize(dropsSize, ItemStack.EMPTY);
            drops.replaceAll(ignored -> buf.readItemStack());
            return this.recipeFactory.create(group, category, ingredient, output, drops);
        }

        @Override
        public void write(PacketByteBuf buf, TurntableRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            BlockIngredient.SERIALIZER.write(buf, recipe.ingredient);
            buf.writeIdentifier(Registries.BLOCK.getId(recipe.output));
            buf.writeVarInt(recipe.drops.size());
            for (ItemStack stack : recipe.getDrops()) {
                buf.writeItemStack(stack);
            }
        }
    }

    public interface RecipeFactory<T extends TurntableRecipe> {
        T create(String group, CraftingRecipeCategory category, BlockIngredient ingredient, Block output, List<ItemStack> drops);
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

        TurntableRecipe.RecipeFactory<TurntableRecipe> getRecipeFactory() {
            return TurntableRecipe::new;
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

        @Override
        public Item getOutputItem() {
            return output.asItem();
        }

        @Override
        public void offerTo(RecipeExporter exporter) {
            this.offerTo(exporter, "turntable_" + fromBlockName);
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            TurntableRecipe turntableRecipe = this.getRecipeFactory().create(
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
