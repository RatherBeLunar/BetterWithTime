package com.bwt;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.bwt.entities.BwtEntities;
import com.bwt.items.BwtItems;
import com.bwt.models.*;
import com.bwt.screens.*;
import com.bwt.utils.Id;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GrassColors;

@Environment(EnvType.CLIENT)
public class BetterWithTimeClient implements ClientModInitializer {
	public static final EntityModelLayer MODEL_WINDMILL_LAYER = new EntityModelLayer(Id.of("windmill"), "main");
	public static final EntityModelLayer MODEL_WATER_WHEEL_LAYER = new EntityModelLayer(Id.of("water_wheel"), "main");
	public static final EntityModelLayer MECH_HOPPER_FILL_LAYER = new EntityModelLayer(Id.of("mech_hopper_fill"), "main");
	public static final EntityModelLayer CAULDRON_FILL_LAYER = new EntityModelLayer(Id.of("cauldron_fill"), "main");
	public static final EntityModelLayer CRUCIBLE_FILL_LAYER = new EntityModelLayer(Id.of("crucible_fill"), "main");

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BlockEntityRendererFactories.register(BwtBlockEntities.mechHopperBlockEntity, MechHopperBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(BwtBlockEntities.cauldronBlockEntity, ctx -> new CookingPotEntityRenderer(ctx, Id.of("textures/block/cauldron_stew.png")));
		BlockEntityRendererFactories.register(BwtBlockEntities.crucibleBlockEntity, ctx -> new CookingPotEntityRenderer(ctx, Id.of("textures/block/crucible_fill.png")));
		EntityRendererRegistry.register(BwtEntities.windmillEntity, WindmillEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.waterWheelEntity, WaterWheelEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.movingRopeEntity, MovingRopeEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.broadheadArrowEntity, BroadheadArrowEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.rottedArrowEntity, RottedArrowEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.dynamiteEntity, DynamiteEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.miningChargeEntity, MiningChargeEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(MODEL_WINDMILL_LAYER, WindmillEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MODEL_WATER_WHEEL_LAYER, WaterWheelEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MECH_HOPPER_FILL_LAYER, MechHopperFillModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(CAULDRON_FILL_LAYER, CookingPotFillModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(CRUCIBLE_FILL_LAYER, CookingPotFillModel::getTexturedModelData);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
				BwtBlocks.lightBlockBlock,
				BwtBlocks.hempCropBlock,
				BwtBlocks.stoneDetectorRailBlock,
				BwtBlocks.obsidianDetectorRailBlock,
				BwtBlocks.grateBlock,
				BwtBlocks.slatsBlock,
				BwtBlocks.wickerPaneBlock,
				BwtBlocks.platformBlock,
				BwtBlocks.stokedFireBlock,
				BwtBlocks.vineTrapBlock,
				BwtBlocks.bloodWoodBlocks.saplingBlock,
				BwtBlocks.bloodWoodBlocks.pottedSaplingBlock,
				BwtBlocks.bloodWoodBlocks.doorBlock,
				BwtBlocks.bloodWoodBlocks.trapdoorBlock
		);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(),
				BwtBlocks.bloodWoodBlocks.leavesBlock,
				BwtBlocks.grassSlabBlock
		);
		HandledScreens.register(BetterWithTime.blockDispenserScreenHandler, BlockDispenserScreen::new);
		HandledScreens.register(BetterWithTime.cauldronScreenHandler, CauldronScreen::new);
		HandledScreens.register(BetterWithTime.crucibleScreenHandler, CrucibleScreen::new);
		HandledScreens.register(BetterWithTime.millStoneScreenHandler, MillStoneScreen::new);
		HandledScreens.register(BetterWithTime.pulleyScreenHandler, PulleyScreen::new);
		HandledScreens.register(BetterWithTime.mechHopperScreenHandler, MechHopperScreen::new);
		HandledScreens.register(BetterWithTime.soulForgeScreenHandler, SoulForgeScreen::new);

		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null) {
				return GrassColors.getDefaultColor();
			}
			RegistryEntry<Biome> biomeEntry = view.getBiomeFabric(pos);
			if (biomeEntry == null) {
				return GrassColors.getDefaultColor();
			}
			return biomeEntry.value().getGrassColorAt(pos.getX(), pos.getZ());
		}, BwtBlocks.grassPlanterBlock);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> GrassColors.getDefaultColor(), BwtBlocks.grassPlanterBlock);

		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null) {
				return GrassColors.getDefaultColor();
			}
			RegistryEntry<Biome> biomeEntry = view.getBiomeFabric(pos);
			if (biomeEntry == null) {
				return GrassColors.getDefaultColor();
			}
			return biomeEntry.value().getGrassColorAt(pos.getX(), pos.getZ());
		}, BwtBlocks.grassSlabBlock);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> GrassColors.getDefaultColor(), BwtBlocks.grassSlabBlock);

		ModelPredicateProviderRegistry.register(BwtItems.compositeBowItem, Id.mc("pull"), (itemStack, clientWorld, livingEntity, seed) -> {
			if (livingEntity == null) {
				return 0.0F;
			}
			return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime(livingEntity) - livingEntity.getItemUseTimeLeft()) / 20.0F;
		});

		ModelPredicateProviderRegistry.register(BwtItems.compositeBowItem, Id.mc("pulling"), (itemStack, clientWorld, livingEntity, seed) -> {
			if (livingEntity == null) {
				return 0.0F;
			}
			return livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
		});
	}
}