package net.frozenorb.potpvp.kt.util.serialization

import com.mongodb.BasicDBList
import com.mongodb.BasicDBObject
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

object ItemStackSerializer {

    private val AIR: BasicDBObject = BasicDBObject()

    init {
        AIR["type"] = "AIR"
        AIR["amount"] = 1
        AIR["data"] = 0
    }

    @JvmStatic
    fun serialize(itemStack: ItemStack?): BasicDBObject {
        if (itemStack == null || itemStack.getType() === Material.AIR) {
            return AIR
        }

        val item = BasicDBObject("type", itemStack.type.toString())
                .append("amount", itemStack.amount)
                .append("data", itemStack.durability)

        val enchants = BasicDBList()
        for (entry in itemStack.enchantments.entries) {
            enchants.add(BasicDBObject("enchantment", entry.key.name).append("level", entry.value))
        }

        if (itemStack.enchantments.isNotEmpty()) {
            item.append("enchants", enchants)
        }

        if (itemStack.hasItemMeta()) {
            val m = itemStack.itemMeta
            val meta = BasicDBObject("displayName", m.displayName)
            item.append("meta", meta)
        }

        return item
    }

    @JvmStatic
    fun deserialize(dbObject: BasicDBObject?): ItemStack {
        if (dbObject == null || dbObject.isEmpty()) {
            return ItemStack(Material.AIR)
        }

        val type = Material.valueOf(dbObject.getString("type"))
        val item = ItemStack(type, dbObject.getInt("amount"))

        item.durability = java.lang.Short.parseShort(dbObject.getString("data"))

        if (dbObject.containsField("enchants")) {
            val enchantments = dbObject.get("enchants") as BasicDBList

            for (o in enchantments) {
                val enchant = o as BasicDBObject
                item.addUnsafeEnchantment(Enchantment.getByName(enchant.getString("enchantment")), enchant.getInt("level"))
            }
        }

        if (dbObject.containsField("meta")) {
            val meta = dbObject.get("meta") as BasicDBObject
            val m = item.itemMeta

            if (meta.containsField("displayName")) {
                m.displayName = meta.getString("displayName")
            }

            if (meta.containsField("lore")) {
                m.lore = arrayListOf()
            }

            item.itemMeta = m
        }
        return item
    }

}