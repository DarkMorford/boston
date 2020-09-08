package com.loadingreadyrun.boston.http.api;

import com.google.gson.Gson;
import com.loadingreadyrun.boston.http.MinecraftMiddleware;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import io.undertow.util.PathTemplateMatch;
import io.undertow.util.StatusCodes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.StatisticsManager;

import java.lang.reflect.Field;

public class PlayerStatResetHandler implements HttpHandler {
    private final Gson gson;

    public PlayerStatResetHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        final MinecraftServer mc = exchange.getAttachment(MinecraftMiddleware.GAME_SERVER);
        final PathTemplateMatch params = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        final String playerName = params.getParameters().get("name");

        final ServerPlayerEntity player = mc.getPlayerList().getPlayerByUsername(playerName);
        if (player == null)
        {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
            exchange.getResponseHeaders().put(new HttpString("Content-Type"), "text/plain");
            exchange.getResponseSender().send(String.format("Unable to find player entity for %s.", playerName));
            return;
        }
        ServerStatisticsManager playerStats = player.getStats();

        try {
            Field statsData = StatisticsManager.class.getDeclaredField("statsData");
            statsData.setAccessible(true);
            Object rawStats = statsData.get(playerStats);
            Object2IntMap<?> statMap = rawStats instanceof Object2IntMap ? (Object2IntMap<?>) rawStats : null;

            if (statMap != null) {
                playerStats.markAllDirty();
                statMap.clear();
            }
        } catch (IllegalAccessException | NoSuchFieldException ignored) {
            exchange.setStatusCode(StatusCodes.UNPROCESSABLE_ENTITY);
            exchange.getResponseHeaders().put(new HttpString("Content-Type"), "text/plain");
            exchange.getResponseSender().send(String.format("Unable to reset statistics for %s.", playerName));
            return;
        }

        exchange.getResponseHeaders().put(new HttpString("Content-Type"), "text/plain");
        exchange.getResponseSender().send("OK");
    }
}
