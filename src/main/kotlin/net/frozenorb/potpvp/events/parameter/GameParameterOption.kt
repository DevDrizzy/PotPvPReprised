package net.frozenorb.potpvp.events.parameter

import org.bukkit.inventory.ItemStack

interface GameParameterOption {

    fun getDisplayName(): String
    fun getIcon(): ItemStack

}