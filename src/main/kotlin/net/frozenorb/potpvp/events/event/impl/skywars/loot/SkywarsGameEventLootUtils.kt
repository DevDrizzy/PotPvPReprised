package net.frozenorb.potpvp.events.event.impl.skywars.loot

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object SkywarsGameEventLootUtils {

    fun hasFullArmor(player: Player): Boolean {
        val inventory = player.inventory
        return (containsType(inventory, "HELMET")
                && containsType(inventory, "CHESTPLATE")
                && containsType(inventory, "LEGGINGS")
                && containsType(inventory, "BOOTS"))
    }

    fun getBetterSword(player: Player, items: List<ItemStack>, inventory: List<ItemStack>): ItemStack? {
        for (item in items) {

            if (containsType(inventory, getType(item))) {
                continue
            }

            val sword = getSword(player)
            if (sword != null) {
                if (isBetter(item, sword)) {
                    return item
                }
            }

            return item
        }
        return null
    }

    fun getBetterArmorItem(player: Player, items: List<ItemStack>, inventory: List<ItemStack>): ItemStack? {
        for (item in items) {

            if (containsType(inventory, getType(item))) {
                continue
            }

            if (containsType(player.inventory, getType(item))) {
                continue
            }

            if (isHelmet(item) && player.inventory.helmet != null) {
                val helmet = player.inventory.helmet

                if (!hasFullArmor(player)) {
                    continue
                }

                if (isBetter(item, helmet)) {
                    return item
                }

                continue
            }

            if (isChestplate(item) && player.inventory.chestplate != null) {
                val chestplate = player.inventory.chestplate

                if (!hasFullArmor(player)) {
                    continue
                }

                if (isBetter(item, chestplate)) {
                    return item
                }

                continue
            }

            if (isLeggings(item) && player.inventory.leggings != null) {
                val leggings = player.inventory.leggings

                if (!hasFullArmor(player)) {
                    continue
                }

                if (isBetter(item, leggings)) {
                    return item
                }

                continue
            }

            if (isBoots(item) && player.inventory.boots != null) {
                val boots = player.inventory.boots

                if (isBetter(item, boots)) {
                    return item
                }

                continue
            }

            return item
        }

        return null
    }


    fun getSword(player: Player): ItemStack? {
        var previous: ItemStack? = null

        for (itemStack in player.inventory.contents) {
            if (itemStack != null && itemStack.type.name.contains("SWORD")) {
                if (previous == null) {
                    previous = itemStack
                } else {
                    if (isBetter(itemStack, previous)) {
                        previous = itemStack
                    }
                }
            }
        }

        return previous
    }

    private fun containsType(inventory: Inventory, type: String): Boolean {
        for (itemStack in inventory.contents) {
            if (itemStack != null) {
                if (itemStack.type.name.contains(type)) {
                    return true
                }
            }
        }
        return false
    }


    private fun containsType(inventory: List<ItemStack?>, type: String): Boolean {
        for (itemStack in inventory) {
            if (itemStack != null) {
                if (itemStack.type.name.contains(type)) {
                    return true
                }
            }
        }
        return false
    }

    private fun getType(itemStack: ItemStack): String {
        var toReturn = itemStack.type.name

        if (itemStack.type.name.contains("HELMET")) toReturn = "HELMET"
        if (itemStack.type.name.contains("CHESTPLATE")) toReturn = "CHESTPLATE"
        if (itemStack.type.name.contains("LEGGINGS")) toReturn = "LEGGINGS"
        if (itemStack.type.name.contains("BOOTS")) toReturn = "BOOTS"
        if (itemStack.type.name.contains("SWORD")) toReturn = "SWORD"

        return toReturn
    }

    private fun getTier(itemStack: ItemStack): Int {
        var toReturn = 0

        if (itemStack.type.name.contains("DIAMOND")) {
            toReturn = 4
        } else if (itemStack.type.name.contains("IRON")) {
            toReturn = 3
        } else if (itemStack.type.name.contains("GOLD") || itemStack.type.name.contains("LEATHER")) {
            toReturn = 2
        } else if (itemStack.type.name.contains("STONE")) {
            toReturn = 1
        }

        return toReturn
    }

    private fun isBetter(first: ItemStack, second: ItemStack): Boolean {
        var firstLevels = 0
        for (enchantment in first.enchantments.keys) {
            firstLevels += first.getEnchantmentLevel(enchantment)
        }

        var secondLevels = 0
        for (enchantment in second.enchantments.keys) {
            secondLevels += second.getEnchantmentLevel(enchantment)
        }

        if (getTier(first) > getTier(second)) {
            return if (secondLevels >= firstLevels + 1) {
                false
            } else true
        }

        return if (getTier(first) == getTier(second)) {
            firstLevels > secondLevels
        } else false

    }

    fun containsBlocks(inventory: List<ItemStack?>): Boolean {
        for (itemStack in inventory) {
            if (itemStack != null && itemStack.type.isBlock) {
                return true
            }
        }
        return false
    }

    fun isHelmet(itemStack: ItemStack): Boolean {
        return itemStack.type.name.contains("HELMET")
    }

    fun isChestplate(itemStack: ItemStack): Boolean {
        return itemStack.type.name.contains("CHESTPLATE")
    }

    fun isLeggings(itemStack: ItemStack): Boolean {
        return itemStack.type.name.contains("LEGGINGS")
    }

    fun isBoots(itemStack: ItemStack): Boolean {
        return itemStack.type.name.contains("BOOTS")
    }
}