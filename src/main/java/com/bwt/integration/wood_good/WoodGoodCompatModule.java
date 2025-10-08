package com.bwt.integration.wood_good;

import com.bwt.blocks.*;
import com.bwt.utils.Id;
import net.mehvahdjukaar.every_compat.api.EveryCompatAPI;
import net.mehvahdjukaar.every_compat.api.SimpleModule;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.api.TabAddMode;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceGenTask;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class WoodGoodCompatModule extends SimpleModule {
    public record BlockClassSet<T extends MaterialInheritedBlock>(ClassConstructor<T> constructor, String name, Supplier<List<T>> blockListGetter, boolean recombinable) {
        public interface ClassConstructor<T> {
            T construct(Block.Settings settings, Block block);
        }

        public static BlockClassSet<SidingBlock> SIDING = new BlockClassSet<>(SidingBlock::new, "siding", () -> BwtBlocks.sidingBlocks, true);
        public static BlockClassSet<MouldingBlock> MOULDING = new BlockClassSet<>(MouldingBlock::new, "moulding", () -> BwtBlocks.mouldingBlocks, true);
        public static BlockClassSet<CornerBlock> CORNER = new BlockClassSet<>(CornerBlock::new, "corner", () -> BwtBlocks.cornerBlocks, true);
        public static BlockClassSet<TableBlock> TABLE = new BlockClassSet<>(TableBlock::new, "table", () -> BwtBlocks.tableBlocks, false);
        public static BlockClassSet<ColumnBlock> COLUMN = new BlockClassSet<>(ColumnBlock::new, "column", () -> BwtBlocks.columnBlocks, false);
        public static BlockClassSet<PedestalBlock> PEDESTAL = new BlockClassSet<>(PedestalBlock::new, "pedestal", () -> BwtBlocks.pedestalBlocks, false);

        public static Stream<SimpleEntrySet.Builder<WoodType, ? extends Block>> streamBuilders(WoodType ecWoodType) {
            return Stream.of(SIDING, MOULDING, CORNER, TABLE, COLUMN, PEDESTAL).map(entry -> entry.getBuilder(ecWoodType));
        }

        public T construct(WoodType woodType) {
            return constructor.construct(Utils.copyPropertySafe(woodType.planks), woodType.planks);
        }

        public SimpleEntrySet.Builder<WoodType, T> getBuilder(WoodType ecWoodType) {
            String woodTypeName = ecWoodType.toVanilla().name();

            SimpleEntrySet.Builder<WoodType, T> builder = SimpleEntrySet.builder(
                    WoodType.class,
                    "planks_" + this.name,
                    () -> this.blockListGetter.get().get(0),
                    () -> ecWoodType,
                    this::construct
            )
                    .addTag(Id.of("wooden_" + name + "_blocks"), RegistryKeys.BLOCK)
                    .addTag(Id.of("wooden_" + name + "_blocks"), RegistryKeys.ITEM)
                    .addTag(BlockTags.AXE_MINEABLE, RegistryKeys.ITEM)
                    .setTabKey(ItemGroups.BUILDING_BLOCKS)
                    .setTabMode(TabAddMode.AFTER_SAME_WOOD)
                    .addRecipe(Id.of("saw_" + woodTypeName + "_planks_" + name));

            // Recombining recipes for the minis
            if (recombinable) {
                builder = builder.addRecipe(Id.of("recombine_" + woodTypeName + "_planks_" + name));
            }
            // For items that can be crafted directly
            else {
                builder = builder.addRecipe(Id.of(woodTypeName + "_planks_" + name));
            }

            return builder;
        }
    }

    public WoodGoodCompatModule(String modId) {
        super(modId, modId, modId);

        net.minecraft.block.WoodType vanillaWoodType = net.minecraft.block.WoodType.stream().findFirst().orElseThrow();
        WoodType ecWoodType = WoodTypeRegistry.INSTANCE.getFromVanilla(vanillaWoodType);

        BlockClassSet.streamBuilders(ecWoodType).map(SimpleEntrySet.Builder::build).forEach(this::addEntry);
    }

    public static void init() {
        EveryCompatAPI.registerModule(new WoodGoodCompatModule(Id.MOD_ID));
    }

    @Override
    public void onModSetup() {
        SawRecipeTemplate.register();
        super.onModSetup();
    }

    @Override
    public void addDynamicClientResources(Consumer<ResourceGenTask> executor) {
        super.addDynamicClientResources(executor);
//        executor.accept(( manager, sink ) -> {
//            try {
//                stump.blocks.forEach((w, block) -> {
//                    Identifier id = Utils.getID(block);
//
//                    try (TextureImage topTexture = TextureImage.open(manager,
//                            RPUtils.findFirstBlockTextureLocation(manager, w.log, CompatSpritesHelper.LOOKS_LIKE_TOP_LOG_TEXTURE))) {
//
//                        Identifier newId = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace("block/oak_stump_top", w, id, "oak"));
//
//                        var newTop = topTexture.makeCopy();
//                        generateStumpTexture(topTexture, newTop);
//
//                        sink.addTextureIfNotPresent(manager, newId.toString(), () -> newTop);
//
//                    } catch (Exception e) {
//                        GuitaWoodworks.LOGGER.error("Failed to generate texture for {} : {}", block, e);
//                    }
//                });
//            } catch (Exception e) {
//                GuitaWoodworks.LOGGER.error("Failed to open stump_top texture: ", e);
//            }
//            try {
//                strippedStump.blocks.forEach((w, block) -> {
//                    Identifier id = Utils.getID(block);
//
//                    try (TextureImage topTexture = TextureImage.open(manager,
//                            RPUtils.findFirstBlockTextureLocation(manager, w.getBlockOfThis("stripped_log"), CompatSpritesHelper.LOOKS_LIKE_TOP_LOG_TEXTURE))) {
//
//                        Identifier newId = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace("block/stripped_oak_stump_top", w, id, "oak"));
//
//                        var newTop = topTexture.makeCopy();
//                        generateStumpTexture(topTexture, newTop);
//
//                        sink.addTextureIfNotPresent(manager, newId.toString(), () -> newTop);
//
//                    } catch (Exception e) {
//                        GuitaWoodworks.LOGGER.error("Failed to generate texture for {} : {}", block, e);
//                    }
//                });
//            } catch (Exception e) {
//                GuitaWoodworks.LOGGER.error("Failed to open stump_top texture: ", e);
//            }
//
//            // gwoodworks/textures block/carved_log_inside_edge.png
//            try (TextureImage insideEdgeMask = TextureImage.open(manager, GuitaWoodworks.id("block/mask/carved_log_inside_edge"));
//                 TextureImage insideMask = TextureImage.open(manager, GuitaWoodworks.id("block/mask/carved_log_inside"))
//            ) {
//                carvedLog.blocks.forEach((woodType, block) -> {
//                    Identifier id = Utils.getID(block);
//                    String texturePath = "block/carved_oak_log_inside";
//
//                    try (TextureImage carvedOakLogInsideTexture = TextureImage.open(manager, GuitaWoodworks.id(texturePath));
//                         TextureImage logSideTexture = TextureImage.open(manager,
//                                 RPUtils.findFirstBlockTextureLocation(manager, woodType.log, CompatSpritesHelper.LOOKS_LIKE_SIDE_LOG_TEXTURE));
//                         TextureImage planksTexture = TextureImage.open(manager,
//                                 RPUtils.findFirstBlockTextureLocation(manager, woodType.planks))
//                    ) {
//
//                        Identifier newId = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace(texturePath, woodType, id, "oak"));
//
//                        TextureImage finishedTexture = generateCarvedLogInsideTexture(carvedOakLogInsideTexture,
//                                logSideTexture, planksTexture, insideEdgeMask, insideMask);
//
//                        sink.addTextureIfNotPresent(manager, newId.toString(), () -> finishedTexture);
//
//                    } catch (Exception e) {
//                        GuitaWoodworks.LOGGER.error("Failed to generate texture for {} : {}", block, e);
//                    }
//                });
//
//                strippedCarvedLog.blocks.forEach((w, block) -> {
//                    Identifier id = Utils.getID(block);
//                    String texturePath = "block/stripped_carved_oak_log_inside";
//
//                    try (TextureImage strippedCarvedLogTexture = TextureImage.open(manager, GuitaWoodworks.id(texturePath));
//                         TextureImage logSideTexture = TextureImage.open(manager,
//                                 RPUtils.findFirstBlockTextureLocation(manager, w.getBlockOfThis("stripped_log"), CompatSpritesHelper.LOOKS_LIKE_SIDE_LOG_TEXTURE));
//                         TextureImage planksTexture = TextureImage.open(manager,
//                                 RPUtils.findFirstBlockTextureLocation(manager, w.planks))
//                    ) {
//
//                        Identifier newId = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace(texturePath, w, id, "oak"));
//
//                        TextureImage finishedTexture = generateCarvedLogInsideTexture(strippedCarvedLogTexture, logSideTexture, planksTexture, insideEdgeMask, insideMask);
//
//                        sink.addTextureIfNotPresent(manager, newId.toString(), () -> finishedTexture);
//
//                    } catch (Exception e) {
//                        GuitaWoodworks.LOGGER.error("Failed to generate texture for {} : {}", block, e);
//                    }
//                });
//            } catch (Exception e) {
//                GuitaWoodworks.LOGGER.error("Failed to open the mask texture: ", e);
//            }
//
//            try {
//                beam.blocks.forEach((w, block) -> {
//                    Identifier id = Utils.getID(block);
//                    String baseTexturePath = "block/oak_beam_top";
//
//                    try (TextureImage topTexture = TextureImage.open(manager,
//                            RPUtils.findFirstBlockTextureLocation(manager, w.log, CompatSpritesHelper.LOOKS_LIKE_TOP_LOG_TEXTURE))) {
//
//                        Identifier newId2x2 = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace(baseTexturePath + "_2x2", w, id, "oak"));
//                        Identifier newId4x4 = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace(baseTexturePath + "_4x4", w, id, "oak"));
//                        Identifier newId6x6 = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace(baseTexturePath + "_6x6", w, id, "oak"));
//                        Identifier newId8x8 = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace(baseTexturePath + "_8x8", w, id, "oak"));
//                        Identifier newId10x10 = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace(baseTexturePath + "_10x10", w, id, "oak"));
//                        Identifier newId12x12 = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace(baseTexturePath + "_12x12", w, id, "oak"));
//                        Identifier newId14x14 = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace(baseTexturePath + "_14x14", w, id, "oak"));
//                        Identifier[] newIds = {
//                                newId2x2,
//                                newId4x4,
//                                newId6x6,
//                                newId8x8,
//                                newId10x10,
//                                newId12x12,
//                                newId14x14
//                        };
//
//                        var newTop2x2 = topTexture.makeCopy();
//                        var newTop4x4 = topTexture.makeCopy();
//                        var newTop6x6 = topTexture.makeCopy();
//                        var newTop8x8 = topTexture.makeCopy();
//                        var newTop10x10 = topTexture.makeCopy();
//                        var newTop12x12 = topTexture.makeCopy();
//                        var newTop14x14 = topTexture.makeCopy();
//                        TextureImage[] newTopTextures = {
//                                newTop2x2,
//                                newTop4x4,
//                                newTop6x6,
//                                newTop8x8,
//                                newTop10x10,
//                                newTop12x12,
//                                newTop14x14
//                        };
//                        for (int i = 0; i < 7; ++i) {
//                            var newTopTexture = newTopTextures[i];
//                            generateBeamTexture(topTexture, newTopTexture, i);
//                            sink.addTextureIfNotPresent(manager, newIds[i].toString(), () -> newTopTexture);
//                        }
//
//                    } catch (Exception e) {
//                        GuitaWoodworks.LOGGER.error("Failed to generate texture for {} : {}", block, e);
//                    }
//                });
//            } catch (Exception e) {
//                GuitaWoodworks.LOGGER.error("Failed to open the beam textures: ", e);
//            }
//
//            try {
//                strippedBeam.blocks.forEach((w, block) -> {
//                    Identifier id = Utils.getID(block);
//                    String baseTexturePath = "block/stripped_oak_beam_top";
//
//                    try (TextureImage topTexture = TextureImage.open(manager,
//                            RPUtils.findFirstBlockTextureLocation(manager, w.getBlockOfThis("stripped_log"), CompatSpritesHelper.LOOKS_LIKE_TOP_LOG_TEXTURE))) {
//
//                        Identifier newId2x2 = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace(baseTexturePath + "_2x2", w, id, "oak"));
//                        Identifier newId4x4 = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace(baseTexturePath + "_4x4", w, id, "oak"));
//                        Identifier newId6x6 = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace(baseTexturePath + "_6x6", w, id, "oak"));
//                        Identifier newId8x8 = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace(baseTexturePath + "_8x8", w, id, "oak"));
//                        Identifier newId10x10 = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace(baseTexturePath + "_10x10", w, id, "oak"));
//                        Identifier newId12x12 = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace(baseTexturePath + "_12x12", w, id, "oak"));
//                        Identifier newId14x14 = EveryCompat.res(BlockTypeResTransformer.replaceTypeNoNamespace(baseTexturePath + "_14x14", w, id, "oak"));
//                        Identifier[] newIds = {
//                                newId2x2,
//                                newId4x4,
//                                newId6x6,
//                                newId8x8,
//                                newId10x10,
//                                newId12x12,
//                                newId14x14
//                        };
//
//                        var newTop2x2 = topTexture.makeCopy();
//                        var newTop4x4 = topTexture.makeCopy();
//                        var newTop6x6 = topTexture.makeCopy();
//                        var newTop8x8 = topTexture.makeCopy();
//                        var newTop10x10 = topTexture.makeCopy();
//                        var newTop12x12 = topTexture.makeCopy();
//                        var newTop14x14 = topTexture.makeCopy();
//                        TextureImage[] newTopTextures = {
//                                newTop2x2,
//                                newTop4x4,
//                                newTop6x6,
//                                newTop8x8,
//                                newTop10x10,
//                                newTop12x12,
//                                newTop14x14
//                        };
//                        for (int i = 0; i < 7; ++i) {
//                            var newTopTexture = newTopTextures[i];
//                            generateBeamTexture(topTexture, newTopTexture, i);
//                            sink.addTextureIfNotPresent(manager, newIds[i].toString(), () -> newTopTexture);
//                        }
//
//                    } catch (Exception e) {
//                        GuitaWoodworks.LOGGER.error("Failed to generate texture for {} : {}", block, e);
//                    }
//                });
//            } catch (Exception e) {
//                GuitaWoodworks.LOGGER.error("Failed to open the beam textures: ", e);
//            }
//        });
    }
}