package com.hestudio.sakisaki;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ClientConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ClientConfig CLIENT;

    public final ForgeConfigSpec.DoubleValue crawlVolume;

    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public ClientConfig(ForgeConfigSpec.Builder builder) {
        builder.push("general");
        crawlVolume = builder
                .comment("Volume of the crawling sound (0.0 - 1.0)")
                .defineInRange("crawlVolume", 1.0, 0.0, 1.0);
        builder.pop();
    }
}
