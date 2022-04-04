package com.qrakn.morpheus.game.event.impl.lms

import com.qrakn.morpheus.game.parameter.GameParameter
import com.qrakn.morpheus.game.parameter.GameParameterOption
import net.frozenorb.potpvp.kittype.KitType
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.Potion

object LastManStandingGameKitParameter : GameParameter {

    private const val DISPLAY_NAME = "Kit"
    private val options = listOf(
            LastManStandingGameKitOption(KitType.byId("NODEBUFF")),
            LastManStandingGameKitOption(KitType.byId("SOUP")),
            LastManStandingGameKitOption(KitType.byId("AXE")),
            LastManStandingGameKitOption(KitType.byId("CLASSIC"))
    )

    override fun getDisplayName(): String {
        return DISPLAY_NAME
    }

    override fun getOptions(): List<GameParameterOption> {
        return options
    }

    class LastManStandingGameKitOption(val kit: KitType) : GameParameterOption {
        override fun getDisplayName(): String {
            return kit.displayName
        }

        override fun getIcon(): ItemStack {
            val icon = ItemStack(kit.icon.itemType)

            icon.data = kit.icon

            return icon
        }

        private fun getItems(): Array<ItemStack> {
            return kit.defaultInventory
        }

        private fun getArmor(): Array<ItemStack> {
            return kit.defaultArmor
        }

        fun apply(player: Player) {
            player.health = player.maxHealth
            player.foodLevel = 20
            player.inventory.armorContents = getArmor()
            player.inventory.contents = getItems()

            player.updateInventory()
        }
    }

}