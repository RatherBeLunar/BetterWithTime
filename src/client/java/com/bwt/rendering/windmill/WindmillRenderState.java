package com.bwt.rendering.windmill;

import com.bwt.entities.WindmillEntity;
import com.bwt.rendering.horizontal_mech_power_source.HorizontalMechPowerSourceEntityRenderState;
import net.minecraft.world.item.DyeColor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WindmillRenderState extends HorizontalMechPowerSourceEntityRenderState {
    List<DyeColor> dyeColors = IntStream.range(0, WindmillEntity.NUM_SAILS).mapToObj(i -> DyeColor.WHITE).collect(Collectors.toList());

    public DyeColor getSailColor(int i) {
        return dyeColors.get(i);
    }
}
