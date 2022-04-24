package net.frozenorb.potpvp.util.serialization;

import com.google.gson.*;
import org.bukkit.util.BlockVector;

import java.lang.reflect.Type;

public class BlockVectorAdapter implements JsonDeserializer<BlockVector>, JsonSerializer<BlockVector> {

    @Override
    public BlockVector deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        return fromJson(src);
    }

    @Override
    public JsonElement serialize(BlockVector src, Type type, JsonSerializationContext context) {
        return toJson(src);
    }

    public static JsonObject toJson(BlockVector src) {
        if (src == null) {
            return null;
        }

        final JsonObject object = new JsonObject();

        object.addProperty("x", src.getX());
        object.addProperty("y", src.getY());
        object.addProperty("z", src.getZ());

        return object;
    }

    public static BlockVector fromJson(JsonElement src) {
        if (src == null || !src.isJsonObject()) {
            return null;
        }
        final JsonObject json = src.getAsJsonObject();

        final double x = json.get("x").getAsDouble();
        final double y = json.get("y").getAsDouble();
        final double z = json.get("z").getAsDouble();

        return new BlockVector(x, y, z);
    }

}
