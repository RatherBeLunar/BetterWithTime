package com.bwt;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.unfired_pottery.UnfiredDecoratedPotBlockEntity;
import com.bwt.entities.BwtEntities;
import com.bwt.items.BwtItems;
import com.bwt.utils.KilnBlockCookProgressSetter;
import com.bwt.utils.kiln_block_cook_overlay.KilnBlockCookingProgressPayload;
import com.bwt.models.*;
import com.bwt.screens.*;
import com.bwt.utils.Id;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.PaintingRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.biome.Biome;

@Environment(EnvType.CLIENT)
public class BetterWithTimeClient implements ClientModInitializer {
	public static final ModelLayerLocation MODEL_WINDMILL_LAYER = new ModelLayerLocation(Id.of("windmill"), "main");
	public static final ModelLayerLocation MODEL_WATER_WHEEL_LAYER = new ModelLayerLocation(Id.of("water_wheel"), "main");
	public static final ModelLayerLocation MECH_HOPPER_FILL_LAYER = new ModelLayerLocation(Id.of("mech_hopper_fill"), "main");
	public static final ModelLayerLocation CAULDRON_FILL_LAYER = new ModelLayerLocation(Id.of("cauldron_fill"), "main");
	public static final ModelLayerLocation CRUCIBLE_FILL_LAYER = new ModelLayerLocation(Id.of("crucible_fill"), "main");

    private final UnfiredDecoratedPotBlockEntity renderUnfiredDecoratedPot = new UnfiredDecoratedPotBlockEntity(BlockPos.ZERO, BwtBlocks.unfiredDecoratedPotBlockWithSherds.defaultBlockState());

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
        BlockEntityRenderers.register(BwtBlockEntities.mechHopperBlockEntity, MechHopperBlockEntityRenderer::new);
        BlockEntityRenderers.register(BwtBlockEntities.unfiredDecoratedPotBlockEntity, UnfiredDecoratedPotBlockEntityRenderer::new);
        BlockEntityRenderers.register(BwtBlockEntities.cauldronBlockEntity, ctx -> new CookingPotEntityRenderer(ctx, Id.of("textures/block/cauldron_stew.png")));
        BlockEntityRenderers.register(BwtBlockEntities.crucibleBlockEntity, ctx -> new CookingPotEntityRenderer(ctx, Id.of("textures/block/crucible_fill.png")));
		EntityRendererRegistry.register(BwtEntities.windmillEntity, WindmillEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.waterWheelEntity, WaterWheelEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.movingRopeEntity, MovingRopeEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.broadheadArrowEntity, BroadheadArrowEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.rottedArrowEntity, RottedArrowEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.dynamiteEntity, DynamiteEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.miningChargeEntity, MiningChargeEntityRenderer::new);
		EntityRendererRegistry.register(BwtEntities.soulUrnProjectileEntity, ThrownItemRenderer::new);
		EntityRendererRegistry.register(BwtEntities.canvasEntity, PaintingRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(MODEL_WINDMILL_LAYER, WindmillEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MODEL_WATER_WHEEL_LAYER, WaterWheelEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MECH_HOPPER_FILL_LAYER, MechHopperFillModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(CAULDRON_FILL_LAYER, CookingPotFillModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(CRUCIBLE_FILL_LAYER, CookingPotFillModel::getTexturedModelData);
        BuiltinItemRendererRegistry.INSTANCE.register(
                BwtBlocks.unfiredDecoratedPotBlockWithSherds.asItem(),
                (stack, mode, matrices, vertexConsumers, light, overlay) -> {
                    renderUnfiredDecoratedPot.readFrom(stack);
                    Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(renderUnfiredDecoratedPot, matrices, vertexConsumers, light, overlay);
                });
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
				BwtBlocks.lightBlockBlock,
				BwtBlocks.lensBeamGlassBlock,
				BwtBlocks.hempCropBlock,
				BwtBlocks.stoneDetectorRailBlock,
				BwtBlocks.obsidianDetectorRailBlock,
				BwtBlocks.grateBlock,
				BwtBlocks.slatsBlock,
				BwtBlocks.wickerPaneBlock,
				BwtBlocks.platformBlock,
				BwtBlocks.stokedFireBlock,
				BwtBlocks.vineTrapBlock,
				BwtBlocks.bloodWoodBlocks.saplingBlock,
				BwtBlocks.bloodWoodBlocks.pottedSaplingBlock,
				BwtBlocks.bloodWoodBlocks.doorBlock,
				BwtBlocks.bloodWoodBlocks.trapdoorBlock
		);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutoutMipped(),
				BwtBlocks.bloodWoodBlocks.leavesBlock,
				BwtBlocks.grassSlabBlock
		);
		MenuScreens.register(BetterWithTime.blockDispenserScreenHandler, BlockDispenserScreen::new);
		MenuScreens.register(BetterWithTime.cauldronScreenHandler, CauldronScreen::new);
		MenuScreens.register(BetterWithTime.crucibleScreenHandler, CrucibleScreen::new);
		MenuScreens.register(BetterWithTime.millStoneScreenHandler, MillStoneScreen::new);
		MenuScreens.register(BetterWithTime.pulleyScreenHandler, PulleyScreen::new);
		MenuScreens.register(BetterWithTime.mechHopperScreenHandler, MechHopperScreen::new);
		MenuScreens.register(BetterWithTime.soulForgeScreenHandler, SoulForgeScreen::new);

		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null) {
				return GrassColor.getDefaultColor();
			}
            Holder<Biome> biomeEntry = view.getBiomeFabric(pos);
			if (biomeEntry == null) {
				return GrassColor.getDefaultColor();
			}
			return biomeEntry.value().getGrassColor(pos.getX(), pos.getZ());
		}, BwtBlocks.grassPlanterBlock);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> GrassColor.getDefaultColor(), BwtBlocks.grassPlanterBlock);

		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null) {
				return GrassColor.getDefaultColor();
			}
			Holder<Biome> biomeEntry = view.getBiomeFabric(pos);
			if (biomeEntry == null) {
				return GrassColor.getDefaultColor();
			}
			return biomeEntry.value().getGrassColor(pos.getX(), pos.getZ());
		}, BwtBlocks.grassSlabBlock);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> GrassColor.getDefaultColor(), BwtBlocks.grassSlabBlock);

		ItemProperties.register(BwtItems.compositeBowItem, Id.mc("pull"), (itemStack, clientWorld, livingEntity, seed) -> {
			if (livingEntity == null) {
				return 0.0F;
			}
			return livingEntity.getMainHandItem() != itemStack ? 0.0F : (itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / 20.0F;
		});

		ItemProperties.register(BwtItems.compositeBowItem, Id.mc("pulling"), (itemStack, clientWorld, livingEntity, seed) -> {
			if (livingEntity == null) {
				return 0.0F;
			}
			return livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F;
		});

		ClientPlayNetworking.registerGlobalReceiver(KilnBlockCookingProgressPayload.ID, (payload, context) ->
			context.client().execute(() -> {
				if (context.client().levelRenderer instanceof KilnBlockCookProgressSetter cookProgressSetter) {
					cookProgressSetter.betterWithTime$setKilnBlockCookingInfo(payload.blockPos(), payload.progress());
				}
			})
		);
	}
}