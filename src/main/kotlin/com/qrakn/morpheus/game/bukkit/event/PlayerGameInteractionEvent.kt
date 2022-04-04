package com.qrakn.morpheus.game.bukkit.event

import com.qrakn.morpheus.game.Game
import com.qrakn.morpheus.game.GameState
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerGameInteractionEvent(val player: Player, val game: Game) : Event() {

    companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

}