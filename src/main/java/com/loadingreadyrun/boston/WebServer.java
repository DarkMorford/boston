package com.loadingreadyrun.boston;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.iki.elonen.NanoHTTPD;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebServer extends NanoHTTPD {
    private static final Logger LOGGER = LogManager.getLogger();
    private Map<String, PlayerBean> players;
    private MinecraftServer minecraftServer;

    public WebServer(MinecraftServer mc) {
        super("0.0.0.0", 9292);

        players = new HashMap<>();
        minecraftServer = mc;
    }

    @Override
    public Response serve(IHTTPSession session) {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, List<String>> params = session.getParameters();
        List<String> chunkX = params.get("chunkX");
        List<String> chunkZ = params.get("chunkZ");

        try {
            String output;

            if (chunkX != null) {

                IChunk chunk = minecraftServer.getWorld(DimensionType.OVERWORLD).getChunk(
                        Integer.valueOf(chunkX.get(0)),
                        Integer.valueOf(chunkZ.get(0))
                );
                Map<String, Integer> chunkContent = Util.compositionOf(chunk);

                output = objectMapper.writeValueAsString(chunkContent);
            } else {
                output = objectMapper.writeValueAsString(players);
            }

            return newFixedLengthResponse(Response.Status.OK, "application/json", output);
        } catch (IOException e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", e.toString());
        }
    }

    public void setPlayerPosition(String name, BlockPos playerPosition) {
        PlayerBean player = this.players.getOrDefault(name, new PlayerBean());
        player.setPlayerPosition(playerPosition);

        this.players.put(name, player);
    }

    public void countBlockBroken(String playerName, String blockName) {
        PlayerBean player = this.players.getOrDefault(playerName, new PlayerBean());
        player.countBlockBroken(blockName);

        this.players.put(playerName, player);
    }

    public void countItemCrafted(String playerName, String itemName, int itemCount) {
        PlayerBean player = this.players.getOrDefault(playerName, new PlayerBean());
        player.countItemsBuilt(itemName, itemCount);

        this.players.put(playerName, player);
    }
}
