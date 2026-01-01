package com.hestudio.sakisaki;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public final class CrawlSoundController {
    private SoundInstance currentSound;

    public void tick(LocalPlayer player) {
        if (currentSound != null && !Minecraft.getInstance().getSoundManager().isActive(currentSound)) {
            currentSound = null;
        }

        boolean crawling = ClientCrawlState.isCrawling();
        boolean moving = isMoving(player);
        if (!crawling || !moving) {
            stopCurrent();
            return;
        }

        if (currentSound == null) {
            currentSound = new EntityBoundSoundInstance(
                    ModSounds.CRAWL_LOOP.get(),
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f,
                    player,
                    player.getId()
            );
            Minecraft.getInstance().getSoundManager().play(currentSound);
        }
    }

    public void reset() {
        stopCurrent();
    }

    private boolean isMoving(LocalPlayer player) {
        if (player.input == null) {
            return false;
        }
        float forward = player.input.forwardImpulse;
        float left = player.input.leftImpulse;
        return Math.abs(forward) > 0.01f || Math.abs(left) > 0.01f;
    }

    private void stopCurrent() {
        if (currentSound != null) {
            Minecraft.getInstance().getSoundManager().stop(currentSound);
            currentSound = null;
        }
    }
}
