package com.bwt.mixin;

import com.bwt.utils.VoxelShapedEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(EntityGetter.class)
public interface EntityVoxelShapeCollisionMixin {
    @ModifyReturnValue(method = "getEntityCollisions", at = @At("TAIL"))
    default List<VoxelShape> bwt$getEntityCollisions(List<VoxelShape> original, Entity entity, @Local List<Entity> list) {
        if (list.stream().noneMatch(entity1 -> entity1 instanceof VoxelShapedEntity)) {
            return original;
        }
        VoxelShape entityVoxelShape = entity instanceof VoxelShapedEntity voxelShapedEntity ? voxelShapedEntity.getVoxelShape() : Shapes.create(entity.getBoundingBox().inflate(1e-7));
        return list.stream().map(entity1 -> {
            if (entity1 instanceof VoxelShapedEntity voxelShapedEntity) {
                return voxelShapedEntity.getVoxelShape().move(entity1.position().x(), entity1.position().y(), entity1.position().z());
            }
            return Shapes.create(entity1.getBoundingBox().move(entity1.position()));
        }).filter(shape ->
                Shapes.joinIsNotEmpty(shape, entityVoxelShape, BooleanOp.AND)
        ).collect(Collectors.toList());
    }
}
