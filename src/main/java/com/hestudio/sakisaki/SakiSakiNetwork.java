package com.hestudio.sakisaki;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class SakiSakiNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(SakiSakiMod.MODID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private SakiSakiNetwork() {
    }

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, CrawlToggleMessage.class, CrawlToggleMessage::encode, CrawlToggleMessage::decode,
                CrawlToggleMessage::handle);
    }

    public static void sendToServer(CrawlToggleMessage message) {
        CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
    }
}
