package com.bwt.screens;

import com.bwt.blocks.soul_forge.SoulForgeScreenHandler;
import com.bwt.utils.Id;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

@Environment(value= EnvType.CLIENT)
public class SoulForgeScreen
        extends AbstractContainerScreen<SoulForgeScreenHandler>
        implements RecipeUpdateListener {
    private static final ResourceLocation TEXTURE = Id.of("textures/gui/container/soul_forge.png");
    private final RecipeBookComponent recipeBook = new RecipeBookComponent();
    private boolean narrow;

    public SoulForgeScreen(SoulForgeScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        imageHeight = 184;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.narrow = this.width < 379;
        this.recipeBook.init(this.width, this.height, this.minecraft, this.narrow, this.menu);
        this.leftPos = this.recipeBook.updateScreenPosition(this.width, this.imageWidth);
        this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, RecipeBookComponent.RECIPE_BUTTON_SPRITES, button -> {
            this.recipeBook.toggleVisibility();
            this.leftPos = this.recipeBook.updateScreenPosition(this.width, this.imageWidth);
            button.setPosition(this.leftPos + 5, this.height / 2 - 49);
        }));
        this.addWidget(this.recipeBook);
        this.setInitialFocus(this.recipeBook);
        this.titleLabelX = 25;
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.recipeBook.tick();
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.recipeBook.isVisible() && this.narrow) {
            this.renderBackground(context, mouseX, mouseY, delta);
            this.recipeBook.render(context, mouseX, mouseY, delta);
        } else {
            super.render(context, mouseX, mouseY, delta);
            this.recipeBook.render(context, mouseX, mouseY, delta);
            this.recipeBook.renderGhostRecipe(context, this.leftPos, this.topPos, true, delta);
        }
        this.renderTooltip(context, mouseX, mouseY);
        this.recipeBook.renderTooltip(context, this.leftPos, this.topPos, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;
        context.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double pointX, double pointY) {
        return (!this.narrow || !this.recipeBook.isVisible()) && super.isHovering(x, y, width, height, pointX, pointY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.recipeBook);
            return true;
        }
        if (this.narrow && this.recipeBook.isVisible()) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.imageWidth) || mouseY >= (double)(top + this.imageHeight);
        return this.recipeBook.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, button) && bl;
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int button, ClickType actionType) {
        super.slotClicked(slot, slotId, button, actionType);
        this.recipeBook.slotClicked(slot);
    }

    @Override
    public void recipesUpdated() {
        this.recipeBook.recipesUpdated();
    }

    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBook;
    }
}

