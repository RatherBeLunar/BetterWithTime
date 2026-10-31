package com.bwt.blocks.block_dispenser.behavior.dispense;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

public class MinecartDispenserBehavior extends DefaultDispenseItemBehavior {
    private final EntityType<? extends AbstractMinecart> entityType;

    public MinecartDispenserBehavior(EntityType<? extends AbstractMinecart> entityType) {
        this.entityType = entityType;
    }

    @Override
    public ItemStack execute(BlockSource blockSource, ItemStack stack) {
        Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
        ServerLevel serverLevel = blockSource.level();
        Vec3 vec3 = blockSource.center();
        double d = vec3.x() + direction.getStepX() * 1.125;
        double e = Math.floor(vec3.y()) + direction.getStepY();
        double f = vec3.z() + direction.getStepZ() * 1.125;
        BlockPos blockPos = blockSource.pos().relative(direction);
        BlockState blockState = serverLevel.getBlockState(blockPos);
        double g;
        if (getRailShape(blockState).isSlope()) {
            g = 0.6;
        } else {
            g = 0.1;
        }

        Vec3 vec32 = new Vec3(d, e + g, f);
        AbstractMinecart abstractMinecart = AbstractMinecart.createMinecart(
                serverLevel, vec32.x, vec32.y, vec32.z, this.entityType, EntitySpawnReason.DISPENSER, stack, null
        );
        if (abstractMinecart != null) {
            serverLevel.addFreshEntity(abstractMinecart);
        }

        return stack;
    }

    private static RailShape getRailShape(BlockState blockState) {
        return blockState.getBlock() instanceof BaseRailBlock baseRailBlock ? blockState.getValue(baseRailBlock.getShapeProperty()) : RailShape.NORTH_SOUTH;
    }
}