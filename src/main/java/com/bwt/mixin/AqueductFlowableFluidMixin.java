package com.bwt.mixin;

import com.bwt.blocks.AqueductBlock;
import com.bwt.blocks.BwtBlocks;
import com.bwt.tags.BwtFluidTags;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

@Mixin(FlowingFluid.class)
public abstract class AqueductFlowableFluidMixin extends Fluid {
    @Unique
    public boolean bwt$isMatchingAndStill(FluidState state) {
        return state.getType().isSame(this) && state.isSource();
    }

    @Inject(method = "getFlow", at = @At(value = "HEAD"), cancellable = true)
    public void bwt$getVelocity(BlockGetter level, BlockPos pos, FluidState state, CallbackInfoReturnable<Vec3> cir) {
        if (!state.is(BwtFluidTags.AQUEDUCT_FLUIDS)) {
            return;
        }
        BlockState belowState = level.getBlockState(pos.below());
        if (!belowState.is(BwtBlocks.aqueductBlock)) {
            return;
        }

        double xVelocity = 0.0;
        double zVelocity = 0.0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            boolean isFlowingFromDirection = belowState.getValue(AqueductBlock.FACING_PROPERTIES.get(direction));
            if (!isFlowingFromDirection) {
                continue;
            }
            double v = state.getOwnHeight();
            xVelocity += direction.getOpposite().getStepX() * v;
            zVelocity += direction.getOpposite().getStepZ() * v;
        }
        Vec3 velocityVector = new Vec3(xVelocity, 0.0, zVelocity);
        cir.setReturnValue(velocityVector.normalize());
    }

    @Inject(method = "getNewLiquid", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/material/FlowingFluid;getDropOff(Lnet/minecraft/world/level/LevelReader;)I"), cancellable = true)
    public void bwt$getUpdatedState(CallbackInfoReturnable<FluidState> cir, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) BlockState state, @Local int i, @Local int k) {
        if (!this.is(BwtFluidTags.AQUEDUCT_FLUIDS)) {
            return;
        }
        if (k <= 0 || !this.isSame(this)) {
            return;
        }
        if (!level.getBlockState(pos.below()).is(BwtBlocks.aqueductBlock)) {
            return;
        }
        int thisLevel = state.getFluidState().getAmount();

        ArrayList<Integer> normalFlowingInNeighborLevels = new ArrayList<>();
        ArrayList<Integer> aqueductSupportedNeighborLevels = new ArrayList<>();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(direction);
            FluidState neighborFluidState = level.getFluidState(neighborPos);
            if (!neighborFluidState.getType().isSame(this)) {
                continue;
            }
            BlockState neighborSupportingBlockState = level.getBlockState(neighborPos.below());
            // If supported by an aqueduct, only accept neighbor flow if that neighbor isn't getting flow from *this* block
            if (neighborSupportingBlockState.is(BwtBlocks.aqueductBlock)) {
                level.neighborShapeChanged(Direction.UP, state, neighborPos.below(), neighborPos, Block.UPDATE_ALL & ~(Block.UPDATE_NEIGHBORS | Block.UPDATE_SUPPRESS_DROPS), 512);
                neighborSupportingBlockState = level.getBlockState(neighborPos.below());
                if (neighborSupportingBlockState.is(BwtBlocks.aqueductBlock) && !neighborSupportingBlockState.getValue(AqueductBlock.FACING_PROPERTIES.get(direction.getOpposite()))) {
                    aqueductSupportedNeighborLevels.add(neighborFluidState.getAmount());
                }
                continue;
            }

            if (
                    (neighborSupportingBlockState.isSolid() || this.bwt$isMatchingAndStill(neighborSupportingBlockState.getFluidState()))
                    && neighborFluidState.getAmount() >= thisLevel
            ) {
                normalFlowingInNeighborLevels.add(neighborFluidState.getAmount());
            }
        }

        int fluidLevel;
        if (normalFlowingInNeighborLevels.isEmpty() && aqueductSupportedNeighborLevels.isEmpty()) {
            fluidLevel = 0;
        }
        else if (!normalFlowingInNeighborLevels.isEmpty()) {
            fluidLevel = Math.max(
                    Collections.max(normalFlowingInNeighborLevels),
                    aqueductSupportedNeighborLevels.stream().mapToInt(l -> l).max().orElse(0)
            );
        }
        else {
            fluidLevel = Collections.min(aqueductSupportedNeighborLevels);
        }
        if (fluidLevel == 0) {
            cir.setReturnValue(Fluids.EMPTY.defaultFluidState());
            return;
        }
        // Don't allow level 8 so we don't get fluid sources everywhere
        fluidLevel = Math.min(fluidLevel, FlowingFluid.LEVEL.getAllValues().mapToInt(Property.Value::value).max().orElse(8) - 1);
        cir.setReturnValue(((FlowingFluid) (Object) this).getFlowing(fluidLevel, false));
    }
}
