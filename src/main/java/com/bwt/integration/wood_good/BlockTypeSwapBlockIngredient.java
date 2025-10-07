package com.bwt.integration.wood_good;

import com.bwt.recipes.BlockIngredient;
import com.mojang.datafixers.util.Either;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.minecraft.block.Block;

import java.util.stream.Stream;

public class BlockTypeSwapBlockIngredient extends BlockIngredient {
    protected BlockTypeSwapBlockIngredient(BlockIngredient original, BlockType fromType, BlockType toType) {
        super(convertEntries(original.dualStreamEntries(), fromType, toType));
    }

    protected static Stream<? extends BlockIngredient.Entry> convertEntries(Stream<Either<BlockEntry, TagEntry>> entries, BlockType fromType, BlockType toType) {
        BlockTypeRegistry<BlockType> fromRegistry = fromType.getRegistry();
        return entries.flatMap(either -> either.map(
                blockEntry -> Stream.of(blockEntry.block()),
                TagEntry::getBlocks
        )).map(block -> fromRegistry.getBlockTypeOf(block) == fromType
                ? BlockType.changeBlockType(block, fromType, toType)
                : block
        ).map(BlockIngredient.BlockEntry::new);
    }
}
