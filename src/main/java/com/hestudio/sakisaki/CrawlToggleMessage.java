package com.hestudio.sakisaki;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public record CrawlToggleMessage(boolean crawling) {
    public static void encode(CrawlToggleMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.crawling);
    }

    public static CrawlToggleMessage decode(FriendlyByteBuf buf) {
        return new CrawlToggleMessage(buf.readBoolean());
    }

    public static void handle(CrawlToggleMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                CrawlStateHandler.setCrawling(player, message.crawling);
            }
        });
        context.setPacketHandled(true);
    }
}
