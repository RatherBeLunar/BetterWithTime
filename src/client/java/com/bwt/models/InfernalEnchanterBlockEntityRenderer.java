package com.bwt.models;

import com.bwt.blocks.infernal_enchanter.InfernalEnchanterBlock;
import com.bwt.blocks.infernal_enchanter.InfernalEnchanterBlockEntity;
import com.bwt.items.components.InfernalEnchanterDecorationComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.CandleBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InfernalEnchanterBlockEntityRenderer implements BlockEntityRenderer<InfernalEnchanterBlockEntity> {

    private final BlockRenderManager manager;

    public InfernalEnchanterBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.manager = ctx.getRenderManager();

    }



    private void renderCandle(World world, BlockPos pos, InfernalEnchanterDecorationComponent.Decoration decoration, BlockState state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        matrixStack.push();
        matrixStack.translate(decoration.getX(), decoration.getY(), decoration.getZ());
        this.manager.renderBlock(state, pos, world, matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getSolid()), false, world.random);
        matrixStack.pop();
    }

    private static BlockState getCandleBlockState(Item item, boolean lit) {
        if(item instanceof BlockItem blockItem) {
            var block = blockItem.getBlock();

            var state = block.getDefaultState();
            if(state.contains(CandleBlock.LIT)) {
                return state.with(CandleBlock.LIT, lit);
            }
            return state;
        }
        return null;
    }


    @Override
    public void render(InfernalEnchanterBlockEntity entity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {

        var candleComponent = entity.getDecorationComponent();
        if(candleComponent == null) {
            return;
        }

        var blockState = entity.getCachedState();
        boolean lit = blockState.get(InfernalEnchanterBlock.LIT);
        for(var decoration: InfernalEnchanterDecorationComponent.Decoration.values()) {
            Item item = candleComponent.get(decoration);
            var candleBlockState = getCandleBlockState(item, lit);
            renderCandle(entity.getWorld(), entity.getPos(), decoration, candleBlockState, matrixStack, vertexConsumerProvider, light, overlay);
        }

    }
}
