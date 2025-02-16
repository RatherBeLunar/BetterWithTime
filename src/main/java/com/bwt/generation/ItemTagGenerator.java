package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import com.bwt.tags.BwtBlockTags;
import com.bwt.tags.BwtItemTags;
import com.bwt.tags.CompatibilityTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {
    public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, BlockTagGenerator blockTagGenerator) {
        super(output, completableFuture, blockTagGenerator);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        copy(BlockTags.RAILS, ItemTags.RAILS);
        copy(BlockTags.SLABS, ItemTags.SLABS);
        copy(BlockTags.DIRT, ItemTags.DIRT);
        copy(BlockTags.SOUL_FIRE_BASE_BLOCKS, ItemTags.SOUL_FIRE_BASE_BLOCKS);
        copy(BlockTags.WOOL, ItemTags.WOOL);
        copy(BwtBlockTags.BLOOD_WOOD_LOGS, BwtItemTags.BLOOD_WOOD_LOGS);
        copy(BlockTags.LOGS, ItemTags.LOGS);
        copy(BlockTags.LEAVES, ItemTags.LEAVES);
        copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        copy(BlockTags.PLANKS, ItemTags.PLANKS);
        copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
        copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        copy(BwtBlockTags.WOODEN_SIDING_BLOCKS, BwtItemTags.WOODEN_SIDING_BLOCKS);
        copy(BwtBlockTags.WOODEN_MOULDING_BLOCKS, BwtItemTags.WOODEN_MOULDING_BLOCKS);
        copy(BwtBlockTags.WOODEN_CORNER_BLOCKS, BwtItemTags.WOODEN_CORNER_BLOCKS);
        copy(BwtBlockTags.SIDING_BLOCKS, BwtItemTags.SIDING_BLOCKS);
        copy(BwtBlockTags.MOULDING_BLOCKS, BwtItemTags.MOULDING_BLOCKS);
        copy(BwtBlockTags.CORNER_BLOCKS, BwtItemTags.CORNER_BLOCKS);
        copy(BwtBlockTags.WOODEN_COLUMN_BLOCKS, BwtItemTags.WOODEN_COLUMN_BLOCKS);
        copy(BwtBlockTags.COLUMN_BLOCKS, BwtItemTags.COLUMN_BLOCKS);
        copy(BwtBlockTags.WOODEN_PEDESTAL_BLOCKS, BwtItemTags.WOODEN_PEDESTAL_BLOCKS);
        copy(BwtBlockTags.PEDESTAL_BLOCKS, BwtItemTags.PEDESTAL_BLOCKS);
        copy(BwtBlockTags.WOODEN_TABLE_BLOCKS, BwtItemTags.WOODEN_TABLE_BLOCKS);
        copy(BwtBlockTags.TABLE_BLOCKS, BwtItemTags.TABLE_BLOCKS);
        copy(BwtBlockTags.VASES, BwtItemTags.VASES);
        copy(BwtBlockTags.WOOL_SLABS, BwtItemTags.WOOL_SLABS);

        getOrCreateTagBuilder(BwtItemTags.SAW_DUSTS).add(BwtItems.sawDustItem, BwtItems.soulDustItem);
        getOrCreateTagBuilder(BwtItemTags.MINING_CHARGE_IMMUNE)
                .forceAddTag(ConventionalItemTags.ORES)
                .forceAddTag(ConventionalItemTags.RAW_MATERIALS)
                .forceAddTag(ConventionalItemTags.REDSTONE_DUSTS)
                .forceAddTag(ConventionalItemTags.EMERALD_GEMS)
                .forceAddTag(ConventionalItemTags.LAPIS_GEMS)
                .forceAddTag(ConventionalItemTags.DIAMOND_GEMS)
                .forceAddTag(ConventionalItemTags.QUARTZ_GEMS)
                .forceAddTag(ItemTags.COALS)
                .add(Items.FLINT)
                .add(Items.ANCIENT_DEBRIS);

        getOrCreateTagBuilder(ItemTags.ARROWS).add(BwtItems.broadheadArrowItem, BwtItems.rottedArrowItem);

        getOrCreateTagBuilder(ItemTags.WOOL).add(BwtBlocks.companionSlabBlock.asItem());

        getOrCreateTagBuilder(ConventionalItemTags.RAW_MEAT_FOODS).add(BwtItems.wolfChopItem);
        getOrCreateTagBuilder(ConventionalItemTags.COOKED_MEAT_FOODS).add(BwtItems.cookedWolfChopItem);

        getOrCreateTagBuilder(ItemTags.PICKAXES).add(BwtItems.netheriteMattockItem);
        getOrCreateTagBuilder(ItemTags.SHOVELS).add(BwtItems.netheriteMattockItem);
        getOrCreateTagBuilder(ItemTags.AXES).add(BwtItems.netheriteBattleAxeItem);
        getOrCreateTagBuilder(ItemTags.SWORDS).add(BwtItems.netheriteBattleAxeItem);
        getOrCreateTagBuilder(ItemTags.BOW_ENCHANTABLE).add(BwtItems.compositeBowItem);

        getOrCreateTagBuilder(BwtItemTags.CAN_INFERNAL_ENCHANT).add(BwtItems.netheriteMattockItem, BwtItems.netheriteBattleAxeItem);

        addHopperFilters();
    }

    protected void addHopperFilters() {
        getOrCreateTagBuilder(BwtItemTags.STOKED_EXPLOSIVES)
                .add(
                        BwtItems.hellfireDustItem,
                        BwtItems.concentratedHellfireItem,
                        Items.GUNPOWDER,
                        Items.TNT
                );

        getOrCreateTagBuilder(BwtItemTags.PASSES_WICKER_FILTER)
                .add(
                        Items.BLAZE_POWDER,
                        BwtItems.coalDustItem,
                        Items.BLACK_DYE,
                        Items.BROWN_DYE,
                        Items.BLUE_DYE,
                        Items.CYAN_DYE,
                        Items.GRAY_DYE,
                        Items.GREEN_DYE,
                        Items.LIGHT_BLUE_DYE,
                        Items.LIGHT_GRAY_DYE,
                        Items.LIME_DYE,
                        Items.MAGENTA_DYE,
                        Items.ORANGE_DYE,
                        Items.PINK_DYE,
                        Items.PURPLE_DYE,
                        Items.RED_DYE,
                        Items.WHITE_DYE,
                        Items.YELLOW_DYE,
                        BwtItems.flourItem,
                        Items.GLOWSTONE_DUST,
                        BwtItems.groundNetherrackItem,
                        Items.GUNPOWDER,
                        BwtItems.hellfireDustItem,
                        Items.NETHER_WART,
                        BwtItems.potashItem,
                        Items.REDSTONE,
                        Items.SAND,
                        BwtItems.sawDustItem,
                        Items.WHEAT_SEEDS,
                        Items.BEETROOT_SEEDS,
                        Items.MELON_SEEDS,
                        Items.PUMPKIN_SEEDS,
                        Items.TORCHFLOWER_SEEDS,
                        BwtItems.hempSeedsItem,
                        BwtItems.soulDustItem,
                        Items.SUGAR
                );

        getOrCreateTagBuilder(BwtItemTags.PASSES_SLATS_FILTER)
                .addTag(BwtItemTags.PASSES_WICKER_FILTER)
                .forceAddTag(ItemTags.TRIM_TEMPLATES)
                .add(
                        // Arcane scrolls
                        BwtItems.fabricItem,
                        Items.GRAVEL,
                        BwtItems.hempItem,
                        BwtItems.hempFiberItem,
                        Items.LEATHER,
                        BwtItems.scouredLeatherItem,
                        BwtItems.tannedLeatherItem,
                        Items.MAP,
                        Items.PAPER,
                        Items.REPEATER,
                        Items.COMPARATOR,
                        BwtItems.strapItem,
                        Items.STRING,

                        Items.PRISMARINE_SHARD,
                        Items.DRIED_KELP
                );

        getOrCreateTagBuilder(BwtItemTags.PASSES_GRATE_FILTER)
                .forceAddTag(ItemTags.FLOWERS)
                .add(
                        Items.APPLE,
                        Items.GOLDEN_APPLE,
                        Items.ENCHANTED_GOLDEN_APPLE,
                        // Arcane scrolls
                        Items.BLAZE_POWDER,
                        BwtItems.broadheadItem,
                        Items.CHARCOAL,
                        Items.CLAY_BALL,
                        Items.COAL,
                        BwtItems.coalDustItem,
                        Items.COCOA_BEANS,
                        Items.COOKIE,
                        Items.DIAMOND,
                        BwtItems.donutItem,
                        BwtItems.dungItem,
                        Items.BLACK_DYE,
                        Items.BROWN_DYE,
                        Items.BLUE_DYE,
                        Items.CYAN_DYE,
                        Items.GRAY_DYE,
                        Items.GREEN_DYE,
                        Items.LIGHT_BLUE_DYE,
                        Items.LIGHT_GRAY_DYE,
                        Items.LIME_DYE,
                        Items.MAGENTA_DYE,
                        Items.ORANGE_DYE,
                        Items.PINK_DYE,
                        Items.PURPLE_DYE,
                        Items.RED_DYE,
                        Items.WHITE_DYE,
                        Items.YELLOW_DYE,
                        Items.EGG,
                        Items.ENDER_PEARL,
                        Items.ENDER_EYE,
                        Items.FEATHER,
                        BwtItems.filamentItem,
                        Items.FLINT,
                        BwtItems.flourItem,
                        Items.GHAST_TEAR,
                        Items.GLOWSTONE_DUST,
                        Items.GOLD_NUGGET,
                        Items.GRAVEL,
                        BwtItems.groundNetherrackItem,
                        Items.GUNPOWDER,
                        BwtItems.hellfireDustItem,
                        BwtItems.hempFiberItem,
                        Items.INK_SAC,
                        Items.GLOW_INK_SAC,
                        Items.IRON_NUGGET,
                        BwtItems.kibbleItem,
                        Items.BROWN_MUSHROOM,
                        Items.RED_MUSHROOM,
                        BwtItems.nethercoalItem,
                        Items.NETHER_STAR,
                        Items.NETHER_WART,
                        Items.PAPER,
                        BwtItems.potashItem,
                        Items.REDSTONE,
                        BwtItems.redstoneEyeItem,
                        Items.SAND,
                        BwtItems.sawDustItem,
                        Items.WHEAT_SEEDS,
                        Items.BEETROOT_SEEDS,
                        Items.MELON_SEEDS,
                        Items.PUMPKIN_SEEDS,
                        Items.TORCHFLOWER_SEEDS,
                        BwtItems.hempSeedsItem,
                        Items.SLIME_BALL,
                        Items.SNOWBALL,
                        BwtItems.soapItem,
                        BwtItems.soulDustItem,
                        Items.SPIDER_EYE,
                        Items.STRING,
                        Items.SUGAR,
                        Items.POISONOUS_POTATO,
                        Items.POTATO,
                        Items.CARROT,
                        Items.BEETROOT,

                        Items.NETHERITE_SCRAP,
                        Items.TURTLE_SCUTE,
                        Items.ARMADILLO_SCUTE,
                        Items.PRISMARINE_SHARD,
                        Items.PRISMARINE_CRYSTALS,
                        Items.NAUTILUS_SHELL,
                        Items.POPPED_CHORUS_FRUIT
            );

        getOrCreateTagBuilder(BwtItemTags.PASSES_TRAPDOOR_FILTER)
                .addTag(BwtItemTags.PASSES_GRATE_FILTER)
                .forceAddTag(ItemTags.ARROWS)
                .add(
                        BwtItems.broadheadArrowItem,
                        BwtItems.rottedArrowItem,
                        BwtItems.beltItem,
                        Items.BLAZE_ROD,
                        Items.BONE,
                        Items.CARROT_ON_A_STICK,
                        Items.FISHING_ROD,
                        BwtItems.haftItem,
                        BwtItems.ropeItem,
                        BwtItems.strapItem,
                        Items.SUGAR_CANE,
                        Items.WHEAT,

                        Items.WARPED_FUNGUS_ON_A_STICK,
                        Items.DRIED_KELP,
                        Items.CREEPER_BANNER_PATTERN,
                        Items.FLOWER_BANNER_PATTERN,
                        Items.PIGLIN_BANNER_PATTERN,
                        Items.GLOBE_BANNER_PATTERN,
                        Items.MOJANG_BANNER_PATTERN,
                        Items.SKULL_BANNER_PATTERN
                );

        getOrCreateTagBuilder(BwtItemTags.PASSES_IRON_BARS_FILTER)
                .addTag(BwtItemTags.PASSES_GRATE_FILTER)
                .forceAddTag(ItemTags.CANDLES)
                .forceAddTag(ConventionalItemTags.POTIONS)
                .forceAddTag(ItemTags.SAPLINGS)
                .forceAddTag(ItemTags.SIGNS)
                .forceAddTag(ItemTags.DECORATED_POT_SHERDS)
                .forceAddTag(ItemTags.TRIM_TEMPLATES)
                .add(
                        BwtItems.armorPlateItem,
                        Items.ARROW,
                        BwtItems.broadheadArrowItem,
                        BwtItems.rottedArrowItem,
                        BwtItems.beltItem,
                        Items.BLAZE_ROD,
                        Items.BONE,
                        Items.BOOK,
                        Items.KNOWLEDGE_BOOK,
                        Items.ENCHANTED_BOOK,
                        Items.WRITTEN_BOOK,
                        Items.EXPERIENCE_BOTTLE,
                        Items.GLASS_BOTTLE,
                        Items.HONEY_BOTTLE,
                        Items.BOWL,
                        Items.BREAD,
                        BwtItems.breedingHarnessItem,
                        Items.BREWING_STAND,
                        Items.BRICK,
                        Items.BUCKET,
                        BwtItems.canvasItem,
                        BwtBlocks.cauldronBlock.asItem(),
                        Items.CAULDRON,
                        Items.CLOCK,
                        Items.COMPASS,
                        Items.RECOVERY_COMPASS,
                        BwtItems.concentratedHellfireItem,
                        BwtItems.dynamiteItem,
                        Items.EMERALD,
                        BwtItems.fabricItem,
                        Items.FIRE_CHARGE,
                        Items.FIREWORK_ROCKET,
                        BwtItems.foulFoodItem,
                        BwtItems.gearItem,
                        Items.GLISTERING_MELON_SLICE,
                        BwtItems.glueItem,
                        BwtBlocks.grateBlock.asItem(),
                        BwtItems.haftItem,
                        BwtItems.hempItem,
                        Items.IRON_INGOT,
                        Items.COPPER_INGOT,
                        Items.GOLD_INGOT,
                        Items.NETHERITE_INGOT,
                        Items.LEATHER,
                        BwtItems.scouredLeatherItem,
                        BwtItems.tannedLeatherItem,
                        Items.MAGMA_CREAM,
                        Items.MAP,
                        Items.PORKCHOP,
                        Items.COOKED_PORKCHOP,
                        Items.CHICKEN,
                        Items.COOKED_CHICKEN,
                        Items.BEEF,
                        Items.COOKED_BEEF,
                        Items.RABBIT,
                        Items.COOKED_RABBIT,
                        Items.MUTTON,
                        Items.COOKED_MUTTON,
                        Items.SALMON,
                        Items.COOKED_SALMON,
                        Items.COD,
                        Items.COOKED_COD,
                        BwtItems.wolfChopItem,
                        BwtItems.cookedWolfChopItem,
                        Items.MELON_SLICE,
                        BwtItems.mouldItem,
                        Items.NETHER_BRICK,
                        // Nether Groth Spores
                        Items.QUARTZ,
                        BwtItems.netherSludgeItem,
                        BwtItems.paddingItem,
                        Items.PAINTING,
                        Items.PUMPKIN_PIE,
                        Items.REPEATER,
                        Items.COMPARATOR,
                        Items.REDSTONE_TORCH,
                        BwtItems.ropeItem,
                        Items.ROTTEN_FLESH,
                        BwtItems.screwItem,
                        BwtBlocks.slatsBlock.asItem(),
                        BwtItems.soulUrnItem,
                        BwtItems.strapItem,
                        Items.SUGAR_CANE,
                        BwtItems.tallowItem,
                        Items.TORCH,
                        Items.SOUL_TORCH,
                        // Urn
                        Items.WHEAT,
                        BwtBlocks.wickerPaneBlock.asItem(),

                        Items.HONEYCOMB,
                        Items.RABBIT_HIDE,
                        Items.DRIED_KELP,
                        Items.AMETHYST_SHARD,
                        Items.HEART_OF_THE_SEA,
                        Items.SHULKER_SHELL,
                        Items.ECHO_SHARD,
                        Items.PHANTOM_MEMBRANE,
                        Items.DRAGON_BREATH,
                        Items.RABBIT_FOOT
                );


        getOrCreateTagBuilder(BwtItemTags.PASSES_LADDER_FILTER)
                .addTag(BwtItemTags.PASSES_IRON_BARS_FILTER)
                .forceAddTag(ItemTags.TRIMMABLE_ARMOR)
                .forceAddTag(ItemTags.BEDS)
                .forceAddTag(ItemTags.BOATS)
                .forceAddTag(ItemTags.CHEST_BOATS)
                .forceAddTag(ItemTags.DOORS)
                .forceAddTag(ConventionalItemTags.ENTITY_WATER_BUCKETS)
                .forceAddTag(ItemTags.CREEPER_DROP_MUSIC_DISCS)
                .forceAddTag(ConventionalItemTags.MUSIC_DISCS)
                .forceAddTag(ItemTags.SAPLINGS)
                .forceAddTag(ConventionalItemTags.TOOLS)
                .forceAddTag(ConventionalItemTags.BOW_TOOLS)
                .forceAddTag(ConventionalItemTags.CROSSBOW_TOOLS)
                .forceAddTag(ConventionalItemTags.SHIELD_TOOLS)
                .add(
                        Items.WRITABLE_BOOK,
                        Items.LAVA_BUCKET,
                        Items.MILK_BUCKET,
                        Items.WATER_BUCKET,
                        Items.CARROT_ON_A_STICK,
                        Items.FISHING_ROD,
                        Items.MINECART,
                        Items.HOPPER_MINECART,
                        Items.CHEST_MINECART,
                        Items.MINECART,
                        Items.TNT_MINECART,
                        Items.FURNACE_MINECART,
                        Items.SADDLE,
                        BwtItems.sailItem,
                        BwtItems.waterWheelItem,
                        BwtItems.windmillItem,
                        BwtItems.woodBladeItem,

                        Items.ELYTRA,
                        Items.WARPED_FUNGUS_ON_A_STICK,
                        Items.SPYGLASS,
                        Items.GOAT_HORN,
                        Items.SUSPICIOUS_STEW,
                        Items.RABBIT_STEW,
                        Items.BEETROOT_SOUP,
                        // ?
                        Items.MUSHROOM_STEW,
                        Items.SHIELD,
                        Items.TOTEM_OF_UNDYING,
                        Items.CREEPER_BANNER_PATTERN,
                        Items.FLOWER_BANNER_PATTERN,
                        Items.PIGLIN_BANNER_PATTERN,
                        Items.GLOBE_BANNER_PATTERN,
                        Items.MOJANG_BANNER_PATTERN,
                        Items.SKULL_BANNER_PATTERN
                );
    }
}
