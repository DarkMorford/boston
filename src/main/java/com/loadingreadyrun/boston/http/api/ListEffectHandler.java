package com.loadingreadyrun.boston.http.api;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class ListEffectHandler implements HttpHandler {
    private final Gson gson;

    public ListEffectHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
        exchange.getResponseSender().send(gson.toJson(ApplyEffectHandler.EFFECTS));
    }
}
