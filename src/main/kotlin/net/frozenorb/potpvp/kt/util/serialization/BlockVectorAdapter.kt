package net.frozenorb.potpvp.kt.util.serialization

import com.google.gson.*
import org.bukkit.util.BlockVector
import java.lang.reflect.Type

class BlockVectorAdapter : JsonDeserializer<BlockVector>, JsonSerializer<BlockVector> {
    @Throws(JsonParseException::class)
    override fun deserialize(src: JsonElement, type: Type, context: JsonDeserializationContext): BlockVector? {
        return fromJson(src)
    }

    override fun serialize(src: BlockVector, type: Type, context: JsonSerializationContext): JsonElement {
        return toJson(src) as JsonElement
    }

    companion object {
        fun toJson(src: BlockVector?): JsonObject? {
            if (src == null) {
                return null
            }
            val `object` = JsonObject()
            `object`.addProperty("x", src.x as Number)
            `object`.addProperty("y", src.y as Number)
            `object`.addProperty("z", src.z as Number)
            return `object`
        }

        fun fromJson(src: JsonElement?): BlockVector? {
            if (src == null || !src.isJsonObject) {
                return null
            }
            val json = src.asJsonObject
            val x = json.get("x").asDouble
            val y = json.get("y").asDouble
            val z = json.get("z").asDouble
            return BlockVector(x, y, z)
        }
    }
}