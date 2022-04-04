package net.frozenorb.potpvp.kt.visibility

import org.bukkit.entity.Player

interface VisibilityHandler {
    fun getAction(toRefresh: Player, refreshFor: Player): VisibilityAction
}