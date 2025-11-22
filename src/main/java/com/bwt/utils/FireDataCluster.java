package com.bwt.utils;

import com.bwt.tags.BwtBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FireDataCluster {
    public static final int primaryFireFactor = 5;
    public static final int secondaryFireFactor = 1; // This was changed to 3 later

    FireData fireData;
    boolean centerBlockIsStoked;

    public FireDataCluster() {
        this.fireData = new FireData();
        this.centerBlockIsStoked = false;
    }

    public static FireDataCluster fromWorld(World world, BlockPos pos, int radius) {
        FireDataCluster fireDataCluster = new FireDataCluster();
        BlockPos below = pos.down();
        BlockState centerState = world.getBlockState(below);
        FireData centerData = FireData.FIRE_AMOUNT_FUNCTIONS.getOrDefault(centerState.getBlock().getClass(), FireData.FireAmountFunction.DEFAULT)
                .getFireData(world, below, centerState);
        // The center block determines whether a fire is active or not
        if (!centerData.anyFirePresent()) {
            return fireDataCluster;
        }
        // Stoked fire in the center dominates the resulting type
        if (centerData.stokedCount > 0) {
            fireDataCluster.centerBlockIsStoked = true;
        }
        fireDataCluster.fireData.add(centerData);

        RadiusAroundBlockStream
                .neighboringBlocksInHorizontalRadius(below, radius)
                .map(neighborPos -> BlockPosAndState.of(world, neighborPos))
                .map(neighbor -> FireData.FIRE_AMOUNT_FUNCTIONS
                        .getOrDefault(neighbor.state().getBlock().getClass(), FireData.FireAmountFunction.DEFAULT)
                        .getFireData(world, neighbor.pos(), neighbor.state())
                )
                .forEach(fireDataCluster.fireData::add);
        return fireDataCluster;
    }

    public static FireDataCluster fromWorld(World world, BlockPos pos) {
        return fromWorld(world, pos, 1);
    }

    public boolean isStoked() {
        return centerBlockIsStoked;
    }

    public int getUnstokedCount() {
        return isStoked() ? 0 : fireData.getUnstokedCount();
    }

    public int getStokedCount() {
        return isStoked() ? fireData.getStokedCount() : 0;
    }

    public boolean anyFirePresent() {
        return getUnstokedCount() > 0 || getStokedCount() > 0;
    }

    public int getUnstokedFactor() {
        int unstokedCount = getUnstokedCount();
        return unstokedCount > 0 ? primaryFireFactor + (unstokedCount - 1) * secondaryFireFactor : 0;
    }

    public int getStokedFactor() {
        int stokedCount = getStokedCount();
        return stokedCount > 0 ? primaryFireFactor + (stokedCount - 1) * secondaryFireFactor : 0;
    }

    public int getDominantFireTypeFactor() {
        return isStoked() ? getStokedFactor() : getUnstokedFactor();
    }
}
