package com.loadingreadyrun.boston.http.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loadingreadyrun.boston.http.MinecraftMiddleware;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;
import it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;

import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.stream.Stream;

public class ChunkDetailHandler implements HttpHandler {
    private final Gson gson;

    public ChunkDetailHandler(Gson gson) {
        this.gson = gson;
    }

    public static AxisAlignedBB getChunkBoundingBox(ChunkPos pos, int buildLimitY) {
        BlockPos chunkStart = pos.asBlockPos();
        BlockPos chunkEnd = chunkStart.add(15, buildLimitY - 1, 15);
        return new AxisAlignedBB(chunkStart, chunkEnd);
    }

    @SuppressWarnings("deprecation")
    private static SortedMap<String, Integer> getBlockCounts(Stream<? extends BlockState> contents) {
        Object2IntRBTreeMap<String> blockCounts = new Object2IntRBTreeMap<>();
        Stream<Block> containedBlocks = contents.filter(b -> !b.isAir()).map(BlockState::getBlock);
        containedBlocks.forEach(b -> blockCounts.addTo(Objects.requireNonNull(b.getRegistryName()).toString(), 1));
        return blockCounts;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        final MinecraftServer mc = exchange.getAttachment(MinecraftMiddleware.GAME_SERVER);

        final Map<String, Deque<String>> queryParams = exchange.getQueryParameters();
        boolean haveXCoord = queryParams.containsKey("x") || queryParams.containsKey("X");
        boolean haveZCoord = queryParams.containsKey("z") || queryParams.containsKey("Z");
        if (!(haveXCoord && haveZCoord)) {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
            exchange.getResponseHeaders().add(new HttpString("Content-Type"), "text/plain");
            exchange.getResponseSender().send("X/Z chunk coordinates not specified");
            return;
        }

        int xCoord, zCoord;
        if (queryParams.containsKey("x")) {
            xCoord = Integer.parseInt(queryParams.get("x").getFirst());
        } else {
            xCoord = Integer.parseInt(queryParams.get("X").getFirst());
        }
        if (queryParams.containsKey("z")) {
            zCoord = Integer.parseInt(queryParams.get("z").getFirst());
        } else {
            zCoord = Integer.parseInt(queryParams.get("Z").getFirst());
        }

        // Only support reading Overworld chunks for now
        ServerWorld world = mc.func_241755_D_();
        if (!world.chunkExists(xCoord, zCoord)) {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
            exchange.getResponseHeaders().add(new HttpString("Content-Type"), "text/plain");
            exchange.getResponseSender().send("Specified chunk not yet generated");
            return;
        }

        int buildLimitY = mc.getBuildLimit();
        IChunk chunk = world.getChunk(xCoord, zCoord);
        ChunkPos chunkPos = chunk.getPos();
        Stream<BlockState> blockIterator = chunk.func_234853_a_(getChunkBoundingBox(chunkPos, buildLimitY));

        JsonObject chunkInfo = new JsonObject();

        JsonObject chunkIdentity = new JsonObject();
        JsonObject chunkLocation = new JsonObject();
        chunkLocation.addProperty("x", xCoord);
        chunkLocation.addProperty("z", zCoord);
        chunkIdentity.add("position", chunkLocation);
        chunkInfo.add("chunk", chunkIdentity);

        SortedMap<String, Integer> blockBreakdown = getBlockCounts(blockIterator);
        int totalBlocks = blockBreakdown.values().stream().mapToInt(Integer::intValue).sum();
        chunkInfo.addProperty("total_blocks", totalBlocks);
        chunkInfo.add("block_qty", gson.getAdapter(SortedMap.class).toJsonTree(blockBreakdown));

        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
        exchange.getResponseSender().send(gson.toJson(chunkInfo));
    }
}
