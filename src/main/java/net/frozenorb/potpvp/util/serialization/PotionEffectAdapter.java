package net.frozenorb.potpvp.util.serialization;

import com.google.gson.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Type;

public class PotionEffectAdapter implements JsonDeserializer<PotionEffect>, JsonSerializer<PotionEffect> {

    @Override
    public JsonElement serialize(PotionEffect src, Type typeOfSrc, JsonSerializationContext context) {
        return (toJson(src));
    }

    @Override
    public PotionEffect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return (fromJson(json));
    }

    public static JsonObject toJson(PotionEffect potionEffect) {
        if (potionEffect == null) {
            return (null);
        }

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", potionEffect.getType().getId());
        jsonObject.addProperty("duration", potionEffect.getDuration());
        jsonObject.addProperty("amplifier", potionEffect.getAmplifier());
        jsonObject.addProperty("ambient", potionEffect.isAmbient());

        return (jsonObject);
    }

    public static PotionEffect fromJson(JsonElement jsonElement) {
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            return (null);
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        PotionEffectType effectType = PotionEffectType.getById(jsonObject.get("id").getAsInt());
        int duration = jsonObject.get("duration").getAsInt();
        int amplifier = jsonObject.get("amplifier").getAsInt();
        boolean ambient = jsonObject.get("ambient").getAsBoolean();

        return (new PotionEffect(effectType, duration, amplifier, ambient));
    }

}