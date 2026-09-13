package com.bwt.screens;

import com.bwt.blocks.mech_hopper.MechHopperScreenHandler;
import com.bwt.utils.Id;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MechHopperScreen extends AbstractContainerScreen<MechHopperScreenHandler> {
    private static final ResourceLocation TEXTURE = Id.of("textures/gui/container/hopper.png");

    static final int gearIconHeight = 14;

    public MechHopperScreen(MechHopperScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        imageHeight = 193;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        context.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // draw the gear power indicator

        if (this.menu.isMechPowered()) {
            context.blit(
                TEXTURE,
                x + 80,
                y + 18,
                176,
                0,
                14,
                gearIconHeight
            );
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }
}
