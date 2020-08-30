package com.loadingreadyrun.boston;

import com.loadingreadyrun.boston.http.WebServer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BostonMod.MOD_ID)
public class BostonMod {
    public static final String MOD_ID = "boston";
    private static final Logger LOGGER = LogManager.getLogger();
    private WebServer httpServer = null;

    public BostonMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Configuration.getServerConfigSpec());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.trace("Entered onServerStarting");

        try {
            LOGGER.debug("Creating HTTP server in stopped state");
            httpServer = new WebServer();
            LOGGER.info("HTTP server created successfully");
        } catch (Exception e) {
            LOGGER.error("Problem creating HTTP server", e);
            throw e;
        }

        LOGGER.trace("Exiting onServerStarting");
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        LOGGER.trace("Entered onServerStarted");

        try {
            LOGGER.trace("Getting Minecraft server from event");
            MinecraftServer mcServer = event.getServer();
            LOGGER.debug("Starting HTTP server");
            httpServer.start(mcServer);
            LOGGER.info("HTTP server started on {}:{}", httpServer.getListenAddress(), httpServer.getListenPort());
        } catch (Exception e) {
            LOGGER.error("Problem starting HTTP server", e);
            throw e;
        }

        LOGGER.trace("Exiting onServerStarted");
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        LOGGER.trace("Entered onServerStopping");

        try {
            LOGGER.debug("Stopping HTTP server");
            httpServer.stop();
            LOGGER.info("HTTP server stopped");
        } catch (Exception e) {
            LOGGER.error("Problem stopping HTTP server", e);
            throw e;
        }

        LOGGER.trace("Exiting onServerStopping");
    }

/*
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (webServer == null)
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
        if (webServer == null)
            return;

        String playerName = event.getPlayer().getName().getString();
        String itemName = event.getCrafting().getItem().getRegistryName().toString();
        int itemCount = event.getCrafting().getCount();

        webServer.countItemCrafted(playerName, itemName, itemCount);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (webServer == null)
            return;

        String playerName = event.player.getName().getString();
        BlockPos pos = event.player.func_233580_cy_();

        webServer.setPlayerPosition(playerName, pos);
    }

    @SubscribeEvent
    public void onPlayerItemBreak(PlayerDestroyItemEvent event) {
        if (webServer == null)
            return;

        String playerName = event.getPlayer().getName().getString();
        String itemName = event.getOriginal().getItem().getRegistryName().toString();

        webServer.countObjectBroken(playerName, itemName);
    }
*/
}
