package com.bwt.rendering.mech_hopper;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class MechHopperRenderState extends BlockEntityRenderState {
    public int slotsOccupied;
    public @Nullable Identifier filterTextureLocation;
}
