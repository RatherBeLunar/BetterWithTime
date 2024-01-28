package com.bwt;

import com.bwt.entities.BwtEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BetterWithTimeClient implements ClientModInitializer {
	public static final EntityModelLayer MODEL_WINDMILL_LAYER = new EntityModelLayer(new Identifier("bwt", "windmill"), "main");

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
//		BlockEntityRendererRegistry.register(BwtEntities.windmillBlockEntity, WindmillBlockEntityRenderer::new);
		EntityRendererRegistry.INSTANCE.register(BwtEntities.windmillEntity, WindmillEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(MODEL_WINDMILL_LAYER, WindmillEntityModel::getTexturedModelData);
	}
}