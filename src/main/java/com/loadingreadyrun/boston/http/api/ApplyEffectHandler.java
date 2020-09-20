package com.loadingreadyrun.boston.http.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loadingreadyrun.boston.http.MinecraftMiddleware;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Objects;

public class ApplyEffectHandler implements HttpHandler {
    private final Gson gson;

    public static final Map<String, Integer> EFFECTS;

    public ApplyEffectHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Reader jReader = new InputStreamReader(exchange.getInputStream());
        JsonObject req = gson.fromJson(jReader, JsonObject.class);
        String playerName = req.get("playerName").getAsString();
        String effectName = req.get("effect").getAsString();
        int duration = req.get("duration").getAsInt();
        int ampLevel = 0;
        if (req.has("level")) {
            ampLevel = req.get("level").getAsInt();
        }

        ResourceLocation effectId = new ResourceLocation("minecraft", effectName);
        if (!ForgeRegistries.POTIONS.containsKey(effectId)) {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
            exchange.getResponseHeaders().add(new HttpString("Content-Type"), "text/plain");
            exchange.getResponseSender().send(String.format("Effect '%s' is not available.", effectName));
            return;
        }

        Effect effectType = Objects.requireNonNull(ForgeRegistries.POTIONS.getValue(effectId));
        EffectInstance effect = new EffectInstance(effectType, duration * 20, ampLevel);

        final MinecraftServer mc = exchange.getAttachment(MinecraftMiddleware.GAME_SERVER);
        PlayerEntity player = mc.getPlayerList().getPlayerByUsername(playerName);
        if (player == null) {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
            exchange.getResponseHeaders().add(new HttpString("Content-Type"), "text/plain");
            exchange.getResponseSender().send(String.format("Player '%s' is not available.", playerName));
            return;
        }

        mc.execute(() -> {
            player.addPotionEffect(effect);
            IFormattableTextComponent chatText = StringTextComponent.field_240750_d_.func_230531_f_();
            chatText.func_230529_a_(player.getDisplayName())
                .func_240702_b_(String.format(" has received %d seconds of ", duration))
                .func_230529_a_(effectType.getDisplayName())
                .func_240702_b_("!");

            mc.getPlayerList().func_232641_a_(chatText, ChatType.SYSTEM, Util.field_240973_b_);
        });

        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "text/plain");
        exchange.getResponseSender().send("OK");
    }

    static {
        Object2IntMap<String> tmpMap = new Object2IntOpenHashMap<>();
        tmpMap.put("speed", 3);
        tmpMap.put("slowness", 3);
        tmpMap.put("haste", 3);
        tmpMap.put("mining_fatigue", 3);
        tmpMap.put("strength", 3);
        tmpMap.put("instant_health", 3);
        tmpMap.put("instant_damage", 3);
        tmpMap.put("jump_boost", 3);
        tmpMap.put("regeneration", 3);
        tmpMap.put("resistance", 3);
        tmpMap.put("fire_resistance", 0);
        tmpMap.put("water_breathing", 0);
        tmpMap.put("invisibility", 0);
        tmpMap.put("blindness", 3);
        tmpMap.put("night_vision", 0);
        tmpMap.put("hunger", 3);
        tmpMap.put("weakness", 0);
        tmpMap.put("poison", 3);
        tmpMap.put("wither", 3);
        tmpMap.put("health_boost", 3);
        tmpMap.put("absorption", 3);
        tmpMap.put("saturation", 3);
        tmpMap.put("glowing", 3);
        tmpMap.put("levitation", 3);
        tmpMap.put("luck", 3);
        tmpMap.put("unluck", 3);
        tmpMap.put("slow_falling", 0);
        tmpMap.put("conduit_power", 3);
        tmpMap.put("dolphins_grace", 3);
        EFFECTS = Object2IntMaps.unmodifiable(tmpMap);
    }
}
