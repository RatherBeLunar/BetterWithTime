package com.bwt.rendering.dynamite;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class DynamiteRenderState extends EntityRenderState {
    public final ItemStackRenderState item = new ItemStackRenderState();
    public int fuse;
}
