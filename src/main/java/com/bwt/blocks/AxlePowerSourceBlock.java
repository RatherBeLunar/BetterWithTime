package com.bwt.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AxlePowerSourceBlock extends AxleBlock {
    public AxlePowerSourceBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(MECH_POWER, 3));
    }

//    @Override
//    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
//        BetterWithTime.LOGGER.info("Adding block entity");
//        return new WindmillBlockEntity(pos, state);
//    }

//    @Override
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
//        if (type.equals(BwtEntities.windmillBlockEntity)) {
//            return (world1, pos, state1, be) -> WindmillBlockEntity.tick(world1, pos, state1, ((WindmillBlockEntity) be));
//        }
//        return null;
//    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {

    }
}
