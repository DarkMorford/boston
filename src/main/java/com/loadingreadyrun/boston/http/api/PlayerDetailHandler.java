package com.loadingreadyrun.boston.http.api;

import com.google.gson.Gson;
import com.loadingreadyrun.boston.http.MinecraftMiddleware;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import io.undertow.util.PathTemplateMatch;
import io.undertow.util.StatusCodes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;

import java.util.HashMap;
import java.util.Map;

public class PlayerDetailHandler implements HttpHandler {
    private final Gson gson;

    public PlayerDetailHandler(Gson gson) {
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
            exchange.setStatusCode(StatusCodes.SERVICE_UNAVAILABLE);
            exchange.getResponseHeaders().put(new HttpString("Content-Type"), "text/plain");
            exchange.getResponseSender().send(String.format("Unable to find player entity for %s.", playerName));
            return;
        }

        ServerStatisticsManager playerStats = player.getStats();
        Map<String, Integer> killedThings = new HashMap<>();
        for (Stat<EntityType<?>> e : Stats.ENTITY_KILLED) {
            killedThings.put(e.getValue().toString(), playerStats.getValue(e));
        }

        exchange.getResponseHeaders().put(new HttpString("Content-Type"), "text/plain");
        exchange.getResponseSender().send(gson.toJson(killedThings));
    }
}
