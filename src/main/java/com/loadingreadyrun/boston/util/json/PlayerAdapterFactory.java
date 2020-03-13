package com.loadingreadyrun.boston.util.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.io.IOException;
import java.util.UUID;

public class PlayerAdapterFactory implements TypeAdapterFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!PlayerEntity.class.isAssignableFrom(type.getRawType())) {
            return null;
        }

        return (TypeAdapter<T>) new PlayerAdapter(gson);
    }

    private static class PlayerAdapter extends TypeAdapter<PlayerEntity> {
        private TypeAdapter<BlockPos> blockPosAdapter;
        private TypeAdapter<ChunkPos> chunkPosAdapter;
        private TypeAdapter<UUID> uuidAdapter;

        private PlayerAdapter(Gson gson) {
            blockPosAdapter = gson.getAdapter(BlockPos.class);
            chunkPosAdapter = gson.getAdapter(ChunkPos.class);
            uuidAdapter = gson.getAdapter(UUID.class);
        }

        @Override
        public void write(JsonWriter out, PlayerEntity value) throws IOException {
            out.beginObject();

            out.name("name").value(value.getName().getString());
            out.name("displayName").value(value.getDisplayName().getString());

            out.name("uuid");
            uuidAdapter.write(out, PlayerEntity.getUUID(value.getGameProfile()));

            BlockPos playerPosition = value.getPosition();

            out.name("position");
            blockPosAdapter.write(out, playerPosition);

            out.name("chunk");
            chunkPosAdapter.write(out, new ChunkPos(playerPosition));

            out.endObject();
        }

        @Override
        public PlayerEntity read(JsonReader in) throws IOException {
            return null;
        }
    }
}
