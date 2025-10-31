package com.bwt.entities.canvas;

import com.bwt.entities.BwtEntities;
import com.bwt.items.BwtItems;
import com.bwt.tags.BwtPaintingVariantTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CanvasEntity extends PaintingEntity {
    public CanvasEntity(EntityType<? extends CanvasEntity> entityType, World world) {
        super(entityType, world);
    }

    private CanvasEntity(World world, BlockPos pos) {
        super(BwtEntities.canvasEntity, world);
        this.attachedBlockPos = pos;
    }

    public CanvasEntity(World world, BlockPos pos, Direction direction, RegistryEntry<PaintingVariant> variant) {
        this(world, pos);
        this.setVariant(variant);
        this.setFacing(direction);
    }

    public static Optional<CanvasEntity> placeCanvas(World world, BlockPos pos, Direction facing) {
        CanvasEntity canvasEntity = new CanvasEntity(world, pos);
        List<RegistryEntry<PaintingVariant>> paintingVariants = new ArrayList<>();
        world.getRegistryManager().get(RegistryKeys.PAINTING_VARIANT).iterateEntries(BwtPaintingVariantTags.CANVAS_PLACEABLE).forEach(paintingVariants::add);
        if (paintingVariants.isEmpty()) {
            return Optional.empty();
        }
        canvasEntity.setFacing(facing);
        paintingVariants.removeIf(variant -> {
            canvasEntity.setVariant(variant);
            return !canvasEntity.canStayAttached();
        });
        if (paintingVariants.isEmpty()) {
            return Optional.empty();
        }
        int i = paintingVariants.stream().mapToInt(CanvasEntity::getSize).max().orElse(0);
        paintingVariants.removeIf(variant -> getSize(variant) < i);
        return Util.getRandomOrEmpty(paintingVariants, canvasEntity.random).map(variant -> {
            canvasEntity.setVariant(variant);
            canvasEntity.setFacing(facing);
            return canvasEntity;
        });
    }

    private static int getSize(RegistryEntry<PaintingVariant> variant) {
        return variant.value().getArea();
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(BwtItems.canvasItem);
    }
}
