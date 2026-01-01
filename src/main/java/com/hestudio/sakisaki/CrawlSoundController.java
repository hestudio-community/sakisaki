package com.hestudio.sakisaki;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public final class CrawlSoundController {
    private SoundInstance currentSound;

    public void tick(LocalPlayer player) {
        if (currentSound != null && !Minecraft.getInstance().getSoundManager().isActive(currentSound)) {
            currentSound = null;
        }

        if (!ClientCrawlState.isCrawling()) {
            return;
        }

        if (!isMoving(player)) {
            return;
        }

        if (currentSound == null) {
            currentSound = new EntityBoundSoundInstance(
                    ModSounds.CRAWL_LOOP.get(),
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f,
                    RandomSource.create(),
                    player
            );
            Minecraft.getInstance().getSoundManager().play(currentSound);
        }
    }

    public void reset() {
        currentSound = null;
    }

    private boolean isMoving(LocalPlayer player) {
        if (player.input == null) {
            return false;
        }
        float forward = player.input.forwardImpulse;
        float left = player.input.leftImpulse;
        return Math.abs(forward) > 0.01f || Math.abs(left) > 0.01f;
    }
}
