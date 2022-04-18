package com.qrakn.morpheus.game.event.impl.brackets

import com.qrakn.morpheus.game.parameter.GameParameter
import com.qrakn.morpheus.game.parameter.GameParameterOption
import net.frozenorb.potpvp.kit.kittype.KitType
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.Potion

object BracketsGameKitParameter : GameParameter {

    private const val DISPLAY_NAME = "Kit"
    private val options = listOf(
            BracketsGameKitOption(KitType.byId("NODEBUFF")),
            BracketsGameKitOption(KitType.byId("SOUP")),
            BracketsGameKitOption(KitType.byId("AXE")),
            BracketsGameKitOption(KitType.byId("CLASSIC"))
    )

    override fun getDisplayName(): String {
        return DISPLAY_NAME
    }

    override fun getOptions(): List<GameParameterOption> {
        return options
    }

    class BracketsGameKitOption(val kit: KitType) : GameParameterOption {
        override fun getDisplayName(): String {
            return kit.displayName
        }

        override fun getIcon(): ItemStack {
            val icon = ItemStack(kit.icon.itemType)

            icon.data = kit.icon

            return icon
        }

        fun getItems(): List<ItemStack> {
            return kit.defaultInventory.take(9)
        }

        fun getArmor(): Array<ItemStack> {
            return kit.defaultArmor
        }

        fun apply(player: Player) {
            player.health = player.maxHealth
            player.foodLevel = 20
            player.inventory.armorContents = getArmor()
            val items = getItems().toMutableList()

            var filler = items[3]
            if (filler != null && filler.type != Material.POTION && filler.type != Material.MUSHROOM_SOUP) {
                filler = ItemStack(Material.AIR)
            }

            for (item in items.toMutableList()) {
                if (item == null) continue

                if (item.type == Material.ENDER_PEARL) {
                    items.remove(item)
                    items.add(filler)
                    continue
                }

                if (item.type == Material.POTION) {
                    val potion = Potion.fromItemStack(item)

                    if (potion.isSplash) {
                        continue
                    }

                    potion.apply(player)
                    items.remove(item)
                    items.add(filler) // im so lazy
                    continue
                }

                if (item.type.isEdible && item.type != Material.MUSHROOM_SOUP && item.type != Material.GOLDEN_APPLE) {
                    items.remove(item)
                    items.add(filler) // im so lazy
                    continue
                }

                if (items[7] != filler && items[8] == filler) {
                    items[8] = item
                    items[7] = filler
                }

            }

            player.inventory.contents = items.toTypedArray()

            if (player.inventory.contains(Material.BOW) && !player.inventory.contains(Material.ARROW)) {
                player.inventory.setItem(17, ItemStack(Material.ARROW, 10))
            }

            player.updateInventory()
        }
    }

}