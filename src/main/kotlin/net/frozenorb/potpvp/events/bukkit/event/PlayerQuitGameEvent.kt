package net.frozenorb.potpvp.events.bukkit.event

import net.frozenorb.potpvp.events.Game
import net.frozenorb.potpvp.PotPvPRP
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerQuitGameEvent(val player: Player, val game: Game) : Event() {

    companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    init {
        Bukkit.getScheduler().runTaskLater(PotPvPRP.getInstance(), {
            game.players.remove(player)
            game.spectators.remove(player)
        }, 2L)
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

}