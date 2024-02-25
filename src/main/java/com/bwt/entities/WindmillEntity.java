package com.bwt.entities;

import com.bwt.items.BwtItems;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class WindmillEntity extends HorizontalMechPowerSourceEntity {
    public static final float height = 12.8f;
    public static final float width = 12.8f;
    public static final float length = 0.8f;

    private static final float rotationPerTick = -0.12F;
    private static final float rotationPerTickInStorm = -2.0F;
    private static final float rotationPerTickInNether = -0.07F;


    public WindmillEntity(EntityType<? extends WindmillEntity> entityType, World world) {
        super(entityType, world);
    }

    public WindmillEntity(World world, Vec3d pos, Direction facing) {
        super(BwtEntities.windmillEntity, world, pos, facing);
    }

    @Override
    public boolean tryToSpawn() {
        return super.tryToSpawn(
                Text.of("Not enough room to place Wind Mill (They are friggin HUGE!)"),
                Text.of("Wind Mill placement is obstructed by something, or by you")
        );
    }

    @Override
    public Predicate<BlockPos> getBlockInterferencePredicate() {
        return blockPos -> !getWorld().getBlockState(blockPos).isAir();
    }

    @Override
    float getSpeedToPowerThreshold() {
        return 0.01f;
    }

    @Override
    public float computeRotation() {
        World world = getWorld();
        // Nether
        if (world.getDimension().ultrawarm()) {
            return rotationPerTickInNether;
        }
        // End dimension or modded
        else if (!world.getDimension().natural()) {
            return 0.0f;
        }
        // Overworld, sky blocked
        else if (!world.isSkyVisible(getBlockPos())) {
            return 0.0f;
        }
        // Overworld, raining
        else if (world.isRaining()) {
            return rotationPerTickInStorm;
        }
        // Overworld, not raining
        return rotationPerTick;
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(BwtItems.windmillItem);
    }
}
