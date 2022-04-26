package net.frozenorb.potpvp.events.bukkit.event

import net.frozenorb.potpvp.events.Game
import net.frozenorb.potpvp.events.GameState
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