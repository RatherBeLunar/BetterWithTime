package com.bwt.plugins;

import com.bwt.models.WindmillModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.util.ModelIdentifier;

@Environment(EnvType.CLIENT)
public class BwtModelLoadingPlugin implements ModelLoadingPlugin {
    public static final ModelIdentifier windmillModel = new ModelIdentifier("bwt", "windmill", "");

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        // We want to add our model when the models are loaded
        pluginContext.modifyModelOnLoad().register((original, context) -> {
            // This is called for every model that is loaded, so make sure we only target ours
            if(context.id().equals(windmillModel)) {
                return new WindmillModel();
            } else {
                // If we don't modify the model we just return the original as-is
                return original;
            }
        });
    }
}
