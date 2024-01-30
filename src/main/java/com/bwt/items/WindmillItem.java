package com.bwt.items;

import com.bwt.blocks.AxleBlock;
import com.bwt.blocks.AxlePowerSourceBlock;
import com.bwt.blocks.BwtBlocks;
import com.bwt.entities.WindmillEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class WindmillItem extends Item {

    public WindmillItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(blockPos);
        if (!blockState.isOf(BwtBlocks.axleBlock)) {
            return ActionResult.FAIL;
        }

        Direction.Axis axleAxis = blockState.get(AxlePowerSourceBlock.AXIS);
        if (axleAxis.isVertical()) {
            return ActionResult.FAIL;
        }

        if (world instanceof ServerWorld && context.getPlayer() != null) {
            Vec3d middleOfAxle = new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
            Vec3d playerPos = context.getPlayer().getPos();
            Vec3d difference = playerPos.subtract(middleOfAxle);
            Direction placementDirection = Direction.from(
                    axleAxis,
                    axleAxis.choose(difference.getX(), difference.getY(), difference.getZ()) > 0
                            ? Direction.AxisDirection.POSITIVE
                            : Direction.AxisDirection.NEGATIVE
            );

            WindmillEntity windmillEntity = new WindmillEntity(world, middleOfAxle, placementDirection);

            if (!windmillEntity.tryToSpawn()) {
                return ActionResult.FAIL;
            }
            world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
        }
        context.getStack().decrement(1);
        return ActionResult.SUCCESS;
    }
}
