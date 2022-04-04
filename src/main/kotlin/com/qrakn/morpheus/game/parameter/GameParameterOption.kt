package com.qrakn.morpheus.game.parameter

import org.bukkit.inventory.ItemStack

interface GameParameterOption {

    fun getDisplayName(): String
    fun getIcon(): ItemStack

}