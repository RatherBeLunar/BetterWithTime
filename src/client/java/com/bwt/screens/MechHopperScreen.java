package com.bwt.screens;

import com.bwt.blocks.mech_hopper.MechHopperScreenHandler;
import com.bwt.utils.Id;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class MechHopperScreen extends AbstractContainerScreen<MechHopperScreenHandler> {
    private static final Identifier TEXTURE = Id.of("textures/gui/container/hopper.png");

    static final int gearIconHeight = 14;

    public MechHopperScreen(MechHopperScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        imageHeight = 193;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        // draw the gear power indicator

        if (this.menu.isMechPowered()) {
            context.blit(
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    x + 80,
                    y + 18,
                    176,
                    0,
                    14,
                    gearIconHeight,
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
