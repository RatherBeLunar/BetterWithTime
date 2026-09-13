package com.bwt.screens;

import com.bwt.blocks.block_dispenser.BlockDispenserScreenHandler;
import com.bwt.utils.Id;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BlockDispenserScreen extends AbstractContainerScreen<BlockDispenserScreenHandler> {
    private static final ResourceLocation TEXTURE = Id.of("textures/gui/container/block_dispenser.png");
    static final int selectionIconWidth = 20;
    static final int selectionIconHeight = 20;

    public BlockDispenserScreen(BlockDispenserScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        imageHeight = 166 + 18;
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

        // draw the selection rectangle
        int selectedSlot = this.menu.getSelectedSlot();
        if (selectedSlot < 0) {
            return;
        }

        int xOffset = ( selectedSlot % 4 ) * 18;
        int yOffset = ( selectedSlot / 4 ) * 18;

        context.blit(TEXTURE,
                x + 51 + xOffset,
                y + 15 + yOffset,
                176,
                0,
                selectionIconWidth,
                selectionIconHeight
        );
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


