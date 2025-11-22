package com.bwt.utils;

import com.bwt.data.BwtDataAttachments;
import com.bwt.data.DragonOrbData;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class DragonOrbHelper {
    public static void spawn(ServerWorld world, Vec3d pos, int amount) {
        for (int i = 0; i < amount; i++) {
            ExperienceOrbEntity orb = new ExperienceOrbEntity(world, pos.x, pos.y, pos.z, 1);
            var data = orb.getAttached(BwtDataAttachments.dragonOrbData);
            if (data == null) return;
            orb.setAttached(BwtDataAttachments.dragonOrbData, new DragonOrbData(true));
            world.spawnEntity(orb);
        }
    }
}