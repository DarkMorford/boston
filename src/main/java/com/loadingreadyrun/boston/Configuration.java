package com.loadingreadyrun.boston;

import com.loadingreadyrun.boston.config.HttpConfig;
// import com.loadingreadyrun.boston.config.TwitchConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;

@EventBusSubscriber(modid = BostonMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Configuration {
    private static ForgeConfigSpec serverConfigSpec;

    public static HttpConfig HTTP;
    // public static TwitchConfig TWITCH;

    public static ForgeConfigSpec getServerConfigSpec() {
        if (serverConfigSpec == null) {
            ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
            // TwitchConfig.setupConfigSpec(b);
            HttpConfig.setupConfigSpec(b);
            serverConfigSpec = b.build();
        }
        return serverConfigSpec;
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        HTTP = new HttpConfig(configEvent.getConfig().getConfigData());
        // TWITCH = new TwitchConfig(configEvent.getConfig().getConfigData());
    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {
    }
}
