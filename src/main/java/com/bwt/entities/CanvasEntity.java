package com.bwt.entities;

import com.bwt.items.BwtItems;
import com.bwt.mixin.accessor.PaintingAccessorMixin;
import com.bwt.tags.BwtPaintingVariantTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CanvasEntity extends Painting {
    public CanvasEntity(EntityType<? extends CanvasEntity> entityType, Level level) {
        super(entityType, level);
    }

    private CanvasEntity(Level level, BlockPos pos) {
        super(BwtEntities.canvasEntity, level);
        this.pos = pos;
    }

    @Override
    public void dropItem(ServerLevel serverLevel, @Nullable Entity breaker) {
        if (serverLevel.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            if (!(breaker instanceof Player playerEntity && playerEntity.hasInfiniteMaterials())) {
                this.spawnAtLocation(serverLevel, BwtItems.canvasItem);
            }
        }
    }

    public static Optional<CanvasEntity> placeCanvas(Level level, BlockPos pos, Direction facing) {
        CanvasEntity canvasEntity = new CanvasEntity(level, pos);
        PaintingAccessorMixin variantSetter = (PaintingAccessorMixin) canvasEntity;
        List<Holder<PaintingVariant>> paintingVariants = new ArrayList<>();
        level.registryAccess().lookupOrThrow(Registries.PAINTING_VARIANT).getTagOrEmpty(BwtPaintingVariantTags.CANVAS_PLACEABLE).forEach(paintingVariants::add);
        if (paintingVariants.isEmpty()) {
            return Optional.empty();
        }
        canvasEntity.setDirection(facing);
        paintingVariants.removeIf(holder -> {
            variantSetter.bwt$setVariant(holder);
            return !canvasEntity.survives();
        });
        if (paintingVariants.isEmpty()) {
            return Optional.empty();
        }
        int i = paintingVariants.stream().mapToInt(CanvasEntity::variantArea).max().orElse(0);
        paintingVariants.removeIf(holder -> variantArea(holder) < i);
        return Util.getRandomSafe(paintingVariants, canvasEntity.random).map(holder -> {
            variantSetter.bwt$setVariant(holder);
            canvasEntity.setDirection(facing);
            return canvasEntity;
        });
    }

    private static int variantArea(Holder<PaintingVariant> variant) {
        return variant.value().area();
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(BwtItems.canvasItem);
    }
}
