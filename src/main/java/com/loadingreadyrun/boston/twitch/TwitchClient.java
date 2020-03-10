package com.loadingreadyrun.boston.twitch;

import com.loadingreadyrun.boston.Configuration;
import com.loadingreadyrun.boston.config.TwitchConfig;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.feature.twitch.TwitchSupport;

import java.util.List;
import java.util.stream.Collectors;

public class TwitchClient {
    private static final Logger LOGGER = LogManager.getLogger();
    private Client chatClient;
    private ConnectionStatus status = ConnectionStatus.OFFLINE;
    private final MinecraftServer server;

    private ConnectHandler connectHandler;
    private MinecraftHandler minecraftHandler;

    public TwitchClient(MinecraftServer minecraftServer) {
        server = minecraftServer;
        final TwitchConfig config = Configuration.TWITCH;

        if (StringUtils.isBlank(config.getBotName())
          || StringUtils.isBlank(config.getChannelName())
          || StringUtils.isBlank(config.getLoginToken())
          || StringUtils.isBlank(config.getChatServer())) {
            status = ConnectionStatus.MISSING_CREDS;
            chatClient = null;
            return;
        }

        Client.Builder b = Client.builder().name(config.getBotName());
        b.nick(config.getBotName().toLowerCase());
        if (StringUtils.isNotBlank(config.getLoginToken())) {
            b.server().password(config.getLoginToken());
        }
        b.server().host(config.getChatServer());
        this.chatClient = b.realName("Punch-A-Chunk Twitch bot").build();
        // chatClient.addChannel(StringUtils.prependIfMissing(config.getChannelName(), "#"));
        TwitchSupport.addSupport(chatClient);

        connectHandler = new ConnectHandler(this);
        minecraftHandler = new MinecraftHandler(this, minecraftServer);

        chatClient.getEventManager().registerEventListener(connectHandler);
        chatClient.getEventManager().registerEventListener(minecraftHandler);
        chatClient.setInputListener(LOGGER::info);
        chatClient.setOutputListener(LOGGER::info);
    }

    public void beginConnect() {
        if (chatClient != null) {
            status = ConnectionStatus.PENDING;
            chatClient.connect();
        }
    }

    public void disconnect() {
        if (chatClient != null) {
            chatClient.shutdown();
            status = ConnectionStatus.SHUTDOWN;
        }
    }

    public void joinChatRoom() {
        if (chatClient != null) {
            String chatChannel = StringUtils.prependIfMissing(Configuration.TWITCH.getChannelName(), "#");
            chatClient.addChannel(chatChannel);
        }
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    void setStatus(ConnectionStatus updatedStatus) {
        status = updatedStatus;
    }

    public enum ConnectionStatus {
        OFFLINE,
        PENDING,
        CONNECTED,
        SHUTDOWN,
        FAILED_AUTH,
        FAILED_NETWORK,
        MISSING_CREDS
    }
}
