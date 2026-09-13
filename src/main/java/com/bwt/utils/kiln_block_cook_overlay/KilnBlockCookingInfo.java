package com.bwt.utils.kiln_block_cook_overlay;

import net.minecraft.core.BlockPos;

public class KilnBlockCookingInfo implements Comparable<KilnBlockCookingInfo> {
    private final BlockPos pos;
    private int stage;

    public KilnBlockCookingInfo(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setStage(int stage) {
        if (stage > 10) {
            stage = 10;
        }

        this.stage = stage;
    }

    public int getStage() {
        return this.stage;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            KilnBlockCookingInfo kilnBlockCookingInfo = (KilnBlockCookingInfo)o;
            return this.pos == kilnBlockCookingInfo.pos;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Long.hashCode(this.pos.asLong());
    }

    public int compareTo(KilnBlockCookingInfo kilnBlockCookingInfo) {
        return this.stage != kilnBlockCookingInfo.stage
                ? Integer.compare(this.stage, kilnBlockCookingInfo.stage)
                : Long.compare(this.pos.asLong(), kilnBlockCookingInfo.pos.asLong());
    }
}
