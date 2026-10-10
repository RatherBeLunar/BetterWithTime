package com.bwt.rendering.dynamite;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

public class DynamiteRenderState extends EntityRenderState {
    public int fuse;
    public ItemStack itemStack;
    public BakedModel itemModel;
}
