package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.CornerBlock;
import com.bwt.blocks.MouldingBlock;
import com.bwt.blocks.SidingBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public class LangGenerator extends FabricLanguageProvider {
    public LangGenerator(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        for (SidingBlock sidingBlock : BwtBlocks.sidingBlocks) {
            translationBuilder.add(sidingBlock, nameKeyToTitleCase(sidingBlock.fullBlock.getName().getString().replaceFirst("_planks", "") + "_siding"));
        }
        for (MouldingBlock mouldingBlock : BwtBlocks.mouldingBlocks) {
            translationBuilder.add(mouldingBlock, nameKeyToTitleCase(mouldingBlock.fullBlock.getName().getString().replaceFirst("_planks", "") + "_moulding"));;
        }
        for (CornerBlock cornerBlock : BwtBlocks.cornerBlocks) {
            translationBuilder.add(cornerBlock, nameKeyToTitleCase(cornerBlock.fullBlock.getName().getString().replaceFirst("_planks", "") + "_corner"));;
        }

        // Load an existing language file.
        try {
            Path existingFilePath = dataOutput.getModContainer().findPath("assets/bwt/lang/en_us.existing.json").get();
            translationBuilder.add(existingFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }

    public static String nameKeyToTitleCase(String snakeString) {
        String[] split = snakeString.split("\\.");
        snakeString = split[split.length - 1];
        return Arrays.stream(snakeString.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }
}
