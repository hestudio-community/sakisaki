package com.hestudio.sakisaki;

import java.util.UUID;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CrawlStateHandler {
    private static final String NBT_CRAWL = "sakisaki_crawling";
    private static final String NBT_CRAWL_SOUND = "sakisaki_crawl_sound";
    private static final String NBT_CRAWL_SOUND_TICK = "sakisaki_crawl_sound_tick";
    private static final UUID CRAWL_SPEED_UUID = UUID.fromString("2b555b50-8d39-4b15-9aa5-6b2b4602a8b0");
    private static final AttributeModifier CRAWL_SPEED_MODIFIER = new AttributeModifier(
            CRAWL_SPEED_UUID,
            "sakisaki_crawl_speed",
            0.5D,
            AttributeModifier.Operation.MULTIPLY_TOTAL
    );

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;
        if (player.level().isClientSide) {
            return;
        }

        boolean crawling = isCrawling(player);
        if (crawling) {
            applyCrawl(player);
        } else {
            clearCrawl(player);
        }

        updateCrawlSound(player, crawling);
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getTarget() instanceof Player targetPlayer)) {
            return;
        }
        if (!(event.getEntity() instanceof net.minecraft.server.level.ServerPlayer trackingPlayer)) {
            return;
        }

        boolean soundActive = targetPlayer.getPersistentData().getBoolean(NBT_CRAWL_SOUND);
        if (!soundActive) {
            return;
        }

        SakiSakiNetwork.sendCrawlSoundStateToPlayer(trackingPlayer, targetPlayer.getId(), true);
    }

    public static boolean isCrawling(Player player) {
        return player.getPersistentData().getBoolean(NBT_CRAWL);
    }

    public static void setCrawling(Player player, boolean crawling) {
        player.getPersistentData().putBoolean(NBT_CRAWL, crawling);
        if (!crawling) {
            clearCrawl(player);
        }
    }

    private static void applyCrawl(Player player) {
        if (player.getPose() != Pose.SWIMMING) {
            player.setPose(Pose.SWIMMING);
            player.refreshDimensions();
        }
        player.setSwimming(true);
        player.setForcedPose(Pose.SWIMMING);

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && speed.getModifier(CRAWL_SPEED_UUID) == null) {
            speed.addTransientModifier(CRAWL_SPEED_MODIFIER);
        }
    }

    private static void clearCrawl(Player player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && speed.getModifier(CRAWL_SPEED_UUID) != null) {
            speed.removeModifier(CRAWL_SPEED_UUID);
        }

        player.setForcedPose(null);
        if (!player.isInWater()) {
            player.setSwimming(false);
        }
        if (player.getPose() == Pose.SWIMMING && player.onGround() && !player.isInWater() && !player.isFallFlying()) {
            player.setPose(Pose.STANDING);
            player.refreshDimensions();
        }
    }

    private static void updateCrawlSound(Player player, boolean crawling) {
        if (!(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) {
            return;
        }

        boolean shouldPlay = crawling;
        boolean wasPlaying = player.getPersistentData().getBoolean(NBT_CRAWL_SOUND);

        if (shouldPlay == wasPlaying) {
            return;
        }

        player.getPersistentData().putBoolean(NBT_CRAWL_SOUND, shouldPlay);
        SakiSakiNetwork.sendCrawlSoundState(serverPlayer, shouldPlay);
    }

}
