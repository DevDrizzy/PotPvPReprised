package net.frozenorb.potpvp.kt.util.serialization

import com.google.gson.*
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.*
import org.bukkit.potion.PotionEffect
import java.lang.reflect.Type
import java.util.*

@Suppress
class ItemStackAdapter : JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {
    override fun serialize(item: ItemStack, type: Type, context: JsonSerializationContext): JsonElement {
        return serialize(item)
    }

    @Throws(JsonParseException::class)
    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): ItemStack {
        return deserialize(element)
    }

    companion object {

        fun serialize(item: ItemStack?): JsonElement {
            var item = item
            if (item == null) {
                item = ItemStack(Material.AIR)
            }

            val element = JsonObject()
            element.addProperty("id", item.typeId as Number)
            element.addProperty(getDataKey(item), item.durability as Number)
            element.addProperty("count", item.amount as Number)

            if (item.hasItemMeta()) {
                val meta = item.itemMeta
                if (meta.hasDisplayName()) {
                    element.addProperty("name", meta.displayName)
                }

                if (meta.hasLore()) {
                    element.add("lore", convertStringList(meta.lore) as JsonElement)
                }

                if (meta is LeatherArmorMeta) {
                    element.addProperty("color", meta.color.asRGB() as Number)
                } else if (meta is SkullMeta) {
                    element.addProperty("skull", meta.owner)
                } else if (meta is BookMeta) {
                    element.addProperty("title", meta.title)
                    element.addProperty("author", meta.author)
                    element.add("pages", convertStringList(meta.pages) as JsonElement)
                } else if (meta is PotionMeta) {
                    if (meta.customEffects.isNotEmpty()) {
                        element.add("potion-effects", convertPotionEffectList(meta.customEffects) as JsonElement)
                    }
                } else if (meta is MapMeta) {
                    element.addProperty("scaling", java.lang.Boolean.valueOf(meta.isScaling))
                } else if (meta is EnchantmentStorageMeta) {
                    val storedEnchantments = JsonObject()
                    for ((key, value) in meta.storedEnchants) {
                        storedEnchantments.addProperty(key.name, value as Number)
                    }
                    element.add("stored-enchants", storedEnchantments as JsonElement)
                }
            }

            if (item.enchantments.isNotEmpty()) {
                val enchantments = JsonObject()
                for (entry2 in item.enchantments.entries) {
                    enchantments.addProperty(entry2.key.name, entry2.value as Number)
                }
                element.add("enchants", enchantments as JsonElement)
            }

            return element
        }

        fun deserialize(`object`: JsonElement?): ItemStack {
            if (`object` == null || `object` !is JsonObject) {
                return ItemStack(Material.AIR)
            }
            val element = `object` as JsonObject?
            val id = element!!.get("id").asInt
            val data = (if (element.has("damage")) element.get("damage").asShort else if (element.has("data")) element.get("data").asShort else 0).toShort()
            val count = element.get("count").asInt
            val item = ItemStack(id, count, data)
            val meta = item.itemMeta

            if (element.has("name")) {
                meta.displayName = element.get("name").asString
            }

            if (element.has("lore")) {
                meta.lore = convertStringList(element.get("lore"))
            }

            if (element.has("color")) {
                (meta as LeatherArmorMeta).color = Color.fromRGB(element.get("color").asInt)
            } else if (element.has("skull")) {
                (meta as SkullMeta).owner = element.get("skull").asString
            } else if (element.has("title")) {
                (meta as BookMeta).title = element.get("title").asString
                meta.author = element.get("author").asString
                meta.pages = convertStringList(element.get("pages"))
            } else if (element.has("potion-effects")) {
                val potionMeta = meta as PotionMeta
                for (effect in convertPotionEffectList(element.get("potion-effects"))!!) {
                    potionMeta.addCustomEffect(effect, false)
                }
            } else if (element.has("scaling")) {
                (meta as MapMeta).isScaling = element.get("scaling").asBoolean
            } else if (element.has("stored-enchants")) {
                val enchantments = element.get("stored-enchants") as JsonObject
                for (enchantment in Enchantment.values()) {
                    if (enchantments.has(enchantment.name)) {
                        (meta as EnchantmentStorageMeta).addStoredEnchant(enchantment, enchantments.get(enchantment.name).asInt, true)
                    }
                }
            }

            item.itemMeta = meta

            if (element.has("enchants")) {
                val enchantments = element.get("enchants") as JsonObject
                for (enchantment in Enchantment.values()) {
                    if (enchantments.has(enchantment.name)) {
                        item.addUnsafeEnchantment(enchantment, enchantments.get(enchantment.name).asInt)
                    }
                }
            }

            return item
        }

        private fun getDataKey(item: ItemStack): String {
            if (item.type === Material.AIR) {
                return "data"
            }
            return if (Enchantment.DURABILITY.canEnchantItem(item)) {
                "damage"
            } else "data"
        }

        fun convertStringList(strings: Collection<String>): JsonArray {
            val ret = JsonArray()
            for (string in strings) {
                ret.add(JsonPrimitive(string) as JsonElement)
            }
            return ret
        }

        fun convertStringList(jsonElement: JsonElement): List<String> {
            val array = jsonElement.asJsonArray
            val ret = ArrayList<String>()
            for (element in array) {
                ret.add(element.asString)
            }
            return ret
        }

        fun convertPotionEffectList(potionEffects: Collection<PotionEffect>): JsonArray {
            val ret = JsonArray()
            for (e in potionEffects) {
                ret.add(PotionEffectAdapter.toJson(e) as JsonElement)
            }
            return ret
        }

        fun convertPotionEffectList(jsonElement: JsonElement?): List<PotionEffect>? {
            if (jsonElement == null) {
                return null
            }

            if (!jsonElement.isJsonArray) {
                return null
            }

            val array = jsonElement.asJsonArray
            val ret = ArrayList<PotionEffect>()
            for (element in array) {
                val e = PotionEffectAdapter.fromJson(element) ?: continue
                ret.add(e)
            }

            return ret
        }
    }
}