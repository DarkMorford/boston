package com.loadingreadyrun.boston.http.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loadingreadyrun.boston.http.MinecraftMiddleware;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

public class ItemGiftHandler implements HttpHandler {
    private final Gson gson;

    public ItemGiftHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Reader jReader = new InputStreamReader(exchange.getInputStream());
        JsonObject req = gson.fromJson(jReader, JsonObject.class);

        String playerName = req.get("playerName").getAsString();
        String itemName = req.get("item").getAsString();
        int quantity = req.get("count").getAsInt();

        ResourceLocation itemId = new ResourceLocation("minecraft", itemName);
        if (!ForgeRegistries.ITEMS.containsKey(itemId)) {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
            exchange.getResponseHeaders().add(new HttpString("Content-Type"), "text/plain");
            exchange.getResponseSender().send(String.format("Item '%s' is not available.", itemName));
            return;
        }

        Item itemType = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(itemId));
        ItemStack stak = new ItemStack(itemType, quantity);

        final MinecraftServer mc = exchange.getAttachment(MinecraftMiddleware.GAME_SERVER);
        PlayerEntity player = mc.getPlayerList().getPlayerByUsername(playerName);
        if (player == null) {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
            exchange.getResponseHeaders().add(new HttpString("Content-Type"), "text/plain");
            exchange.getResponseSender().send(String.format("Player '%s' is not available.", playerName));
            return;
        }

        mc.execute(() -> {
            ItemHandlerHelper.giveItemToPlayer(player, stak);
            IFormattableTextComponent chatText = StringTextComponent.field_240750_d_.func_230531_f_();
            chatText.func_230529_a_(player.getDisplayName())
                .func_240702_b_(String.format(" has received a "))
                .func_230529_a_(stak.getDisplayName())
                .func_240702_b_("! (Or several!)");

            mc.getPlayerList().func_232641_a_(chatText, ChatType.SYSTEM, Util.field_240973_b_);
        });

        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "text/plain");
        exchange.getResponseSender().send("OK");
    }
}
