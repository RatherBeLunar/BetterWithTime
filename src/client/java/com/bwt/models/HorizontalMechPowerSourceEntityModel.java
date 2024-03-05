package com.bwt.models;

import com.bwt.entities.HorizontalMechPowerSourceEntity;
import net.minecraft.client.render.entity.model.EntityModel;

public abstract class HorizontalMechPowerSourceEntityModel<T extends HorizontalMechPowerSourceEntity> extends EntityModel<T> {
    public HorizontalMechPowerSourceEntityModel() {}

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}
}
