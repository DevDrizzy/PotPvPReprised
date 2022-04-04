package com.qrakn.morpheus.game.bukkit.event

import com.qrakn.morpheus.game.Game
import com.qrakn.morpheus.game.GameState
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class GameStateChangeEvent(val game: Game, val to: GameState) : Event() {

    companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    init {
        game.state = to
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

}