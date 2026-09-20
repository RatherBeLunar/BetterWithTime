package com.bwt.jei_plugin;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class DrawableCombined implements IDrawableAnimated {
    private final List<IDrawable> drawables;
    private final int width;
    private final int height;

    public DrawableCombined(IDrawable... drawables) {
        this(List.of(drawables));
    }

    public DrawableCombined(List<IDrawable> drawables) {
        IDrawable first = drawables.getFirst();
        this.width = first.getWidth();
        this.height = first.getHeight();
        this.drawables = drawables;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void draw(GuiGraphics guiGraphics) {
        drawables.forEach(drawable -> drawable.draw(guiGraphics));
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        drawables.forEach(drawable -> drawable.draw(guiGraphics, xOffset, yOffset));
    }
}