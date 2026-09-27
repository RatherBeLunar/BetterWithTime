package com.bwt.mixin;

import com.bwt.blocks.BwtBlocks;
import com.bwt.tags.BwtBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AlterGroundDecorator.class)
public abstract class AlterGroundTreeDecoratorMixin {

    @Unique
    private static boolean isSoilBlock(LevelSimulatedReader level, BlockPos pos) {
        return level.isStateAtPosition(pos, blockState -> blockState.is(BwtBlockTags.CAN_CONVERT_TO_PODZOL));
    }

    @Unique
    private static boolean isSoilSlab(LevelSimulatedReader level, BlockPos pos) {
        return level.isStateAtPosition(pos, blockState -> blockState.is(BwtBlockTags.CAN_CONVERT_TO_PODZOL_SLAB));
    }


    @Accessor("provider")
    abstract BlockStateProvider getProvider();


    @Inject(method = "placeBlockAt", at = @At("HEAD"), cancellable = true)
    private void bwt$setColumn(TreeDecorator.Context generator, BlockPos origin, CallbackInfo ci) {
        for (int i = 2; i >= -3; --i) {
            BlockPos blockPos = origin.above(i);
            if (isSoilBlock(generator.level(), blockPos)) {
                generator.setBlock(blockPos, getProvider().getState(generator.random(), origin));
                break;
            }

            if (isSoilSlab(generator.level(), blockPos)) {
                generator.setBlock(blockPos, BwtBlocks.podzolSlabBlock.defaultBlockState());
                break;
            }

            if (!generator.isAir(blockPos) && i < 0) {
                break;
            }
        }
        ci.cancel();
    }

}
