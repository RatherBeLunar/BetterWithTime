package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.CornerBlock;
import com.bwt.blocks.MouldingBlock;
import com.bwt.blocks.SidingBlock;
import com.bwt.gamerules.BwtGameRules;
import com.bwt.items.BwtItems;
import com.bwt.sounds.BwtSoundEvents;
import com.bwt.utils.DyeUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.sound.SoundEvent;
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
        addSubtitles(translationBuilder);

        translationBuilder.add(BwtGameRules.VANILLA_HOPPERS_DISABLED.getTranslationKey(), "Disable Vanilla Hopper Transfer");

        for (SidingBlock sidingBlock : BwtBlocks.sidingBlocks) {
            translationBuilder.add(sidingBlock, nameKeyToTitleCase(sidingBlock.fullBlock.getName().getString().replaceFirst("_planks", "") + "_siding"));
        }
        for (MouldingBlock mouldingBlock : BwtBlocks.mouldingBlocks) {
            translationBuilder.add(mouldingBlock, nameKeyToTitleCase(mouldingBlock.fullBlock.getName().getString().replaceFirst("_planks", "") + "_moulding"));;
        }
        for (CornerBlock cornerBlock : BwtBlocks.cornerBlocks) {
            translationBuilder.add(cornerBlock, nameKeyToTitleCase(cornerBlock.fullBlock.getName().getString().replaceFirst("_planks", "") + "_corner"));;
        }
        translationBuilder.add(BwtBlocks.unfiredCrucibleBlock, "Unfired Crucible");
        translationBuilder.add(BwtBlocks.unfiredPlanterBlock, "Unfired Planter");
        translationBuilder.add(BwtBlocks.unfiredVaseBlock, "Unfired Vase");
        translationBuilder.add(BwtBlocks.unfiredUrnBlock, "Unfired Urn");
        translationBuilder.add(BwtBlocks.unfiredMouldBlock, "Unfired Mould");
        BwtBlocks.vaseBlocks.entrySet().stream().sorted(DyeUtils.COMPARE_DYE_COLOR_ENTRY).forEach(entry -> translationBuilder.add(entry.getValue(), nameKeyToTitleCase(entry.getKey().getName() + "_vase")));
        translationBuilder.add(BwtBlocks.soilPlanterBlock, "Soil Planter");
        translationBuilder.add(BwtBlocks.soulSandPlanterBlock, "Soul Sand Planter");
        translationBuilder.add(BwtBlocks.grassPlanterBlock, "Grass Planter");
        translationBuilder.add(BwtBlocks.stoneDetectorRailBlock, "Stone Detector Rail");
        translationBuilder.add(BwtBlocks.obsidianDetectorRailBlock, "Obsidian Detector Rail");
        translationBuilder.add(BwtBlocks.soulForgeBlock, "Soul Forge");
        translationBuilder.add(BwtItems.screwItem, "Screw");
        translationBuilder.add(BwtItems.breedingHarnessItem, "Breeding Harness");
        translationBuilder.add(BwtItems.refinedPickaxeItem, "Refined Pickaxe");
        translationBuilder.add(BwtItems.refinedShovelItem, "Refined Shovel");
        translationBuilder.add(BwtItems.refinedAxeItem, "Refined Axe");
        translationBuilder.add(BwtItems.refinedHoeItem, "Refined Hoe");
        translationBuilder.add(BwtItems.refinedSwordItem, "Refined Sword");
        translationBuilder.add(BwtItems.mattockItem, "Mattock");
        translationBuilder.add(BwtItems.battleAxeItem, "Battle Axe");

        // Load an existing language file.
        try {
            Path existingFilePath = dataOutput.getModContainer().findPath("assets/bwt/lang/en_us.existing.json").get();
            translationBuilder.add(existingFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }

    protected void addSubtitles(TranslationBuilder translationBuilder) {
        addSubtitle(BwtSoundEvents.MECH_BANG, "Mechanical device operates", translationBuilder);
        addSubtitle(BwtSoundEvents.MECH_EXPLODE, "Mechanical device explodes", translationBuilder);
        addSubtitle(BwtSoundEvents.MECH_CREAK, "Mechanical device creaks", translationBuilder);
        addSubtitle(BwtSoundEvents.ANCHOR_RETRACT, "Anchor retracts rope", translationBuilder);
        addSubtitle(BwtSoundEvents.BELLOWS_COMPRESS, "Bellows compresses", translationBuilder);
        addSubtitle(BwtSoundEvents.COMPANION_CUBE_DEATH, "Companion cube dies. You monster.", translationBuilder);
        addSubtitle(BwtSoundEvents.GEAR_BOX_ACTIVATE, "Gear box activates", translationBuilder);
        addSubtitle(BwtSoundEvents.HAND_CRANK_CLICK, "Hand crank activates", translationBuilder);
        addSubtitle(BwtSoundEvents.HIBACHI_IGNITE, "Hibachi ignites", translationBuilder);
        addSubtitle(BwtSoundEvents.DETECTOR_CLICK, "Detector block toggles", translationBuilder);
        addSubtitle(BwtSoundEvents.SOUL_CONVERSION, "Souls transmogrify", translationBuilder);
        addSubtitle(BwtSoundEvents.MILL_STONE_GRIND, "Mill stone grinds", translationBuilder);
        addSubtitle(BwtSoundEvents.TURNTABLE_SETTING_CLICK, "Turntable setting changes", translationBuilder);
        addSubtitle(BwtSoundEvents.TURNTABLE_TURNING_CLICK, "Turntable rotates", translationBuilder);
        addSubtitle(BwtSoundEvents.WOLF_DUNG_PRODUCTION, "Wolf produces dung", translationBuilder);
        addSubtitle(BwtSoundEvents.WOLF_DUNG_EFFORT, "Wolf growls", translationBuilder);
    }

    protected void addSubtitle(SoundEvent soundEvent, String value, TranslationBuilder translationBuilder) {
        translationBuilder.add(soundEvent.getId().withPrefixedPath("subtitles."), value);
    }

    public static String nameKeyToTitleCase(String snakeString) {
        String[] split = snakeString.split("\\.");
        snakeString = split[split.length - 1];
        return Arrays.stream(snakeString.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }
}
