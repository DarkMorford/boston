package com.loadingreadyrun.boston.config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class HttpConfig {
    public static final String SECTION = "http";
    private static final Logger LOGGER = LogManager.getLogger();
    private String listenAddress;
    private int listenPort;

    public HttpConfig(UnmodifiableCommentedConfig configData) {
        UnmodifiableConfig cfg = configData.get(SECTION);
        listenAddress = cfg.get("httpListenAddress");
        listenPort = cfg.get("httpListenPort");
    }

    public static void setupConfigSpec(ForgeConfigSpec.Builder builder) {
        builder.comment("Control panel web server").push(SECTION);

        builder.worldRestart()
            .comment("IP address to listen for connections on")
            .define("httpListenAddress", "0.0.0.0");

        builder.worldRestart()
            .comment("Network port to listen on")
            .defineInRange("httpListenPort", 38911, 1025, 65535);
        builder.pop();
    }

    public String getListenAddress() {
        return listenAddress;
    }

    public int getListenPort() {
        return listenPort;
    }
}
