package com.bwt.blocks.block_dispenser.behavior.dispense;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import com.bwt.mixin.accessors.BoatItemAccessorMixin;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class BoatDispenserBehavior extends DefaultDispenseItemBehavior {

    public BoatDispenserBehavior() {}

    @Override
    public ItemStack execute(BlockSource pointer, ItemStack stack) {
        if (!(stack.getItem() instanceof BoatItem boatItem)) {
            return stack;
        }
        boolean chest = ((BoatItemAccessorMixin) boatItem).getHasChest();
        Boat.Type boatType = ((BoatItemAccessorMixin) boatItem).getType();

        double h;
        Direction direction = pointer.state().getValue(BlockDispenserBlock.FACING);
        ServerLevel serverLevel = pointer.level();
        Vec3 vec3d = pointer.center();
        double d = 0.5625 + (double) EntityType.BOAT.getWidth() / 2.0;
        double e = vec3d.x() + (double) direction.getStepX() * d;
        double f = vec3d.y() + (double) ((float) direction.getStepY() * 1.125f);
        double g = vec3d.z() + (double) direction.getStepZ() * d;
        Boat boatEntity = chest ? new ChestBoat(serverLevel, e, f, g) : new Boat(serverLevel, e, f, g);
        EntityType.createDefaultStackConfig(serverLevel, stack, null).accept(boatEntity);
        boatEntity.setVariant(boatType);
        boatEntity.setYRot(direction.toYRot());
        serverLevel.addFreshEntity(boatEntity);
        return stack;
    }
}