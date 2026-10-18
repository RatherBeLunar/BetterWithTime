package com.bwt.items;

import com.bwt.entities.BwtEntities;
import com.bwt.entities.CanvasEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CanvasItem extends HangingEntityItem {
    private static final Component RANDOM_TEXT = Component.translatable("canvas.random").withStyle(ChatFormatting.GRAY);

    public CanvasItem(Properties settings) {
        super(BwtEntities.canvasEntity, settings);
    }

    @Override
    protected boolean mayPlace(Player player, Direction side, ItemStack stack, BlockPos pos) {
        return !player.level().isOutsideBuildHeight(pos) && super.mayPlace(player, side, stack, pos);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos targetPos = blockPos.relative(direction);
        Player playerEntity = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();
        if (playerEntity != null && !this.mayPlace(playerEntity, direction, itemStack, targetPos)) {
            return InteractionResult.FAIL;
        }
        Level level = context.getLevel();
        Optional<CanvasEntity> optional = CanvasEntity.placeCanvas(level, targetPos, direction);
        if (optional.isEmpty()) {
            return InteractionResult.CONSUME;
        }
        CanvasEntity canvasEntity = optional.get();

        CustomData nbtComponent = itemStack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
        if (!nbtComponent.isEmpty()) {
            EntityType.updateCustomEntityTag(level, playerEntity, canvasEntity, nbtComponent);
        }

        if (canvasEntity.survives()) {
            if (!level.isClientSide) {
                canvasEntity.playPlacementSound();
                level.gameEvent(playerEntity, GameEvent.ENTITY_PLACE, canvasEntity.position());
                level.addFreshEntity(canvasEntity);
            }

            itemStack.shrink(1);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void appendHoverText(
            ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag
    ) {
        if (tooltipDisplay.shows(DataComponents.PAINTING_VARIANT)) {
            Holder<PaintingVariant> holder = itemStack.get(DataComponents.PAINTING_VARIANT);
            if (holder != null) {
                holder.value().title().ifPresent(consumer);
                holder.value().author().ifPresent(consumer);
                consumer.accept(Component.translatable("canvas.dimensions", holder.value().width(), holder.value().height()));
            } else if (tooltipFlag.isCreative()) {
                consumer.accept(RANDOM_TEXT);
            }
        }
    }
}
