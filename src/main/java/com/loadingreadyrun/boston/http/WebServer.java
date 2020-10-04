package com.loadingreadyrun.boston.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loadingreadyrun.boston.Configuration;
import com.loadingreadyrun.boston.config.HttpConfig;
import com.loadingreadyrun.boston.http.api.*;
import com.loadingreadyrun.boston.util.json.PlayerAdapterFactory;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.DisableCacheHandler;
import io.undertow.server.handlers.error.SimpleErrorPageHandler;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebServer {
    private static final Logger LOGGER = LogManager.getLogger();

    private final MinecraftMiddleware mcMiddleware;
    private final Undertow httpServer;

    public WebServer() {
        final Gson gson = buildGson();
        final HttpConfig config = Configuration.HTTP;

        LOGGER.debug("Building HTTP handler chain");
        RoutingHandler mainRouter = new RoutingHandler();
        this.mcMiddleware = new MinecraftMiddleware(mainRouter);
        HttpHandler cacheKiller = new DisableCacheHandler(mcMiddleware);
        HttpHandler errorPages = new SimpleErrorPageHandler(cacheKiller);

        LOGGER.debug("Adding HTTP GET routes");
        mainRouter.get("/api/chunks", new ChunkDetailHandler(gson));
        mainRouter.get("/api/effects", new ListEffectHandler(gson));
        mainRouter.get("/api/items", new ListItemHandler(gson));
        mainRouter.get("/api/players", new OnlinePlayersHandler(gson));
        mainRouter.get("/api/players/{name}", new PlayerDetailHandler(gson));

        LOGGER.debug("Adding HTTP POST routes");
        mainRouter.post("/api/command", new BlockingHandler(new CommandStringHandler(gson)));
        mainRouter.post("/api/effects", new BlockingHandler(new ApplyEffectHandler(gson)));
        mainRouter.post("/api/items", new BlockingHandler(new ItemGiftHandler(gson)));
        mainRouter.post("/api/players/{name}/reset", new PlayerStatResetHandler(gson));

        LOGGER.debug("Finalizing HTTP server creation");
        this.httpServer = Undertow.builder()
            .addHttpListener(config.getListenPort(), config.getListenAddress(), errorPages)
            .build();
    }

    public String getListenAddress() {
        return Configuration.HTTP.getListenAddress();
    }

    public int getListenPort() {
        return Configuration.HTTP.getListenPort();
    }

    public void start(MinecraftServer server) {
        mcMiddleware.setGameServer(server);
        httpServer.start();
    }

    public void stop() {
        httpServer.stop();
    }

    private Gson buildGson() {
        GsonBuilder b = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping();

        b.registerTypeAdapterFactory(new PlayerAdapterFactory());

        return b.create();
    }
}
