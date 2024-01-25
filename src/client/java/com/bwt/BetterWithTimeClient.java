package com.bwt;

import com.bwt.entities.BwtEntities;
import com.bwt.entities.WindmillBlockEntityRenderer;
import com.bwt.plugins.BwtModelLoadingPlugin;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class BetterWithTimeClient implements ClientModInitializer {
//	BwtModelLoadingPlugin bwtModelLoadingPlugin = new BwtModelLoadingPlugin();

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
//		ModelLoadingPlugin.register(bwtModelLoadingPlugin);
		BlockEntityRendererRegistry.register(BwtEntities.windmillBlockEntity, WindmillBlockEntityRenderer::new);
	}
}