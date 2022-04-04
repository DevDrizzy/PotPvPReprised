package net.frozenorb.potpvp.kt.util.serialization

import com.google.gson.*
import org.bukkit.util.Vector
import java.lang.reflect.Type

class VectorAdapter : JsonDeserializer<Vector>, JsonSerializer<Vector> {
    @Throws(JsonParseException::class)
    override fun deserialize(src: JsonElement, type: Type, context: JsonDeserializationContext): Vector? {
        return fromJson(src)
    }

    override fun serialize(src: Vector, type: Type, context: JsonSerializationContext): JsonElement {
        return toJson(src) as JsonElement
    }

    companion object {

        fun toJson(src: Vector?): JsonObject? {
            if (src == null) {
                return null
            }
            val `object` = JsonObject()
            `object`.addProperty("x", src.getX() as Number)
            `object`.addProperty("y", src.getY() as Number)
            `object`.addProperty("z", src.getZ() as Number)
            return `object`
        }

        fun fromJson(src: JsonElement?): Vector? {
            if (src == null || !src.isJsonObject()) {
                return null
            }
            val json = src.getAsJsonObject()
            val x = json.get("x").getAsDouble()
            val y = json.get("y").getAsDouble()
            val z = json.get("z").getAsDouble()
            return Vector(x, y, z)
        }
    }
}
