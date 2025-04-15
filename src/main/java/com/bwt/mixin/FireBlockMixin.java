package com.bwt.mixin;

import com.bwt.blocks.mining_charge.ICaughtFireBlock;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin extends AbstractFireBlock {
    public FireBlockMixin(Settings settings, float damage) {
        super(settings, damage);
    }

    @Inject(
            method = "trySpreadingFire",
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
    public void onCaughtFire(World world, BlockPos pos, int spreadFactor, Random random, int currentAge, CallbackInfo ci, @Local BlockState blockState) {
        Block block = blockState.getBlock();
        if (block instanceof ICaughtFireBlock caught) {
            caught.onCaughtFire(blockState, world, pos, null, null);
        }
    }
}
