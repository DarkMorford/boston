package com.loadingreadyrun.boston.twitch;

import com.loadingreadyrun.boston.Configuration;
import com.loadingreadyrun.boston.config.TwitchConfig;
import net.engio.mbassy.listener.Handler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;

public class MinecraftHandler {
    private final TwitchClient client;
    private final MinecraftServer server;

    public MinecraftHandler(TwitchClient client, MinecraftServer minecraftServer) {
        this.client = client;
        this.server = minecraftServer;
    }

    @Handler
    public void dispatchMessage(ChannelMessageEvent event) {
        User sender = event.getActor();
        if (!sender.getNick().equalsIgnoreCase("darkmorford"))
            return;

        String prefix = Configuration.TWITCH.getCommandPrefix();
        String msg = event.getMessage();
        if (!msg.startsWith(prefix))
            return;

        msg = msg.substring(prefix.length()).trim();
        if (msg.toLowerCase().startsWith("tpk")) {
            event.getClient().sendMessage(event.getChannel(), "Initiating TPK");
            String[] onlinePlayers = server.getPlayerList().getOnlinePlayerNames();
            for (String pName : onlinePlayers) {
                ServerPlayerEntity p = server.getPlayerList().getPlayerByUsername(pName);
                if (p == null) return;
                p.onKillCommand();
            }
        }
    }
}
