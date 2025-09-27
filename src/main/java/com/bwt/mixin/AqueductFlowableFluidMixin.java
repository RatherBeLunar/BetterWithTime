package com.bwt.mixin;

import com.bwt.blocks.AqueductBlock;
import com.bwt.blocks.BwtBlocks;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;

@Mixin(FlowableFluid.class)
public abstract class AqueductFlowableFluidMixin extends Fluid {
    @Invoker("getLevelDecreasePerBlock")
    public abstract int getLevelDecreasePerBlock(WorldView world);

    @Unique
    public boolean bwt$isMatchingAndStill(FluidState state) {
        return state.getFluid().matchesType(this) && state.isStill();
    }

    @Inject(method = "getVelocity", at = @At(value = "HEAD"), cancellable = true)
    public void bwt$getVelocity(BlockView world, BlockPos pos, FluidState state, CallbackInfoReturnable<Vec3d> cir) {
        BlockState belowState = world.getBlockState(pos.down());
        if (!belowState.isOf(BwtBlocks.aqueductBlock)) {
            return;
        }

        double xVelocity = 0.0;
        double zVelocity = 0.0;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            boolean isFlowingFromDirection = belowState.get(AqueductBlock.FACING_PROPERTIES.get(direction));
            if (!isFlowingFromDirection) {
                continue;
            }
            double v = state.getHeight();
            xVelocity += direction.getOpposite().getOffsetX() * v;
            zVelocity += direction.getOpposite().getOffsetZ() * v;
        }
        Vec3d velocityVector = new Vec3d(xVelocity, 0.0, zVelocity);
        cir.setReturnValue(velocityVector.normalize());
    }

    @Inject(method = "getUpdatedState", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/fluid/FlowableFluid;getLevelDecreasePerBlock(Lnet/minecraft/world/WorldView;)I"), cancellable = true)
    public void bwt$getUpdatedState(CallbackInfoReturnable<FluidState> cir, @Local(argsOnly = true) World world, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) BlockState state, @Local int i, @Local int k) {
        if (k <= 0 || !this.matchesType(this)) {
            return;
        }
        if (!world.getBlockState(pos.down()).isOf(BwtBlocks.aqueductBlock)) {
            return;
        }
        int thisLevel = state.getFluidState().getLevel();

        ArrayList<Integer> normalFlowingInNeighborLevels = new ArrayList<>();
        ArrayList<Integer> aqueductSupportedNeighborLevels = new ArrayList<>();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos neighborPos = pos.offset(direction);
            FluidState neighborFluidState = world.getFluidState(neighborPos);
            if (!neighborFluidState.getFluid().matchesType(this)) {
                continue;
            }
            BlockState neighborSupportingBlockState = world.getBlockState(neighborPos.down());
            // If supported by an aqueduct, only accept neighbor flow if that neighbor isn't getting flow from *this* block
            if (neighborSupportingBlockState.isOf(BwtBlocks.aqueductBlock)) {
                world.replaceWithStateForNeighborUpdate(Direction.UP, state, neighborPos.down(), neighborPos, Block.NOTIFY_ALL & ~(Block.NOTIFY_NEIGHBORS | Block.SKIP_DROPS), 512);
                neighborSupportingBlockState = world.getBlockState(neighborPos.down());
                if (neighborSupportingBlockState.isOf(BwtBlocks.aqueductBlock) && !neighborSupportingBlockState.get(AqueductBlock.FACING_PROPERTIES.get(direction.getOpposite()))) {
                    aqueductSupportedNeighborLevels.add(neighborFluidState.getLevel());
                }
                continue;
            }

            if (
                    (neighborSupportingBlockState.isSolid() || this.bwt$isMatchingAndStill(neighborSupportingBlockState.getFluidState()))
                    && neighborFluidState.getLevel() >= thisLevel
            ) {
                normalFlowingInNeighborLevels.add(neighborFluidState.getLevel());
            }
        }

        int level;
        if (normalFlowingInNeighborLevels.isEmpty() && aqueductSupportedNeighborLevels.isEmpty()) {
            level = 0;
        }
        else if (!normalFlowingInNeighborLevels.isEmpty()) {
            level = Math.max(
                    Collections.max(normalFlowingInNeighborLevels),
                    aqueductSupportedNeighborLevels.stream().mapToInt(l -> l).max().orElse(0)
            );
        }
        else {
            level = Collections.min(aqueductSupportedNeighborLevels);
        }
        if (level == 0) {
            cir.setReturnValue(Fluids.EMPTY.getDefaultState());
            return;
        }
        // Don't allow level 8 so we don't get fluid sources everywhere
        level = Math.min(level, FlowableFluid.LEVEL.stream().mapToInt(Property.Value::value).max().orElse(8) - 1);
        // Don't allow level 1 or 2 so water doesn't slosh around
        level = Math.max(level, 3);
        cir.setReturnValue(((FlowableFluid) (Object) this).getFlowing(level, false));
    }
}
