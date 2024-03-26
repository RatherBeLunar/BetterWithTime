package com.bwt.blocks.block_dispenser.behavior.dispense;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import com.bwt.mixin.MinecartItemAccessorMixin;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class MinecartDispenserBehavior extends ItemDispenserBehavior {

    public MinecartDispenserBehavior() {}

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        double g = 0;
        RailShape railShape;
        Direction direction = pointer.state().get(BlockDispenserBlock.FACING);
        ServerWorld serverWorld = pointer.world();
        Vec3d vec3d = pointer.centerPos();
        double d = vec3d.getX() + (double)direction.getOffsetX() * 1.125;
        double e = Math.floor(vec3d.getY()) + (double)direction.getOffsetY();
        double f = vec3d.getZ() + (double)direction.getOffsetZ() * 1.125;
        BlockPos blockPos = pointer.pos().offset(direction);
        BlockState blockState = serverWorld.getBlockState(blockPos);
        railShape = blockState.getBlock() instanceof AbstractRailBlock ? blockState.get(((AbstractRailBlock) blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
        if (blockState.isIn(BlockTags.RAILS)) {
            g = railShape.isAscending() ? 0.6 : 0.1;
        }
        else if (blockState.isAir() && serverWorld.getBlockState(blockPos.down()).isIn(BlockTags.RAILS)) {
            RailShape railShape22;
            BlockState blockState2 = serverWorld.getBlockState(blockPos.down());
            railShape22 = blockState2.getBlock() instanceof AbstractRailBlock ? blockState2.get(((AbstractRailBlock) blockState2.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            g = direction == Direction.DOWN || !railShape22.isAscending() ? -0.9 : -0.4;
        }
        AbstractMinecartEntity.Type minecartType = ((MinecartItemAccessorMixin) stack.getItem()).getType();
        AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create(serverWorld, d, e + g, f, minecartType, stack, null);
        serverWorld.spawnEntity(abstractMinecartEntity);
        if (g > 0 && direction.getAxis().isHorizontal()) {
            abstractMinecartEntity.setVelocity(Vec3d.of(direction.getVector()).multiply(0.3));
        }

        return stack;
    }
}