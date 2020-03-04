package com.loadingreadyrun.boston.config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class TwitchConfig {
    public static final String SECTION = "twitch";
    private static final Logger LOGGER = LogManager.getLogger();

    private String botName;
    private String channelName;
    private String chatServer;
    private String commandPrefix;
    private String loginToken;
    private List<String> operators;

    public TwitchConfig(UnmodifiableCommentedConfig configData) {
        UnmodifiableConfig cfg = configData.get(SECTION);
        botName = ((String) cfg.get("botName")).trim().toLowerCase();
        channelName = ((String) cfg.get("channelName")).trim().toLowerCase();
        chatServer = ((String) cfg.get("serverAddress")).trim();
        commandPrefix = ((String) cfg.get("commandPrefix")).trim();
        loginToken = ((String) cfg.get("loginToken")).trim();
        operators = sanitizeOperatorList(cfg.get("operators"));
    }

    public static void setupConfigSpec(Builder builder) {
        builder.comment("Twitch.tv chat integration").push(SECTION);

        builder.worldRestart()
            .comment("Address of the Twitch chat server to connect to")
            .define("serverAddress", "irc.chat.twitch.tv");

        builder.worldRestart()
            .comment("Twitch account to log in as")
            .define("botName", "CraftyBot16");

        builder.worldRestart()
            .comment("Twitch channel to participate in")
            .define("channelName", "loadingreadyrun");

        builder.worldRestart()
            .comment("OAuth token used for login")
            .define("loginToken", "");

        builder
            .comment("Character string used to recognize bot commands")
            .define("commandPrefix", "%");

        List<String> defaultOperators = new ArrayList<>(Arrays.asList(
            "DarkMorford",
            "ThingsOnMyStream"
        ));

        builder
            .comment("Chat users (other than mods) permitted to control the bot")
            .define("operators", defaultOperators);

        builder.pop();
    }

    private static List<String> sanitizeOperatorList(List<String> operators) {
        return operators.stream()
            .map(s -> s.trim().toLowerCase())
            .collect(Collectors.toList());
    }

    public String getBotName() {
        return botName;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChatServer() {
        return chatServer;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix.trim();
    }

    public String getLoginToken() {
        return loginToken;
    }

    public List<String> getOperators() {
        return operators;
    }
}
