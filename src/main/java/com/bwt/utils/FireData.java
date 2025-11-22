package com.bwt.utils;

import com.bwt.blocks.StokedFireBlock;
import com.bwt.tags.BwtBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class FireData {
    public interface FireAmountFunction {
        FireAmountFunction DEFAULT = (world, pos, state) -> {
            FireData data = new FireData();
            if (state.isIn(BwtBlockTags.HEATS_COOKING_STATIONS)) {
                data.unstokedCount += 1;
            }
            else if (state.isIn(BwtBlockTags.HEATS_COOKING_STATIONS_WHEN_LIT) && state.getOrEmpty(Properties.LIT).orElse(false)) {
                data.unstokedCount += 1;
            }

            if (state.isIn(BwtBlockTags.STOKES_COOKING_STATIONS)) {
                data.stokedCount += 1;
            }
            else if (state.isIn(BwtBlockTags.STOKES_COOKING_STATIONS_WHEN_LIT) && state.getOrEmpty(Properties.LIT).orElse(false)) {
                data.stokedCount += 1;
            }
            return data;
        };

        FireData getFireData(World world, BlockPos pos, BlockState state);
    }

    public static final HashMap<Class<? extends Block>, FireAmountFunction> FIRE_AMOUNT_FUNCTIONS = new HashMap<>();

    int unstokedCount;
    int stokedCount;

    public FireData(int unstokedCount, int stokedCount) {
        this.unstokedCount = unstokedCount;
        this.stokedCount = stokedCount;
    }

    public FireData(int unstokedCount) {
        this(unstokedCount, 0);
    }

    public FireData() {
        this(0);
    }

    public void add(FireData otherData) {
        this.unstokedCount += otherData.unstokedCount;
        this.stokedCount += otherData.stokedCount;
    }

    public boolean anyFirePresent() {
        return unstokedCount > 0 || stokedCount > 0;
    }

    public int getUnstokedCount() {
        return unstokedCount;
    }

    public int getStokedCount() {
        return stokedCount;
    }

}