package com.hestudio.sakisaki;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

public final class ClientEvents {
    private static final KeyMapping CRAWL_KEY = new KeyMapping(
            "key.sakisaki.crawl",
            GLFW.GLFW_KEY_X,
            "key.categories.sakisaki"
    );

    private static final CrawlSoundController SOUND_CONTROLLER = new CrawlSoundController();

    private ClientEvents() {
    }

    public static void register() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(ClientEvents::onRegisterKeys);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onClientTick);
    }

    private static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(CRAWL_KEY);
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            ClientCrawlState.setCrawling(false);
            SOUND_CONTROLLER.reset();
            return;
        }

        while (CRAWL_KEY.consumeClick()) {
            boolean newState = !ClientCrawlState.isCrawling();
            ClientCrawlState.setCrawling(newState);
            SakiSakiNetwork.sendToServer(new CrawlToggleMessage(newState));
        }

        SOUND_CONTROLLER.tick(player);
    }
}
