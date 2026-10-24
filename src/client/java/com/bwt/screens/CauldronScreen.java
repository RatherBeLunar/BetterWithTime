package com.bwt.screens;

import com.bwt.blocks.cauldron.CauldronScreenHandler;
import com.bwt.utils.Id;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class CauldronScreen extends AbstractContainerScreen<CauldronScreenHandler> {
    private static final Identifier TEXTURE = Id.of("textures/gui/container/cauldron.png");

    static final int fireIconHeight = 14;

    public CauldronScreen(CauldronScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        imageHeight = 193;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        // draw the cooking indicator

        float cookProgress = this.menu.getCookProgress();
        if ( cookProgress > 0 )
        {
            int scaledIconHeight = Math.round(fireIconHeight * cookProgress);
            context.blit(
                RenderPipelines.GUI_TEXTURED,
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
