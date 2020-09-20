package com.loadingreadyrun.boston.http.api;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class ListItemHandler implements HttpHandler {
    private final Gson gson;

    public ListItemHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "text/plain");
        // exchange.getResponseSender().send(gson.toJson(ApplyEffectHandler.EFFECTS));
        exchange.getResponseSender().send("https://minecraft.gamepedia.com/Java_Edition_data_value#Items");
    }
}
