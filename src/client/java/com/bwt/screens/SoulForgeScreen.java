package com.bwt.screens;

import com.bwt.blocks.soul_forge.SoulForgeScreenHandler;
import com.bwt.utils.Id;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

@Environment(value= EnvType.CLIENT)
public class SoulForgeScreen extends AbstractRecipeBookScreen<SoulForgeScreenHandler> {
    private static final ResourceLocation TEXTURE = Id.of("textures/gui/container/soul_forge.png");

    public SoulForgeScreen(SoulForgeScreenHandler handler, Inventory inventory, Component title) {
        super(handler, new SoulForgeCraftingRecipeBookComponent(handler), inventory, title);
        imageHeight = 184;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 5, this.height / 2 - 49);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 25;
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;
        context.blit(RenderType::guiTextured, TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}

