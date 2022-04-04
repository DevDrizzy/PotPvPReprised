package com.qrakn.morpheus.game

import com.qrakn.morpheus.game.bukkit.event.PlayerQuitGameEvent
import com.qrakn.morpheus.game.event.GameEvent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.player.PlayerQuitEvent

class GameListeners : Listener {

    @EventHandler
    fun onPlayerDamageEvent(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val game = GameQueue.getCurrentGame(event.entity as Player)

            if (game != null && game.state == GameState.STARTING && game.players.contains(event.entity as Player)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        if (event.damager is Player) {
            val game = GameQueue.getCurrentGame(event.damager as Player)
            if (game != null && game.spectators.contains(event.damager as Player)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val game = GameQueue.getCurrentGame(event.player)

        if (game != null && game.players.contains(event.player)) {
            Bukkit.getPluginManager().callEvent(PlayerQuitGameEvent(event.player, game))
        }

        val iterator = GameQueue.games.iterator()
        while (iterator.hasNext()) {
            val other = iterator.next()
            if (other.host == event.player && other.state == GameState.QUEUED) {
                iterator.remove()
            }
        }

    }

    @EventHandler
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        val game = GameQueue.getCurrentGame(event.player)

        if (game != null && game.players.contains(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerInventoryClickEvent(event: InventoryClickEvent) {
        val game = GameQueue.getCurrentGame(event.whoClicked as Player)

        if (game != null && game.state == GameState.STARTING && game.players.contains(event.whoClicked as Player)) {
            event.isCancelled = true
        }

        if (game != null && game.spectators.contains(event.whoClicked as Player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerPickupItemEvent(event: PlayerPickupItemEvent) {
        val game = GameQueue.getCurrentGame(event.player)
        if (game != null && game.spectators.contains(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onFoodLevelChangeEvent(event: FoodLevelChangeEvent) {
        val game = GameQueue.getCurrentGame(event.entity as Player)
        if (game != null && game.state == GameState.STARTING && game.spectators.contains(event.entity as Player)) {
            event.foodLevel = 20
        }
    }


    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        val game = GameQueue.getCurrentGame(event.player)

        if (game != null && GameEvent.leaveItem == event.item) {
            Bukkit.getPluginManager().callEvent(PlayerQuitGameEvent(event.player, game))
            event.player.sendMessage(ChatColor.RED.toString() + "You left the " + game.event.getName() + " event.")
            return
        }

        if (game != null && game.spectators.contains(event.player)) {
            event.isCancelled = true
        }
    }

}