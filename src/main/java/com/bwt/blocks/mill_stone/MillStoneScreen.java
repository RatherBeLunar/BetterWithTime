package com.bwt.blocks.mill_stone;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MillStoneScreen extends HandledScreen<MillStoneScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("bwt", "textures/gui/container/mill_stone.png");

    static final int gearIconHeight = 12;

    public MillStoneScreen(MillStoneScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundHeight = 158;
        playerInventoryTitleY = backgroundHeight - 94;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        // draw the gear indicator

        float grindProgress = this.handler.getGrindProgress();
        if ( grindProgress > 0 )
        {
            int scaledIconHeight = Math.round(gearIconHeight * grindProgress);
            context.drawTexture(
                    TEXTURE,
                    x + 80,
                    y + 18 + gearIconHeight - scaledIconHeight,
                    176,
                    gearIconHeight - scaledIconHeight,
                    14,
                    scaledIconHeight + 2
            );
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}
