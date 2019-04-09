package com.loadingreadyrun.boston;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

import java.util.HashMap;
import java.util.Map;

public class Util {
    /**
     * Takes a given chunk and returns a human-readable map of
     * every type of solid block present in the chunk.
     * @param chunk
     * @return
     */
    public static Map<String, Integer> compositionOf(IChunk chunk) {
        HashMap<String, Integer> comp = new HashMap<>();

        int blocks = 0;

        for(int blockX = 0; blockX < 16; blockX++) {
            for(int blockY = 0; blockY < 256; blockY++) {
                for(int blockZ = 0; blockZ < 16; blockZ++) {
                    BlockPos pos = new BlockPos(blockX, blockY, blockZ);
                    IBlockState block = chunk.getBlockState(pos);

                    String blockName = block.getBlock().asItem().getRegistryName().toString();

                    Integer currentValue = comp.getOrDefault(blockName, 0);

                    if(block.isSolid()) {
                        comp.put(blockName, currentValue + 1);
                        blocks++;
                    }
                }
            }
        }

        comp.put("_solidBlocks", blocks);

        return comp;
    }
}
