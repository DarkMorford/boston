package com.loadingreadyrun.boston.http;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.util.AttachmentKey;
import io.undertow.util.StatusCodes;
import net.minecraft.server.MinecraftServer;

public class MinecraftMiddleware implements HttpHandler {
    public static final AttachmentKey<MinecraftServer> GAME_SERVER = AttachmentKey.create(MinecraftServer.class);
    private final HttpHandler nextHandler;
    private MinecraftServer gameServer;

    public MinecraftMiddleware(final HttpHandler next) {
        this(next, null);
    }

    public MinecraftMiddleware(final HttpHandler next, final MinecraftServer server) {
        this.nextHandler = next;
        this.gameServer = server;
    }

    public void setGameServer(MinecraftServer server) {
        this.gameServer = server;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (gameServer == null) {
            ResponseCodeHandler.HANDLE_403.handleRequest(exchange);
            return;
        }

        boolean hasAuth = exchange.getQueryParameters().containsKey("auth");
        if (!(hasAuth && exchange.getQueryParameters().get("auth").peekFirst().equalsIgnoreCase("nifty"))) {
            exchange.setStatusCode(StatusCodes.UNAUTHORIZED);
            return;
        }

        exchange.putAttachment(GAME_SERVER, gameServer);
        nextHandler.handleRequest(exchange);
    }
}
