package com.bwt.items;

import com.bwt.entities.BwtEntities;
import com.bwt.entities.canvas.CanvasEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DecorationItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.Optional;

public class CanvasItem extends DecorationItem {
    private static final Text RANDOM_TEXT = Text.translatable("canvas.random").formatted(Formatting.GRAY);

    public CanvasItem(Settings settings) {
        super(BwtEntities.canvasEntity, settings);
    }

    @Override
    protected boolean canPlaceOn(PlayerEntity player, Direction side, ItemStack stack, BlockPos pos) {
        return !player.getWorld().isOutOfHeightLimit(pos) && super.canPlaceOn(player, side, stack, pos);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        BlockPos targetPos = blockPos.offset(direction);
        PlayerEntity playerEntity = context.getPlayer();
        ItemStack itemStack = context.getStack();
        if (playerEntity != null && !this.canPlaceOn(playerEntity, direction, itemStack, targetPos)) {
            return ActionResult.FAIL;
        }
        World world = context.getWorld();
        Optional<CanvasEntity> optional = CanvasEntity.placeCanvas(world, targetPos, direction);
        if (optional.isEmpty()) {
            return ActionResult.CONSUME;
        }
        CanvasEntity canvasEntity = optional.get();

        NbtComponent nbtComponent = itemStack.getOrDefault(DataComponentTypes.ENTITY_DATA, NbtComponent.DEFAULT);
        if (!nbtComponent.isEmpty()) {
            EntityType.loadFromEntityNbt(world, playerEntity, canvasEntity, nbtComponent);
        }

        if (canvasEntity.canStayAttached()) {
            if (!world.isClient) {
                canvasEntity.onPlace();
                world.emitGameEvent(playerEntity, GameEvent.ENTITY_PLACE, canvasEntity.getPos());
                world.spawnEntity(canvasEntity);
            }

            itemStack.decrement(1);
            return ActionResult.success(world.isClient);
        } else {
            return ActionResult.CONSUME;
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        RegistryWrapper.WrapperLookup wrapperLookup = context.getRegistryLookup();
        if (wrapperLookup != null) {
            NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.ENTITY_DATA, NbtComponent.DEFAULT);
            if (!nbtComponent.isEmpty()) {
                nbtComponent.get(wrapperLookup.getOps(NbtOps.INSTANCE), CanvasEntity.VARIANT_MAP_CODEC).result().ifPresentOrElse(variant -> {
                    variant.getKey().ifPresent(key -> {
                        tooltip.add(Text.translatable(key.getValue().toTranslationKey("canvas", "title")).formatted(Formatting.YELLOW));
                        tooltip.add(Text.translatable(key.getValue().toTranslationKey("canvas", "author")).formatted(Formatting.GRAY));
                    });
                    tooltip.add(Text.translatable("canvas.dimensions", variant.value().width(), variant.value().height()));
                }, () -> tooltip.add(RANDOM_TEXT));
            } else if (type.isCreative()) {
                tooltip.add(RANDOM_TEXT);
            }
        }
    }
}
