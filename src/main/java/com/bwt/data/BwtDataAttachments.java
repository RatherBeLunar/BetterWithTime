package com.bwt.data;

import com.bwt.utils.Id;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class BwtDataAttachments implements ModInitializer {
    public static AttachmentType<DragonOrbData> dragonOrbData = AttachmentRegistry.create(
            Id.of("dragon_orb_data"),
            builder -> builder
                    .initializer(() -> new DragonOrbData(false))
                    .persistent(DragonOrbData.CODEC)
                    .syncWith(DragonOrbData.PACKET_CODEC, AttachmentSyncPredicate.all())
    );

    @Override
    public void onInitialize() {

    }
}