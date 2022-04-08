package net.frozenorb.potpvp.kt.util

import net.frozenorb.potpvp.PotPvPRP
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.WordUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import java.util.ArrayList
import java.util.HashMap
import java.io.IOException
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

object ItemUtils {

    private val craftItemStack = Reflections.getCBClass("inventory.CraftItemStack")!!
    private val asNmsCopyMethod = Reflections.getMethod(craftItemStack, "asNMSCopy", ItemStack::class.java)
    private val nameMap: MutableMap<String, ItemData> = HashMap()

    init {
        nameMap.clear()

        val lines = readLines()
        for (line in lines!!) {
            val parts = line.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            nameMap[parts[0]] = ItemData(Material.getMaterial(Integer.parseInt(parts[1])), java.lang.Short.parseShort(parts[2]))
        }
    }

    @JvmStatic
    fun setDisplayName(itemStack: ItemStack, name: String) {
        val itemMeta = itemStack.itemMeta
        itemMeta.displayName = name
        itemStack.itemMeta = itemMeta
    }

    @JvmStatic
    fun builder(type: Material): ItemBuilder {
        return ItemBuilder(type)
    }

    @JvmStatic
    operator fun get(input: String, amount: Int): ItemStack? {
        val item = get(input)

        if (item != null) {
            item.amount = amount
        }

        return item
    }

    @JvmStatic
    operator fun get(input: String): ItemStack? {
        var input = input
        input = input.toLowerCase().replace(" ", "")

        if (NumberUtils.isInteger(input)) {
            return ItemStack(Material.getMaterial(Integer.parseInt(input)))
        }

        if (input.contains(":")) {
            if (!NumberUtils.isShort(input.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])) {
                return null
            }

            if (NumberUtils.isInteger(input.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])) {
                return ItemStack(Material.getMaterial(Integer.parseInt(input.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])), 1, java.lang.Short.parseShort(input.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]))
            }

            if (!nameMap.containsKey(input.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].toLowerCase())) {
                return null
            }

            val data = nameMap[input.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].toLowerCase()]

            return if (data != null) {
                ItemStack(data.material, 1, java.lang.Short.parseShort(input.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]))
            } else null
        } else {
            return nameMap[input]?.toItemStack()
        }
    }

    @JvmStatic
    fun getName(item: ItemStack): String {
        var name = Reflections.callMethod(asNmsCopyMethod!!.invoke(null, item), "getName") as String

        if (name.contains(".")) {
            name = WordUtils.capitalize(item.type.toString().toLowerCase().replace("_", " "))
        }

        return name
    }

    private fun readLines(): List<String>? {
        return try {
            IOUtils.readLines(PotPvPRP::class.java.classLoader.getResourceAsStream("items.csv"))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    class ItemData(val material: Material, val data: Short) {
        fun getName(): String {
            return getName(this.toItemStack())
        }

        fun matches(item: ItemStack?): Boolean {
            return item != null && item.type === this.material && item.durability == data
        }

        fun toItemStack(): ItemStack {
            return ItemStack(this.material, 1, this.data)
        }
    }

    class ItemBuilder constructor(private var type: Material?) {
        private var amount: Int = 0
        private var data: Short = 0
        private var name: String? = null
        private var lore: MutableList<String>? = null
        private val enchantments: MutableMap<Enchantment, Int>

        init {
            this.amount = 1
            this.data = 0
            this.lore = ArrayList()
            this.enchantments = HashMap()
        }

        fun type(type: Material): ItemBuilder {
            this.type = type
            return this
        }

        fun amount(amount: Int): ItemBuilder {
            this.amount = amount
            return this
        }

        fun data(data: Short): ItemBuilder {
            this.data = data
            return this
        }

        fun name(name: String): ItemBuilder {
            this.name = name
            return this
        }

        fun addLore(vararg lore: String): ItemBuilder {
            this.lore!!.addAll(mutableListOf(*lore))
            return this
        }

        fun addLore(index: Int, lore: String): ItemBuilder {
            this.lore!![index] = lore
            return this
        }

        fun setLore(lore: MutableList<String>): ItemBuilder {
            this.lore = lore
            return this
        }

        fun enchant(enchantment: Enchantment, level: Int): ItemBuilder {
            this.enchantments[enchantment] = level
            return this
        }

        fun unenchant(enchantment: Enchantment): ItemBuilder {
            this.enchantments.remove(enchantment)
            return this
        }

        fun build(): ItemStack {
            val item = ItemStack(this.type, this.amount, this.data)
            val meta = item.itemMeta
            meta.displayName = ChatColor.translateAlternateColorCodes('&', this.name)

            val finalLore = ArrayList<String>()

            for (index in this.lore!!.indices) {
                finalLore[index] = ChatColor.translateAlternateColorCodes('&', this.lore!![index])
            }

            meta.lore = finalLore

            for ((key, value) in this.enchantments) {
                item.addUnsafeEnchantment(key, value)
            }

            item.itemMeta = meta

            return item
        }
    }

}