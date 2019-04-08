package com.loadingreadyrun.boston;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class PlayerBean {
    private Map<String, Integer> blocksBroken;
    private Map<String, Integer> itemsBuilt;

    private BlockPos playerPosion;

    PlayerBean() {
        blocksBroken = new HashMap<>();
        itemsBuilt = new HashMap<>();
        playerPosion = new BlockPos(0,0,0);
    }

    public BlockPos getPlayerPosion() {
        return playerPosion;
    }

    public void setPlayerPosion(BlockPos playerPosion) {
        this.playerPosion = playerPosion;
    }

    public Map<String, Integer> getBlocksBroken() {
        return blocksBroken;
    }

    public void countBlockBroken(String name) {
       Integer currentCount = this.blocksBroken.getOrDefault(name, 0);
       this.blocksBroken.put(name, currentCount + 1);
    }

    public Map<String, Integer> getItemsBuilt() {
        return itemsBuilt;
    }

    public void countItemsBuilt(String name, int itemCount) {
        Integer currentCount = this.itemsBuilt.getOrDefault(name, 0);
        this.itemsBuilt.put(name, currentCount + itemCount);
    }
}
