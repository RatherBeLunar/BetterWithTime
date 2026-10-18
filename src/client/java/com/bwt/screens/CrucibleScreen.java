package com.bwt.screens;

import com.bwt.blocks.crucible.CrucibleScreenHandler;
import com.bwt.utils.Id;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CrucibleScreen extends AbstractContainerScreen<CrucibleScreenHandler> {
    private static final ResourceLocation TEXTURE = Id.of("textures/gui/container/crucible.png");

    static final int fireIconHeight = 14;

    public CrucibleScreen(CrucibleScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        imageHeight = 193;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        context.blit(RenderType::guiTextured, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        // draw the cooking indicator

        float cookProgress = this.menu.getCookProgress();
        if ( cookProgress > 0 )
        {
            int scaledIconHeight = Math.round(fireIconHeight * cookProgress);
            context.blit(
                    RenderType::guiTextured,
                    TEXTURE,
                    x + 80,
                    y + 18 + fireIconHeight - scaledIconHeight,
                    176,
                    fireIconHeight - scaledIconHeight,
                    14,
                    scaledIconHeight + 2,
                    256,
                    256
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
