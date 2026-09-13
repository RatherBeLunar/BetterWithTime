package com.bwt.items;

import com.bwt.blocks.axles.AxlePowerSourceBlock;
import com.bwt.blocks.BwtBlocks;
import com.bwt.entities.HorizontalMechPowerSourceEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class HorizontalMechPowerSourceItem extends Item {
    protected final HorizontalMechPowerSourceEntity.Factory entityFactory;

    public HorizontalMechPowerSourceItem(HorizontalMechPowerSourceEntity.Factory entityFactory, Item.Properties settings) {
        super(settings);
        this.entityFactory = entityFactory;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockPos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState blockState = level.getBlockState(blockPos);
        if (!blockState.is(BwtBlocks.axleBlock)) {
            return InteractionResult.FAIL;
        }

        Direction.Axis axleAxis = blockState.getValue(AxlePowerSourceBlock.AXIS);
        if (axleAxis.isVertical()) {
            return InteractionResult.FAIL;
        }

        Vec3 middleOfAxle = blockPos.getCenter();
        Vec3 playerPos = context.getPlayer().position();
        Vec3 difference = playerPos.subtract(middleOfAxle);
        Direction placementDirection = Direction.fromAxisAndDirection(
                axleAxis,
                axleAxis.choose(difference.x(), difference.y(), difference.z()) > 0
                        ? Direction.AxisDirection.POSITIVE
                        : Direction.AxisDirection.NEGATIVE
        );

        HorizontalMechPowerSourceEntity mechPowerSourceEntity = entityFactory.create(level, middleOfAxle, placementDirection);

        if (!mechPowerSourceEntity.tryToSpawn(context.getPlayer())) {
            return InteractionResult.FAIL;
        }
        if (context.getPlayer() instanceof ServerPlayer serverPlayerEntity) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayerEntity, mechPowerSourceEntity);
            level.gameEvent(serverPlayerEntity, GameEvent.ENTITY_PLACE, blockPos);
        }
        context.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }
}
