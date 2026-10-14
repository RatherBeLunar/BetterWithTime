package com.bwt.recipes.soul_forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

public record SoulForgeShapedCraftingRecipeDisplay(int width, int height, List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation)
        implements RecipeDisplay {
    public static final MapCodec<SoulForgeShapedCraftingRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codec.INT.fieldOf("width").forGetter(SoulForgeShapedCraftingRecipeDisplay::width),
                            Codec.INT.fieldOf("height").forGetter(SoulForgeShapedCraftingRecipeDisplay::height),
                            SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(SoulForgeShapedCraftingRecipeDisplay::ingredients),
                            SlotDisplay.CODEC.fieldOf("result").forGetter(SoulForgeShapedCraftingRecipeDisplay::result),
                            SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(SoulForgeShapedCraftingRecipeDisplay::craftingStation)
                    )
                    .apply(instance, SoulForgeShapedCraftingRecipeDisplay::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SoulForgeShapedCraftingRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SoulForgeShapedCraftingRecipeDisplay::width,
            ByteBufCodecs.VAR_INT,
            SoulForgeShapedCraftingRecipeDisplay::height,
            SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()),
            SoulForgeShapedCraftingRecipeDisplay::ingredients,
            SlotDisplay.STREAM_CODEC,
            SoulForgeShapedCraftingRecipeDisplay::result,
            SlotDisplay.STREAM_CODEC,
            SoulForgeShapedCraftingRecipeDisplay::craftingStation,
            SoulForgeShapedCraftingRecipeDisplay::new
    );
    public static final RecipeDisplay.Type<SoulForgeShapedCraftingRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

    public SoulForgeShapedCraftingRecipeDisplay {
        if (ingredients.size() != width * height) {
            throw new IllegalArgumentException("Invalid shaped recipe display contents");
        }
    }

    @Override
    public RecipeDisplay.Type<SoulForgeShapedCraftingRecipeDisplay> type() {
        return TYPE;
    }

    @Override
    public boolean isEnabled(FeatureFlagSet featureFlagSet) {
        return this.ingredients.stream().allMatch(slotDisplay -> slotDisplay.isEnabled(featureFlagSet)) && RecipeDisplay.super.isEnabled(featureFlagSet);
    }
}