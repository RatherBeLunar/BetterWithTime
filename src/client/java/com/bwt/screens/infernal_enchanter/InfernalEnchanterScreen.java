package com.bwt.screens.infernal_enchanter;

import com.bwt.blocks.infernal_enchanter.InfernalEnchanterScreenHandler;
import com.bwt.utils.Id;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class InfernalEnchanterScreen extends HandledScreen<InfernalEnchanterScreenHandler> {
    private static final Identifier TEXTURE = Id.of("bwt", "textures/gui/container/infernal_enchanter.png");

    private final int BUTTON_WIDTH = 108, BUTTON_HEIGHT = 19;
    private List<Drawable> components = new ArrayList<>();


    public InfernalEnchanterScreen(InfernalEnchanterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundHeight = 211;
        playerInventoryTitleY = backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        for (int i = 0; i < 5; i++) {
            components.add(new Button(i, x + 60, y + 17 + (i * 19), BUTTON_WIDTH, BUTTON_HEIGHT, this));
        }
        components.add(new VerticalBar(x + 50, y + 34, 5, 61, 176, 32, 176 + 5, 32, this) {
            @Override
            int getProgress(DrawContext context) {
                return this.screen.handler.getPropertyDelegate().get(1);
            }

            @Override
            int maxProgress() {
                return 60;
            }
        });
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int input) {

        for (Drawable drawable : components) {
            if (drawable instanceof Button) {
                Button button = (Button) drawable;
                if (button.mouseClick(mouseX, mouseY, input)) {
                    if (this.client != null && this.handler.onButtonClick(this.client.player, button.id)) {
                        this.client.interactionManager.clickButton(this.handler.syncId, button.id);
                    }
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
//        drawSizeBar(context);

        for (Drawable component : components) {
            component.render(context, mouseX, mouseY, delta);
        }
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }


    private static abstract class VerticalBar implements Drawable {
        private final int x, y, w, h;
        private final int u1;
        private final int v1;
        private final int u2;
        private final int v2;
        protected final InfernalEnchanterScreen screen;

        private VerticalBar(int x, int y, int w, int h, int u1, int v1, int u2, int v2, InfernalEnchanterScreen screen) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.u1 = u1;
            this.v1 = v1;
            this.u2 = u2;
            this.v2 = v2;
            this.screen = screen;
        }

        abstract int getProgress(DrawContext context);
        abstract int maxProgress();

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            int progress = this.getProgress(context);
            int clampedProgress = Math.min(progress, this.maxProgress());
            context.drawTexture(TEXTURE, x, y, u1, v1, w, h);
            context.drawTexture(TEXTURE, x, y, u2, v2, w, clampedProgress);
            boolean selected = (mouseX > x && mouseY > y && mouseX < x + w && mouseY < y + h);

            if (selected) {
                context.drawTooltip(this.screen.textRenderer, Text.of(String.format("%d", progress)), mouseX, mouseY);
            }
        }
    }

    private static class Button implements Drawable {
        private final int id, x, y, w, h;
        private final InfernalEnchanterScreen screen;

        private Button(int id, int x, int y, int w, int h, InfernalEnchanterScreen screen) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.screen = screen;
        }

        private record State(MutableText text, boolean enabled) {
        }

        public boolean mouseClick(double mouseX, double mouseY, int input) {
            var state = this.getButtonState();
            if (!state.enabled) {
                return false;
            }
            return mouseX > x && mouseY > y && mouseX < x + w && mouseY < y + h;
        }

        private State getButtonState() {
            int enchanterTier = this.screen.handler.getPropertyDelegate().get(0);

            if (!this.screen.handler.canEnchantToolWithEnchantment(this.id)) {
                return new State(Text.translatable("tooltip.bwt.infernal_enchanter.invalid_enchantment"), false);
            }

            if (this.id + 1 > enchanterTier) {
                return new State(Text.translatable("tooltip.bwt.infernal_enchanter.enchanter_tier_not_high_enough"), false);
            }

            int levelRequired = this.screen.handler.getButtonCost(this.id);

            var player = this.screen.client.player;
            int playerLevel = player.experienceLevel;

            if (playerLevel < levelRequired && !player.isCreative()) {
                return new State(Text.translatable("tooltip.bwt.infernal_enchanter.not_enough_levels", levelRequired), false);
            }




            return new State(null, true);
        }


        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            int levelRequired = this.screen.handler.getButtonCost(this.id);
            var enchantment = this.screen.handler.getEnchantment();
            var state = this.getButtonState();
            boolean selected = (mouseX > x && mouseY > y && mouseX < x + w && mouseY < y + h);
            int textColor = selected ? 16777088 : 6839882;

            if (state.enabled) {

                var levelString = String.format("%d", levelRequired);
                int levelColor = 8453920;


                int p = 86 - this.screen.textRenderer.getWidth(levelString);

                if (selected) {
                    RenderSystem.enableBlend();
                    context.drawTexture(TEXTURE, x, y, 108, 211, w, h);
                    RenderSystem.disableBlend();
                    context.drawTooltip(this.screen.textRenderer, InfernalEnchantingPhrases.tooltipForEnchantment(enchantment, this.screen.handler.getResultEnchantmentLevel(this.id)), mouseX, mouseY);
                }
                StringVisitable stringVisitable = InfernalEnchantingPhrases.getInstance().generatePhrase(this.screen.textRenderer, p, enchantment, this.screen.handler.getResultEnchantmentLevel(id));
                context.drawTextWrapped(this.screen.textRenderer, stringVisitable, x + 2, y + 2, p, textColor);
                context.drawTextWithShadow(this.screen.textRenderer, levelString, x + w - 2 - this.screen.textRenderer.getWidth(levelString), y + 9, levelColor);

            } else {
                RenderSystem.enableBlend();
                context.drawTexture(TEXTURE, x, y, 0, 230, w, h);
                if(selected && state.text != null) {
                    context.drawTooltip(this.screen.textRenderer, state.text, mouseX, mouseY);
                }
                RenderSystem.disableBlend();
            }
        }
    }
}
