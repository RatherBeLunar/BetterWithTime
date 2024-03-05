package com.bwt.screens;

import com.bwt.blocks.block_dispenser.BlockDispenserScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BlockDispenserScreen extends HandledScreen<BlockDispenserScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("bwt", "textures/gui/container/block_dispenser.png");
    static final int selectionIconWidth = 20;
    static final int selectionIconHeight = 20;

    public BlockDispenserScreen(BlockDispenserScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundHeight = 166 + 18;
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

        // draw the selection rectangle
        int selectedSlot = this.handler.getSelectedSlot();
        if (selectedSlot < 0) {
            return;
        }

        int xOffset = ( selectedSlot % 4 ) * 18;
        int yOffset = ( selectedSlot / 4 ) * 18;

        context.drawTexture(TEXTURE,
                x + 51 + xOffset,
                y + 15 + yOffset,
                176,
                0,
                selectionIconWidth,
                selectionIconHeight
        );
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


