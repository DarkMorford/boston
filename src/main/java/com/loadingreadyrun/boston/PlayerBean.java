package com.loadingreadyrun.boston;

import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.Map;

public class PlayerBean {
    private Map<String, Integer> objectsBroken;
    private Map<String, Integer> itemsBuilt;

    private Triple<Integer, Integer, Integer> playerPosition;

    PlayerBean() {
        objectsBroken = new HashMap<>();
        itemsBuilt = new HashMap<>();
        playerPosition = new ImmutableTriple<>(0,0,0);
    }

    public Triple<Integer, Integer, Integer> getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(BlockPos playerPosition) {
        this.playerPosition = new ImmutableTriple<>(playerPosition.getX(), playerPosition.getY(), playerPosition.getZ());
    }

    public Map<String, Integer> getObjectsBroken() {
        return objectsBroken;
    }

    public void countObjectBroken(String name) {
       Integer currentCount = this.objectsBroken.getOrDefault(name, 0);
       this.objectsBroken.put(name, currentCount + 1);
    }

    public Map<String, Integer> getItemsBuilt() {
        return itemsBuilt;
    }

    public void countItemsBuilt(String name, int itemCount) {
        Integer currentCount = this.itemsBuilt.getOrDefault(name, 0);
        this.itemsBuilt.put(name, currentCount + itemCount);
    }
}
