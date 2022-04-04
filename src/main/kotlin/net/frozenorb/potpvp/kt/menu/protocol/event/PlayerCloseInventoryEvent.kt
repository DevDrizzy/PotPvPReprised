package net.frozenorb.potpvp.kt.menu.protocol.event

import org.bukkit.event.HandlerList
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent

class PlayerCloseInventoryEvent(player: Player) : PlayerEvent(player) {

    private val instanceHandlers: HandlerList = handlerList

    override fun getHandlers(): HandlerList {
        return instanceHandlers
    }

    companion object {
        var handlerList: HandlerList = HandlerList()
    }

}