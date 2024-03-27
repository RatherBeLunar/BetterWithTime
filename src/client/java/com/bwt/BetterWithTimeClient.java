package com.bwt;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.bwt.entities.BwtEntities;
import com.bwt.models.*;
import com.bwt.screens.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT)
public class BetterWithTimeClient implements ClientModInitializer {
	public static final EntityModelLayer MODEL_WINDMILL_LAYER = new EntityModelLayer(new Identifier("bwt", "windmill"), "main");
	public static final EntityModelLayer MODEL_WATER_WHEEL_LAYER = new EntityModelLayer(new Identifier("bwt", "water_wheel"), "main");
	public static final EntityModelLayer MECH_HOPPER_FILL_LAYER = new EntityModelLayer(new Identifier("bwt", "mech_hopper_fill"), "main");

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BlockEntityRendererFactories.register(BwtBlockEntities.mechHopperBlockEntity, MechHopperBlockEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.windmillEntity, WindmillEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.waterWheelEntity, WaterWheelEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.movingAnchorEntity, MovingAnchorEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(MODEL_WINDMILL_LAYER, WindmillEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MODEL_WATER_WHEEL_LAYER, WaterWheelEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MECH_HOPPER_FILL_LAYER, MechHopperFillModel::getTexturedModelData);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
				BwtBlocks.lightBlockBlock,
				BwtBlocks.hempCropBlock,
				BwtBlocks.stoneDetectorRailBlock,
				BwtBlocks.obsidianDetectorRailBlock,
				BwtBlocks.grateBlock,
				BwtBlocks.slatsBlock,
				BwtBlocks.wickerBlock,
				BwtBlocks.platformBlock,
				BwtBlocks.stokedFireBlock
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
	}
}