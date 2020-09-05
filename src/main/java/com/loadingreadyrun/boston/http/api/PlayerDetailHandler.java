package com.loadingreadyrun.boston.http.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loadingreadyrun.boston.http.MinecraftMiddleware;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import io.undertow.util.PathTemplateMatch;
import io.undertow.util.StatusCodes;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;
import java.util.UUID;

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
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
            exchange.getResponseHeaders().put(new HttpString("Content-Type"), "text/plain");
            exchange.getResponseSender().send(String.format("Unable to find player entity for %s.", playerName));
            return;
        }

        JsonObject detailInfo = new JsonObject();
        ServerStatisticsManager playerStats = player.getStats();

        JsonObject playerIdentity = new JsonObject();
        playerIdentity.addProperty("name", player.getName().getString());
        playerIdentity.addProperty("displayName", player.getDisplayName().getString());
        playerIdentity.add("uuid", gson.getAdapter(UUID.class).toJsonTree(player.getUniqueID()));
        playerIdentity.add("position", gson.getAdapter(BlockPos.class).toJsonTree(player.func_233580_cy_()));
        playerIdentity.addProperty("death_count", playerStats.getValue(Stats.CUSTOM.get(Stats.DEATHS)));
        Stat<?> fallStat = Stats.CUSTOM.get(Stats.FALL_ONE_CM);
        playerIdentity.addProperty("fall_distance", fallStat.format(playerStats.getValue(fallStat)));
        detailInfo.add("player", playerIdentity);

        JsonObject killedEntities = new JsonObject();
        for (Stat<EntityType<?>> e : Stats.ENTITY_KILLED) {
            int count = playerStats.getValue(e);
            if (count != 0) {
                String entryName = Objects.requireNonNull(e.getValue().getRegistryName()).toString();
                killedEntities.addProperty(entryName, count);
            }
        }
        detailInfo.add("entity_killed", killedEntities);

        JsonObject killerEntities = new JsonObject();
        for (Stat<EntityType<?>> e : Stats.ENTITY_KILLED_BY) {
            int count = playerStats.getValue(e);
            if (count != 0) {
                String entryName = Objects.requireNonNull(e.getValue().getRegistryName()).toString();
                killerEntities.addProperty(entryName, count);
            }
        }
        detailInfo.add("entity_killed_by", killerEntities);

        JsonObject minedBlocks = new JsonObject();
        for (Stat<Block> b : Stats.BLOCK_MINED) {
            int count = playerStats.getValue(b);
            if (count != 0) {
                String entryName = Objects.requireNonNull(b.getValue().getRegistryName()).toString();
                minedBlocks.addProperty(entryName, count);
            }
        }
        detailInfo.add("block_mined", minedBlocks);

        JsonObject craftedItems = new JsonObject();
        for (Stat<Item> i : Stats.ITEM_CRAFTED) {
            int count = playerStats.getValue(i);
            if (count != 0) {
                String entryName = Objects.requireNonNull(i.getValue().getRegistryName()).toString();
                craftedItems.addProperty(entryName, count);
            }
        }
        detailInfo.add("item_crafted", craftedItems);

        JsonObject brokenItems = new JsonObject();
        for (Stat<Item> i : Stats.ITEM_BROKEN) {
            int count = playerStats.getValue(i);
            if (count != 0) {
                String entryName = Objects.requireNonNull(i.getValue().getRegistryName()).toString();
                brokenItems.addProperty(entryName, count);
            }
        }
        detailInfo.add("item_broken", brokenItems);

        exchange.getResponseHeaders().put(new HttpString("Content-Type"), "application/json");
        exchange.getResponseSender().send(gson.toJson(detailInfo));
    }
}
