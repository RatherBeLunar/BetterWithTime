package com.bwt.mixin.planter_and_vase_functionality;

import com.bwt.tags.BwtBlockTags;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class FlowerBlockOffsetMixin extends StateHolder<Block, BlockState> {
    @Shadow public abstract Block getBlock();

    protected FlowerBlockOffsetMixin(Block owner, Reference2ObjectArrayMap<Property<?>, Comparable<?>> propertyMap, MapCodec<BlockState> codec) {
        super(owner, propertyMap, codec);
    }

    @Inject(method="getOffset", at = @At("HEAD"), cancellable = true)
    public void getModelOffset(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Vec3> cir) {
        if (this.getBlock() instanceof FlowerBlock && level.getBlockState(pos.below()).is(BwtBlockTags.VASES)) {
            cir.setReturnValue(Vec3.ZERO);
        }
    }
}
