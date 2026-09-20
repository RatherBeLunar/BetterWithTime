package com.bwt.jei_plugin.categories;

import com.bwt.jei_plugin.DrawableCombined;
import com.bwt.utils.Id;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IDrawableWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BwtRecipeCategoryBase<T> implements IRecipeCategory<T> {
	public static final ResourceLocation WIDGETS = Id.of("textures/gui/container/emiwidgets.png");

	@Nullable
	private final IDrawableStatic background;
	private final IDrawable icon;
	private final Component localizedName;
	private final IGuiHelper guiHelper;
	private final int width;
	private final int height;

	public final IDrawableStatic EMPTY_GEAR;
	public final IDrawableStatic FULL_GEAR;

	public BwtRecipeCategoryBase(int width, int height, Component localizedName, IGuiHelper guiHelper, ItemLike iconItem, @Nullable IDrawableStatic background) {
		this.width = width;
		this.height = height;
		this.localizedName = localizedName;
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(iconItem));
		this.background = background;
		this.guiHelper = guiHelper;

		EMPTY_GEAR = guiHelper.createDrawable(WIDGETS, 0, 0, 14, 14);
		FULL_GEAR = guiHelper.createDrawable(WIDGETS, 14, 0, 14, 14);
	}

	public IDrawable getAnimatedRecipeGearWidget(int animationTime) {
		IDrawableAnimated animatedFill = guiHelper.createAnimatedDrawable(FULL_GEAR, animationTime, IDrawableAnimated.StartDirection.BOTTOM, false);
		return new DrawableCombined(EMPTY_GEAR, animatedFill);
	}

	@Override
	public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if (background == null) {
            return;
        }
        background.draw(guiGraphics);
    }

	@Override
	public @NotNull Component getTitle() {
		return localizedName;
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
	public IDrawable getIcon() {
		return icon;
	}

	protected RegistryAccess getRegistryAccess() {
        ClientLevel level = Minecraft.getInstance().level;
		return level != null ? level.registryAccess() : RegistryAccess.EMPTY;
	}
}

