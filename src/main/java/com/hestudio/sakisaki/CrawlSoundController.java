package com.hestudio.sakisaki;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public final class CrawlSoundController {
    private static final float BASE_VOLUME = 1.0f;

    private LoopingEntitySoundInstance currentSound;

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
            currentSound = createLoopSound(player);
            currentSound.setVolume(BASE_VOLUME);
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

    private static LoopingEntitySoundInstance createLoopSound(LocalPlayer player) {
        SoundEvent soundEvent = net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_STRONG;
        return new LoopingEntitySoundInstance(
                soundEvent,
                SoundSource.PLAYERS,
                BASE_VOLUME,
                1.0f,
                player,
                player.getId()
        );
    }

    private static final class LoopingEntitySoundInstance extends EntityBoundSoundInstance {
        private LoopingEntitySoundInstance(
                SoundEvent soundEvent,
                SoundSource source,
                float volume,
                float pitch,
                Entity entity,
                long seed
        ) {
            super(soundEvent, source, volume, pitch, entity, seed);
            this.looping = true;
            this.delay = 0;
        }

        private void setVolume(float volume) {
            this.volume = volume;
        }
    }
}
