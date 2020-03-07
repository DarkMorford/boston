package com.loadingreadyrun.boston.twitch;

import net.engio.mbassy.listener.Handler;
import org.apache.logging.log4j.LogManager;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.event.channel.RequestedChannelJoinCompleteEvent;
import org.kitteh.irc.client.library.event.client.ClientNegotiationCompleteEvent;
import org.kitteh.irc.client.library.event.connection.ClientConnectionClosedEvent;
import org.kitteh.irc.client.library.event.connection.ClientConnectionEndedEvent;
import org.kitteh.irc.client.library.event.connection.ClientConnectionFailedEvent;
import org.kitteh.irc.client.library.event.user.PrivateNoticeEvent;
import org.kitteh.irc.client.library.event.user.ServerNoticeEvent;

import java.util.Optional;

public class ConnectHandler {
    private TwitchClient parent;

    private static final String BAD_PASSWORD = "Login authentication failed";
    private static final String WRONG_FORMAT = "Improperly formatted auth";

    ConnectHandler(TwitchClient client) {
        this.parent = client;
    }

    @Handler
    public void onConnectionReady(final ClientNegotiationCompleteEvent event) {
        LogManager.getLogger().info("IRC connection successful");
    }

    @Handler
    public void onConnectionClosed(final ClientConnectionClosedEvent event) {
    }

    @Handler
    public void onConnectionFailure(final ClientConnectionFailedEvent event) {
    }

    @Handler
    public void onServerNotice(final ServerNoticeEvent event) {
    }
}
