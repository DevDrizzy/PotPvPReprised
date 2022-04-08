package net.frozenorb.potpvp.kt.util.serialization

import net.frozenorb.potpvp.PotPvPRP
import com.google.gson.*
import org.bukkit.Location
import java.lang.reflect.Type

class LocationAdapter : JsonDeserializer<Location>, JsonSerializer<Location> {
    override fun serialize(src: Location, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return toJson(src) as JsonElement
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Location? {
        return fromJson(json)
    }

    companion object {
        fun toJson(location: Location?): JsonObject? {
            if (location == null) {
                return null
            }

            val jsonObject = JsonObject()
            jsonObject.addProperty("world", location.getWorld().getName())
            jsonObject.addProperty("x", location.getX() as Number)
            jsonObject.addProperty("y", location.getY() as Number)
            jsonObject.addProperty("z", location.getZ() as Number)
            jsonObject.addProperty("yaw", location.getYaw() as Number)
            jsonObject.addProperty("pitch", location.getPitch() as Number)
            return jsonObject
        }

        fun fromJson(jsonElement: JsonElement?): Location? {
            if (jsonElement == null || !jsonElement.isJsonObject()) {
                return null
            }

            val jsonObject = jsonElement.getAsJsonObject()
            val world = PotPvPRP.getInstance().server.getWorld(jsonObject.get("world").getAsString())
            val x = jsonObject.get("x").getAsDouble()
            val y = jsonObject.get("y").getAsDouble()
            val z = jsonObject.get("z").getAsDouble()
            val yaw = jsonObject.get("yaw").getAsFloat()
            val pitch = jsonObject.get("pitch").getAsFloat()

            return Location(world, x, y, z, yaw, pitch)
        }
    }
}