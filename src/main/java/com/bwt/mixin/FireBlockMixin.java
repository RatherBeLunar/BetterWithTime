package com.bwt.mixin;

import com.bwt.blocks.mining_charge.ICaughtFireBlock;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin extends BaseFireBlock {
    public FireBlockMixin(Properties settings, float damage) {
        super(settings, damage);
    }

    @Inject(
            method = "checkBurnOut",
            at = @At(
                    value = "JUMP",
                    opcode = Opcodes.IFEQ,  // Targets the jump that exits the if block
                    shift = At.Shift.BEFORE
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"),
                    to = @At(value = "RETURN")
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void onCaughtFire(Level level, BlockPos pos, int spreadFactor, RandomSource random, int currentAge, CallbackInfo ci, @Local BlockState blockState) {
        Block block = blockState.getBlock();
        if (block instanceof ICaughtFireBlock caught) {
            caught.onCaughtFire(blockState, level, pos, null, null);
        }
    }
}
