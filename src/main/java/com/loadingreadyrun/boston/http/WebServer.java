package com.loadingreadyrun.boston.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loadingreadyrun.boston.Configuration;
import com.loadingreadyrun.boston.config.HttpConfig;
import com.loadingreadyrun.boston.http.api.OnlinePlayersHandler;
import com.loadingreadyrun.boston.http.api.PlayerDetailHandler;
import com.loadingreadyrun.boston.util.json.PlayerAdapterFactory;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.DisableCacheHandler;
import io.undertow.server.handlers.error.SimpleErrorPageHandler;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

public class WebServer {
    private static final Logger LOGGER = LogManager.getLogger();

    private MinecraftMiddleware mcMiddleware;
    private RoutingHandler mainRouter;
    private Undertow httpServer;

    public WebServer() {
        final Gson gson = buildGson();
        final HttpConfig config = Configuration.HTTP;

        this.mainRouter = new RoutingHandler();
        this.mcMiddleware = new MinecraftMiddleware(mainRouter);
        HttpHandler cacheKiller = new DisableCacheHandler(mcMiddleware);
        HttpHandler errorPages = new SimpleErrorPageHandler(cacheKiller);

        mainRouter.get("/api/players", new OnlinePlayersHandler(gson));
        mainRouter.get("/api/players/{name}", new PlayerDetailHandler(gson));

        this.httpServer = Undertow.builder()
            .addHttpListener(config.getListenPort(), config.getListenAddress(), errorPages)
            .build();
    }

    public String getListenAddress() {
        InetSocketAddress address = (InetSocketAddress) this.httpServer.getListenerInfo().get(0).getAddress();
        return address.getAddress().getHostAddress();
    }

    public int getListenPort() {
        InetSocketAddress address = (InetSocketAddress) this.httpServer.getListenerInfo().get(0).getAddress();
        return address.getPort();
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
