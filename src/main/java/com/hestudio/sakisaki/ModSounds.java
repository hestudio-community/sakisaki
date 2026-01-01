package com.hestudio.sakisaki;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SakiSakiMod.MODID);

    public static final RegistryObject<SoundEvent> CRAWL_LOOP = SOUNDS.register(
            "crawl_loop",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(SakiSakiMod.MODID, "crawl_loop"))
    );

    private ModSounds() {
    }

    public static void register(IEventBus modBus) {
        SOUNDS.register(modBus);
    }
}
