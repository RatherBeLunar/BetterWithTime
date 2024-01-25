package com.bwt.entities;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@Environment(EnvType.CLIENT)
public class WindmillBlockEntityRenderer implements BlockEntityRenderer<WindmillBlockEntity > {
    // A jukebox itemstack
    private static ItemStack stack = new ItemStack(Items.JUKEBOX, 1);

    public WindmillBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(WindmillBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    }
//
//    @Override
//    public void render(WindmillBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
//        matrices.push();
//        GL11.glTranslatef( (float)x, (float)y, (float)z );
//
//        loadTexture("/btwmodtex/windmill.png");
//        GL11.glScalef( 1.0F, 1.0F, 1.0F );//(-1F, -1F, 1.0F);
//
//        // rock the wind mill for damage
//        float deltaTime = (float)entityWindMill.m_iTimeSinceHit - renderPartialTicks;
//        float deltaDamage = (float)entityWindMill.m_iCurrentDamage - renderPartialTicks;
//        float rotateForDamage = 0.0F;
//
//        if ( deltaDamage < 0.0F)
//        {
//            deltaDamage = 0.0F;
//        }
//
//        if ( deltaDamage > 0.0F )
//        {
//            rotateForDamage = ( (MathHelper.sin( deltaTime ) * deltaTime * deltaDamage) / 40F ) *
//                    (float)entityWindMill.m_iRockDirection;
//        }
//
//        if ( entityWindMill.m_bIAligned )
//        {
//            GL11.glRotatef( entityWindMill.m_fRotation, 1.0F, 0.0F, 0.0F);
//
//            GL11.glRotatef( rotateForDamage, 0.0F, 0.0F, 1.0F);
//
//            GL11.glRotatef( 90F, 0.0F, 1.0F, 0.0F );
//        }
//        else
//        {
//            GL11.glRotatef( entityWindMill.m_fRotation, 0.0F, 0.0F, 1.0F);
//
//            GL11.glRotatef( rotateForDamage, 1.0F, 0.0F, 0.0F);
//        }
//
//        modelWindMill.render( 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, entityWindMill );
//
//        GL11.glPopMatrix();
//    }
}