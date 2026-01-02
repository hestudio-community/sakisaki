package com.hestudio.sakisaki;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public final class RemoteCrawlSoundController {
    private static final float BASE_VOLUME = 1.0f;
    private static final Set<Integer> CRAWLING_ENTITIES = new HashSet<>();
    private static final Map<Integer, LoopingEntitySoundInstance> ACTIVE_SOUNDS = new HashMap<>();

    private RemoteCrawlSoundController() {
    }

    public static void handleSoundState(CrawlSoundMessage message) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.getId() == message.entityId()) {
            return;
        }
        if (message.active()) {
            CRAWLING_ENTITIES.add(message.entityId());
        } else {
            CRAWLING_ENTITIES.remove(message.entityId());
            stop(message.entityId());
        }
    }

    public static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            CRAWLING_ENTITIES.clear();
            ACTIVE_SOUNDS.values().forEach(sound -> minecraft.getSoundManager().stop(sound));
            ACTIVE_SOUNDS.clear();
            return;
        }

        ACTIVE_SOUNDS.entrySet().removeIf(entry -> !minecraft.getSoundManager().isActive(entry.getValue()));

        for (Integer entityId : CRAWLING_ENTITIES) {
            Entity entity = minecraft.level.getEntity(entityId);
            if (entity == null || !entity.isAlive()) {
                if (ACTIVE_SOUNDS.containsKey(entityId)) {
                    stop(entityId);
                }
                continue;
            }

            boolean moving = isMoving(entity);
            boolean playing = ACTIVE_SOUNDS.containsKey(entityId);

            if (moving && !playing) {
                start(entity);
            } else if (!moving && playing) {
                stop(entityId);
            }
        }
    }

    private static boolean isMoving(Entity entity) {
        double dx = entity.getX() - entity.xo;
        double dz = entity.getZ() - entity.zo;
        return (dx * dx + dz * dz) > 1.0E-4D;
    }

    private static void start(Entity entity) {
        if (ACTIVE_SOUNDS.containsKey(entity.getId())) {
            return;
        }

        LoopingEntitySoundInstance sound = createLoopSound(entity);
        sound.setVolume(BASE_VOLUME);
        Minecraft.getInstance().getSoundManager().play(sound);
        ACTIVE_SOUNDS.put(entity.getId(), sound);
    }

    private static void stop(int entityId) {
        LoopingEntitySoundInstance sound = ACTIVE_SOUNDS.remove(entityId);
        if (sound != null) {
            Minecraft.getInstance().getSoundManager().stop(sound);
        }
    }

    private static LoopingEntitySoundInstance createLoopSound(Entity entity) {
        SoundEvent soundEvent = net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_STRONG;
        return new LoopingEntitySoundInstance(
                soundEvent,
                SoundSource.PLAYERS,
                BASE_VOLUME,
                1.0f,
                entity,
                entity.getId()
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