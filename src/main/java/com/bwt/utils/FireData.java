package com.bwt.utils;

import com.bwt.tags.BwtBlockTags;
import java.util.HashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FireData {
    public interface FireAmountFunction {
        FireAmountFunction DEFAULT = (level, pos, state) -> {
            FireData data = new FireData();
            if (state.is(BwtBlockTags.HEATS_COOKING_STATIONS)) {
                data.unstokedCount += 1;
            }
            else if (state.is(BwtBlockTags.HEATS_COOKING_STATIONS_WHEN_LIT) && state.getOptionalValue(BlockStateProperties.LIT).orElse(false)) {
                data.unstokedCount += 1;
            }

            if (state.is(BwtBlockTags.STOKES_COOKING_STATIONS)) {
                data.stokedCount += 1;
            }
            else if (state.is(BwtBlockTags.STOKES_COOKING_STATIONS_WHEN_LIT) && state.getOptionalValue(BlockStateProperties.LIT).orElse(false)) {
                data.stokedCount += 1;
            }
            return data;
        };

        FireData getFireData(Level level, BlockPos pos, BlockState state);
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