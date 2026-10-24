package com.bwt.rendering;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Unit;

/**
 * Used for Cauldrons, Crucibles, and the Mech Hopper to show fill level visually
 */
public class ContainerFillLayerModel extends Model<Unit> {
    protected final ModelPart root;
    protected final ModelPart flat;

    public ContainerFillLayerModel(ModelPart root) {
        super(root, RenderTypes::itemEntityTranslucentCull);
        this.root = root;
        this.flat = root.getChild("flat");
    }

    public static MeshDefinition getModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("flat", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0, 0, 0, 16f, 0.1f, 16f), PartPose.ZERO);
        return modelData;
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = getModelData();
        return LayerDefinition.create(modelData, 16, 16);
    }

    @Override
    public void setupAnim(Unit object) {
        super.setupAnim(object);
    }
}
