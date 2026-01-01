package com.hestudio.sakisaki;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SakiSakiMod.MODID)
public class SakiSakiMod {
    public static final String MODID = "sakisaki";

    public SakiSakiMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModSounds.register(modBus);
        SakiSakiNetwork.register();

        MinecraftForge.EVENT_BUS.register(new CrawlStateHandler());
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientEvents::register);
    }
}
