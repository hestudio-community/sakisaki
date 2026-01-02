package com.hestudio.sakisaki;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record CrawlSoundMessage(int entityId, boolean active) {
    public static void encode(CrawlSoundMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityId);
        buf.writeBoolean(message.active);
    }

    public static CrawlSoundMessage decode(FriendlyByteBuf buf) {
        return new CrawlSoundMessage(buf.readInt(), buf.readBoolean());
    }

    public static void handle(CrawlSoundMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> RemoteCrawlSoundController.handleSoundState(message));
        context.setPacketHandled(true);
    }
}
