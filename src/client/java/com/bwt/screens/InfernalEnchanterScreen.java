package com.bwt.screens;

import com.bwt.blocks.infernal_enchanter.InfernalEnchanterScreenHandler;
import com.bwt.blocks.mill_stone.MillStoneScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class InfernalEnchanterScreen extends HandledScreen<InfernalEnchanterScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("bwt", "textures/gui/container/infernal_enchanter.png");

    private final int BUTTON_WIDTH = 108, BUTTON_HEIGHT = 19;
    private Button[] buttons = new Button[5];

    public InfernalEnchanterScreen(InfernalEnchanterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundHeight = 211;
        playerInventoryTitleY = backgroundHeight - 94;
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int input) {

        for (Button button : buttons) {
            if (button.mouseClick(mouseX, mouseY, input)) {
                if (this.client != null && this.handler.onButtonClick(this.client.player, button.id)) {
                    this.client.interactionManager.clickButton(this.handler.syncId, button.id);
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, input);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        for (Button button : buttons) {
            button.render(context, mouseX, mouseY, delta);
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
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        for (int i = 0; i < 5; i++) {
            buttons[i] = new Button(i, x + 60, y + 17 + (i * 19), BUTTON_WIDTH, BUTTON_HEIGHT, this.handler.getPropertyDelegate());
        }
    }

    private static class Button implements Drawable {
        private final int id, x, y, w, h;
        private final PropertyDelegate propertyDelegate;

        private Button(int id, int x, int y, int w, int h, PropertyDelegate propertyDelegate) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.propertyDelegate = propertyDelegate;
        }

        public boolean mouseClick(double mouseX, double mouseY, int input) {
            if (this.isDisabled()) {
                return false;
            }
            return mouseX > x && mouseY > y && mouseX < x + w && mouseY < y + h;
        }

        private boolean isDisabled() {
            int level = this.propertyDelegate.get(0);
            return this.id + 1 > level;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            if (this.isDisabled()) {
                RenderSystem.enableBlend();
                context.drawTexture(TEXTURE, x, y, 0, 230, w, h);
                RenderSystem.disableBlend();
            } else {
                if (mouseX > x && mouseY > y && mouseX < x + w && mouseY < y + h) {
                    RenderSystem.enableBlend();
                    context.drawTexture(TEXTURE, x, y, 108, 211, w, h);
                    RenderSystem.disableBlend();
                }
            }
        }
    }
}
