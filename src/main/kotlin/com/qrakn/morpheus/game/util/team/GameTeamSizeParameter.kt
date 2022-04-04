package com.qrakn.morpheus.game.util.team

import com.qrakn.morpheus.game.parameter.GameParameter
import com.qrakn.morpheus.game.parameter.GameParameterOption
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object GameTeamSizeParameter : GameParameter {

    private const val DISPLAY_NAME = "Team Size"

    override fun getDisplayName(): String {
        return DISPLAY_NAME
}

    override fun getOptions(): List<GameParameterOption> {
        return listOf(Singles, Duos)
    }

    object Singles : GameParameterOption {
        private const val DISPLAY_NAME = "1v1"
        private val icon = ItemStack(Material.DIAMOND_HELMET)

        override fun getDisplayName(): String {
            return DISPLAY_NAME
        }

        override fun getIcon(): ItemStack {
            return icon
        }
    }

    object Duos : GameParameterOption {
        private const val DISPLAY_NAME = "2v2"
        private val icon = ItemStack(Material.DIAMOND_HELMET, 2)

        override fun getDisplayName(): String {
            return DISPLAY_NAME
        }

        override fun getIcon(): ItemStack {
            return icon
        }
    }

}