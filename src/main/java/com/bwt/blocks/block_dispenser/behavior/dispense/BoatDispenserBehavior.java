package com.bwt.blocks.block_dispenser.behavior.dispense;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import com.bwt.mixin.accessors.BoatItemAccessorMixin;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BoatDispenserBehavior extends ItemDispenserBehavior {

    public BoatDispenserBehavior() {}

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        if (!(stack.getItem() instanceof BoatItem boatItem)) {
            return stack;
        }
        boolean chest = ((BoatItemAccessorMixin) boatItem).getChest();
        BoatEntity.Type boatType = ((BoatItemAccessorMixin) boatItem).getType();

        double h;
        Direction direction = pointer.state().get(BlockDispenserBlock.FACING);
        ServerWorld serverWorld = pointer.world();
        Vec3d vec3d = pointer.centerPos();
        double d = 0.5625 + (double) EntityType.BOAT.getWidth() / 2.0;
        double e = vec3d.getX() + (double) direction.getOffsetX() * d;
        double f = vec3d.getY() + (double) ((float) direction.getOffsetY() * 1.125f);
        double g = vec3d.getZ() + (double) direction.getOffsetZ() * d;
        BoatEntity boatEntity = chest ? new ChestBoatEntity(serverWorld, e, f, g) : new BoatEntity(serverWorld, e, f, g);
        EntityType.copier(serverWorld, stack, null).accept(boatEntity);
        boatEntity.setVariant(boatType);
        boatEntity.setYaw(direction.asRotation());
        serverWorld.spawnEntity(boatEntity);
        return stack;
    }
}