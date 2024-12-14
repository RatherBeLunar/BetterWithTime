package com.bwt.mixin;

import com.bwt.blocks.BwtBlocks;
import com.bwt.tags.BwtBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.treedecorator.AlterGroundTreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AlterGroundTreeDecorator.class)
public abstract class AlterGroundTreeDecoratorMixin {

    @Unique
    private static boolean isSoilBlock(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> blockState.isIn(BwtBlockTags.CAN_CONVERT_TO_PODZOL));
    }

    @Unique
    private static boolean isSoilSlab(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> blockState.isIn(BwtBlockTags.CAN_CONVERT_TO_PODZOL_SLAB));
    }


    @Accessor("provider")
    abstract BlockStateProvider getProvider();


    @Inject(method = "setColumn", at = @At("HEAD"), cancellable = true)
    private void bwt$setColumn(TreeDecorator.Generator generator, BlockPos origin, CallbackInfo ci) {
        for (int i = 2; i >= -3; --i) {
            BlockPos blockPos = origin.up(i);
            if (isSoilBlock(generator.getWorld(), blockPos)) {
                generator.replace(blockPos, getProvider().get(generator.getRandom(), origin));
                break;
            }

            if (isSoilSlab(generator.getWorld(), blockPos)) {
                generator.replace(blockPos, BwtBlocks.podzolSlabBlock.getDefaultState());
                break;
            }

            if (!generator.isAir(blockPos) && i < 0) {
                break;
            }
        }
        ci.cancel();
    }

}
