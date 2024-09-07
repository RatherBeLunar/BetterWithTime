package com.bwt.recipes.soul_forge;

import com.bwt.mixin.accessors.ShapedRecipeJsonBuilderAccessorMixin;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.SoulForgeRecipe;
import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class SoulForgeShapedRecipe extends ShapedRecipe implements SoulForgeRecipe {
    protected RawShapedRecipe raw;

    public SoulForgeShapedRecipe(String group, CraftingRecipeCategory category, RawShapedRecipe raw, ItemStack result, boolean showNotification) {
        super(group, category, raw, result, showNotification);
        this.raw = raw;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.SOUL_FORGE_SHAPED_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BwtRecipes.SOUL_FORGE_RECIPE_TYPE;
    }

    @Override
    public boolean fits(int width, int height) {
        return width == 4 && height == 4;
    }

    public RawShapedRecipe getRaw() {
        return raw;
    }

    public ItemStack getResult() {
        return getResult(null);
    }

    public static class Serializer implements RecipeSerializer<SoulForgeShapedRecipe> {
        public static final MapCodec<SoulForgeShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
                        CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(ShapedRecipe::getCategory),
                        RawShapedRecipe.CODEC.forGetter(SoulForgeShapedRecipe::getRaw),
                        ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(SoulForgeShapedRecipe::getResult),
                        Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification)
                ).apply(instance, SoulForgeShapedRecipe::new));
        public static final PacketCodec<RegistryByteBuf, SoulForgeShapedRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                SoulForgeShapedRecipe.Serializer::write,
                SoulForgeShapedRecipe.Serializer::read
        );

        @Override
        public MapCodec<SoulForgeShapedRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, SoulForgeShapedRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static SoulForgeShapedRecipe read(RegistryByteBuf buf) {
            String string = buf.readString();
            CraftingRecipeCategory craftingRecipeCategory = buf.readEnumConstant(CraftingRecipeCategory.class);
            RawShapedRecipe rawShapedRecipe = RawShapedRecipe.PACKET_CODEC.decode(buf);
            ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
            boolean bl = buf.readBoolean();
            return new SoulForgeShapedRecipe(string, craftingRecipeCategory, rawShapedRecipe, itemStack, bl);
        }

        private static void write(RegistryByteBuf buf, SoulForgeShapedRecipe recipe) {
            buf.writeString(recipe.getGroup());
            buf.writeEnumConstant(recipe.getCategory());
            RawShapedRecipe.PACKET_CODEC.encode(buf, recipe.raw);
            ItemStack.PACKET_CODEC.encode(buf, recipe.getResult());
            buf.writeBoolean(recipe.showNotification());
        }
    }

    public static class JsonBuilder extends ShapedRecipeJsonBuilder {
        public JsonBuilder(RecipeCategory category, ItemConvertible output, int count) {
            super(category, output, count);
        }

        public static JsonBuilder create(RecipeCategory category, ItemConvertible output) {
            return JsonBuilder.create(category, output, 1);
        }

        public static JsonBuilder create(RecipeCategory category, ItemConvertible output, int count) {
            return new JsonBuilder(category, output, count);
        }

        private RawShapedRecipe validate(Identifier recipeId) {
            ShapedRecipeJsonBuilderAccessorMixin accessor = ((ShapedRecipeJsonBuilderAccessorMixin) this);
            if (accessor.getCriteria().isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + recipeId);
            }
            return RawShapedRecipe.create(accessor.getInputs(), accessor.getPattern());
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            recipeId = Id.of(recipeId.getPath());
            ShapedRecipeJsonBuilderAccessorMixin accessor = ((ShapedRecipeJsonBuilderAccessorMixin) this);
            RawShapedRecipe rawShapedRecipe = validate(recipeId);
            Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            accessor.getCriteria().forEach(builder::criterion);
            SoulForgeShapedRecipe shapedRecipe = new SoulForgeShapedRecipe(Objects.requireNonNullElse(accessor.getGroup(), ""), CraftingRecipeJsonBuilder.toCraftingCategory(accessor.getCategory()), rawShapedRecipe, new ItemStack(accessor.getOutput(), accessor.getCount()), accessor.getShowNotification());
            exporter.accept(recipeId, shapedRecipe, builder.build(recipeId.withPrefixedPath("recipes/" + accessor.getCategory().getName() + "/")));
        }
    }
}

