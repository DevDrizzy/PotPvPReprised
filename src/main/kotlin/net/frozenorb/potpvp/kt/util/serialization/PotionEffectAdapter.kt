package net.frozenorb.potpvp.kt.util.serialization

import com.google.gson.*
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.lang.reflect.Type

@Suppress
class PotionEffectAdapter : JsonDeserializer<PotionEffect>, JsonSerializer<PotionEffect> {
    override fun serialize(src: PotionEffect, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return toJson(src) as JsonElement
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PotionEffect? {
        return fromJson(json)
    }

    companion object {
        fun toJson(potionEffect: PotionEffect?): JsonObject? {
            if (potionEffect == null) {
                return null
            }

            val jsonObject = JsonObject()
            jsonObject.addProperty("id", potionEffect.type.id as Number)
            jsonObject.addProperty("duration", potionEffect.duration as Number)
            jsonObject.addProperty("amplifier", potionEffect.amplifier as Number)
            jsonObject.addProperty("ambient", java.lang.Boolean.valueOf(potionEffect.isAmbient))
            return jsonObject
        }

        fun fromJson(jsonElement: JsonElement?): PotionEffect? {
            if (jsonElement == null || !jsonElement.isJsonObject) {
                return null
            }

            val jsonObject = jsonElement.asJsonObject
            val effectType = PotionEffectType.getById(jsonObject.get("id").asInt)
            val duration = jsonObject.get("duration").asInt
            val amplifier = jsonObject.get("amplifier").asInt
            val ambient = jsonObject.get("ambient").asBoolean

            return PotionEffect(effectType, duration, amplifier, ambient)
        }
    }
}
