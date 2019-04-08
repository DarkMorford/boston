package com.loadingreadyrun.boston;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
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
        webServer = new WebServer();
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("STARTING stats server on localhost:9292");

        try {
            webServer.start();
        } catch (IOException e) {
            LOGGER.error(e);
            System.exit(99);
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        IWorld world = event.getWorld();

        if (world.isRemote())
            return;

        String playerName = event.getPlayer().getName().getString();
        String blockName = event.getState().getBlock().asItem().getName().getString();

        webServer.countBlockBroken(playerName, blockName);
    }

    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        String playerName = event.getPlayer().getName().getString();
        String itemName = event.getCrafting().getItem().getName().getString();
        int itemCount = event.getCrafting().getCount();

        webServer.countItemCrafted(playerName, itemName, itemCount);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        String playerName = event.player.getName().getString();
        BlockPos pos = event.player.getPosition();

        webServer.setPlayerPosition(playerName, pos);
    }
}
