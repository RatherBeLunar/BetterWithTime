package com.bwt.blocks.block_dispenser.behavior.dispense;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import com.bwt.mixin.accessors.MinecartItemAccessorMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

public class MinecartDispenserBehavior extends DefaultDispenseItemBehavior {

    public MinecartDispenserBehavior() {}

    @Override
    public ItemStack execute(BlockSource pointer, ItemStack stack) {
        double g = 0;
        RailShape railShape;
        Direction direction = pointer.state().getValue(BlockDispenserBlock.FACING);
        ServerLevel serverLevel = pointer.level();
        Vec3 vec3d = pointer.center();
        double d = vec3d.x() + (double)direction.getStepX() * 1.125;
        double e = Math.floor(vec3d.y()) + (double)direction.getStepY();
        double f = vec3d.z() + (double)direction.getStepZ() * 1.125;
        BlockPos blockPos = pointer.pos().relative(direction);
        BlockState blockState = serverLevel.getBlockState(blockPos);
        railShape = blockState.getBlock() instanceof BaseRailBlock ? blockState.getValue(((BaseRailBlock) blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
        if (blockState.is(BlockTags.RAILS)) {
            g = railShape.isAscending() ? 0.6 : 0.1;
        }
        else if (blockState.is(BlockTags.AIR) && serverLevel.getBlockState(blockPos.below()).is(BlockTags.RAILS)) {
            RailShape railShape22;
            BlockState blockState2 = serverLevel.getBlockState(blockPos.below());
            railShape22 = blockState2.getBlock() instanceof BaseRailBlock ? blockState2.getValue(((BaseRailBlock) blockState2.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            g = direction == Direction.DOWN || !railShape22.isAscending() ? -0.9 : -0.4;
        }
        AbstractMinecart.Type minecartType = ((MinecartItemAccessorMixin) stack.getItem()).getType();
        AbstractMinecart abstractMinecartEntity = AbstractMinecart.createMinecart(serverLevel, d, e + g, f, minecartType, stack, null);
        serverLevel.addFreshEntity(abstractMinecartEntity);
        if (g > 0 && direction.getAxis().isHorizontal()) {
            abstractMinecartEntity.setDeltaMovement(Vec3.atLowerCornerOf(direction.getNormal()).scale(0.3));
        }

        return stack;
    }
}