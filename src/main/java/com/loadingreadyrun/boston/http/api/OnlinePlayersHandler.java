package com.loadingreadyrun.boston.http.api;

import com.google.gson.Gson;
import com.loadingreadyrun.boston.http.MinecraftMiddleware;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class OnlinePlayersHandler implements HttpHandler {
    private final Gson gson;

    public OnlinePlayersHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        final MinecraftServer mc = exchange.getAttachment(MinecraftMiddleware.GAME_SERVER);

        List<ServerPlayerEntity> allPlayers = mc.getPlayerList().getPlayers();
        String outText = gson.toJson(allPlayers);

        exchange.getResponseHeaders().put(new HttpString("Content-Type"), "application/json");
        exchange.getResponseSender().send(outText);
    }
}
