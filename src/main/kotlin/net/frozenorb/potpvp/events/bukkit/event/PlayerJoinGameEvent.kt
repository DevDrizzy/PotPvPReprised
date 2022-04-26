package net.frozenorb.potpvp.events.bukkit.event

import net.frozenorb.potpvp.events.Game
import net.frozenorb.potpvp.events.GameState
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerJoinGameEvent(val player: Player, val game: Game) : Event() {

    companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

}