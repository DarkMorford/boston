package com.loadingreadyrun.boston;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod("boston")
public class BostonMod {
    private static final Logger LOGGER = LogManager.getLogger();

    private WebServer webServer;

    public BostonMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("STARTING stats server on localhost:9292");

        webServer = new WebServer(event.getServer());

        try {
            webServer.start();
        } catch (IOException e) {
            LOGGER.error(e);
            System.exit(99);
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if(webServer == null)
            return;

        IWorld world = event.getWorld();

        if (world.isRemote())
            return;

        String playerName = event.getPlayer().getName().getString();
        String blockName = event.getState().getBlock().getRegistryName().toString();

        webServer.countObjectBroken(playerName, blockName);
    }

    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if(webServer == null)
            return;

        String playerName = event.getPlayer().getName().getString();
        String itemName = event.getCrafting().getItem().getRegistryName().toString();
        int itemCount = event.getCrafting().getCount();

        webServer.countItemCrafted(playerName, itemName, itemCount);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(webServer == null)
            return;

        String playerName = event.player.getName().getString();
        BlockPos pos = event.player.getPosition();

        webServer.setPlayerPosition(playerName, pos);
    }

    @SubscribeEvent
    public void onPlayerItemBreak(PlayerDestroyItemEvent event) {
        if(webServer == null)
            return;

        String playerName = event.getPlayer().getName().getString();
        String itemName = event.getOriginal().getItem().getRegistryName().toString();

        webServer.countObjectBroken(playerName, itemName);
    }
}
