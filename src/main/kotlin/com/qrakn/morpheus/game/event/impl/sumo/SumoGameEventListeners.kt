package com.qrakn.morpheus.game.event.impl.sumo

import com.qrakn.morpheus.game.GameQueue
import com.qrakn.morpheus.game.GameState
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerMoveEvent

class SumoGameEventListeners : Listener {

    @EventHandler
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (event.from.blockX != event.to.blockX || event.from.blockY != event.to.blockY || event.from.blockZ != event.to.blockZ) {
            val game = GameQueue.getCurrentGame(event.player) ?: return
            val logic = game.logic as? SumoGameEventLogic ?: return
            val participant = logic.get(event.player) ?: return

            if (event.to.blockY + 5 < game.getFirstSpawnLocations()[0].blockY && participant.fighting) {
                participant.died(event.player)

                if (participant.isFinished()) {
                    logic.check()
                } else {
                    game.addSpectator(event.player)
                }
            }

        }
    }

    @EventHandler
    fun onPlayerDamageEvent(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            val game = GameQueue.getCurrentGame(player) ?: return
            val logic = game.logic as? SumoGameEventLogic ?: return

            if (game.event != SumoGameEvent) {
                return
            }

            if (game.players.contains(player) && game.state != GameState.STARTING) {
                val participant = logic.get(player)

                if (participant != null) {
                    if (participant.fighting && !participant.hasDied(player)) {
                        event.damage = 0.0
                        event.isCancelled = false
                        return
                    }
                }

                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onFoodLevelChangeEvent(event: FoodLevelChangeEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            val game = GameQueue.getCurrentGame(player) ?: return
            val logic = game.logic as? SumoGameEventLogic ?: return

            if (game.event != SumoGameEvent) {
                return
            }

            if (game.players.contains(player)) {
                event.foodLevel = 20
            }
        }
    }

}