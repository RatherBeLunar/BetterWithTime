package com.bwt;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserScreen;
import com.bwt.blocks.cauldron.CauldronScreen;
import com.bwt.blocks.mill_stone.MillStoneScreen;
import com.bwt.entities.BwtEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BetterWithTimeClient implements ClientModInitializer {
	public static final EntityModelLayer MODEL_WINDMILL_LAYER = new EntityModelLayer(new Identifier("bwt", "windmill"), "main");
	public static final EntityModelLayer MODEL_WATER_WHEEL_LAYER = new EntityModelLayer(new Identifier("bwt", "water_wheel"), "main");

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
//		BlockEntityRendererRegistry.register(BwtEntities.windmillBlockEntity, WindmillBlockEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.windmillEntity, WindmillEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.waterWheelEntity, WaterWheelEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(MODEL_WINDMILL_LAYER, WindmillEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MODEL_WATER_WHEEL_LAYER, WaterWheelEntityModel::getTexturedModelData);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
				BwtBlocks.lightBlockBlock,
				BwtBlocks.hempCropBlock,
				BwtBlocks.stoneDetectorRailBlock,
				BwtBlocks.obsidianDetectorRailBlock
		);
		HandledScreens.register(BetterWithTime.blockDispenserScreenHandler, BlockDispenserScreen::new);
		HandledScreens.register(BetterWithTime.cauldronScreenHandler, CauldronScreen::new);
		HandledScreens.register(BetterWithTime.millStoneScreenHandler, MillStoneScreen::new);
	}
}