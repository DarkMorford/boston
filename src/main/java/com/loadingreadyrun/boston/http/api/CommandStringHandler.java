package com.loadingreadyrun.boston.http.api;

import com.google.gson.Gson;
import com.loadingreadyrun.boston.http.MinecraftMiddleware;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.io.IOUtils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.UUID;

public class CommandStringHandler implements HttpHandler {
    private final Gson gson;

    public CommandStringHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Reader postReader = new InputStreamReader(exchange.getInputStream());
        String cmdString = IOUtils.toString(postReader);

        final MinecraftServer mc = exchange.getAttachment(MinecraftMiddleware.GAME_SERVER);
        ServerWorld mcWorld = mc.func_241755_D_();
        Vector3d spawnPoint = Vector3d.func_237491_b_(mcWorld.func_241135_u_());
        CommandHandler cmdHandler = new CommandHandler();
        ResultHandler resHandler = new ResultHandler();

        CommandSource cmdSource = new CommandSource(cmdHandler, spawnPoint, Vector2f.ZERO, mcWorld, 4,
            "Grapekeeper", new StringTextComponent("Grapekeeper"), mc, null)
            .withResultConsumer(resHandler);

        mc.getCommandManager().handleCommand(cmdSource, cmdString);

        exchange.setStatusCode(200);
    }

    private class CommandHandler implements ICommandSource {

        @Override
        public void sendMessage(ITextComponent component, UUID p_145747_2_) {
            String msg = component.getString();
        }

        @Override
        public boolean shouldReceiveFeedback() {
            return true;
        }

        @Override
        public boolean shouldReceiveErrors() {
            return true;
        }

        @Override
        public boolean allowLogging() {
            return true;
        }
    }

    private class ResultHandler implements ResultConsumer<CommandSource> {
        @Override
        public void onCommandComplete(CommandContext<CommandSource> context, boolean success, int result) {

        }
    }
}
