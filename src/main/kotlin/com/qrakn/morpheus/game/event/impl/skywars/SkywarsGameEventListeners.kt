package com.qrakn.morpheus.game.event.impl.skywars

import com.qrakn.morpheus.game.GameQueue
import com.qrakn.morpheus.game.GameState
import com.qrakn.morpheus.game.bukkit.event.PlayerGameInteractionEvent
import com.qrakn.morpheus.game.bukkit.event.PlayerQuitGameEvent
import com.qrakn.morpheus.game.event.impl.skywars.loot.SkywarsGameEventLoot
import net.frozenorb.potpvp.PotPvPSI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.random.Random

class SkywarsGameEventListeners : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {
        val game = GameQueue.getCurrentGame(event.player)?: return
        val logic = game.logic as? SkywarsGameEventLogic ?: return

        if (game.players.contains(event.player) && game.state != GameState.STARTING && !game.spectators.contains(event.player)) {
            if (game.arena.bounds.contains(event.block)) {
                if (event.block.type == Material.CHEST) {
                    logic.chests.add(event.block.location)
                }

                event.isCancelled = false

                if (event.block.type == Material.TNT) {
                    val tnt = event.block.world.spawn(event.block.location, TNTPrimed::class.java)
                    tnt.fuseTicks = 30
                    event.block.type = Material.AIR
                }
            }
        }

    }

    @EventHandler
    fun onOpenChestInventoryEvent(event: InventoryOpenEvent) {
        val player = event.player as Player
        val holder = event.inventory.holder

        if (holder is Chest) {
            val game = GameQueue.getCurrentGame(player)?: return
            val logic = game.logic as? SkywarsGameEventLogic ?: return
            val team = logic.get(player)?: return

            if (game.spectators.contains(player) || game.state == GameState.STARTING) {
                event.isCancelled = true
                return
            }

            if (game.players.contains(player)) {
                if (!(logic.chests.contains(holder.location))) {
                    logic.chests.add(holder.location)

                    val option = logic.game.getParameter(SkywarsGameEventTypeParameter.SkywarsGameEventTypeOption::class.java)?: return
                    val items = SkywarsGameEventLoot.getItems(player, logic, (option as SkywarsGameEventTypeParameter.SkywarsGameEventTypeOption).getLoot(), holder.block)

                    for (item in items) {
                        event.inventory.setItem(Random.nextInt(event.inventory.size), item)
                    }
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val game = GameQueue.getCurrentGame(event.player)?: return
        val logic = game.logic as? SkywarsGameEventLogic ?: return
        val team = logic.get(event.player)?: return

        if (game.players.contains(event.player) && game.state != GameState.STARTING && !game.spectators.contains(event.player)) {
            if (game.arena.bounds.contains(event.block) && !team.starting) {
                if (event.block.type == Material.CHEST && !logic.chests.contains(event.block.location)) {
                    val option = logic.game.getParameter(SkywarsGameEventTypeParameter.SkywarsGameEventTypeOption::class.java)?: return
                    val items = SkywarsGameEventLoot.getItems(event.player, logic, (option as SkywarsGameEventTypeParameter.SkywarsGameEventTypeOption).getLoot(), event.block)

                    event.isCancelled = true
                    event.block.type = Material.AIR

                    for (item in items) {
                        event.block.world.dropItem(event.block.location, item)
                    }

                    logic.chests.add(event.block.location)

                    return
                }
                event.isCancelled = false
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        val game = GameQueue.getCurrentGame(event.whoClicked as Player)?: return
        val logic = game.logic as? SkywarsGameEventLogic ?: return

        if (game.players.contains(event.whoClicked as Player) && game.state != GameState.STARTING && !game.spectators.contains(event.whoClicked as Player)) {
            event.isCancelled = false
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        val game = GameQueue.getCurrentGame(event.player)?: return
        val logic = game.logic as? SkywarsGameEventLogic ?: return

        if (game.players.contains(event.player) && game.state != GameState.STARTING && !game.spectators.contains(event.player)) {
            event.isCancelled = false
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPrepareItemCraftEvent(event: PrepareItemCraftEvent) {
        val game = GameQueue.getCurrentGame(event.viewers[0] as Player)?: return
        val logic = game.logic as? SkywarsGameEventLogic ?: return

        if (game.players.contains(event.viewers[0]) && game.state != GameState.STARTING && !game.spectators.contains(event.viewers[0])) {
            event.inventory.result = event.recipe.result
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onCraftItemEvent(event: CraftItemEvent) {
        val game = GameQueue.getCurrentGame(event.whoClicked as Player)?: return
        val logic = game.logic as? SkywarsGameEventLogic ?: return

        if (game.players.contains(event.whoClicked as Player) && game.state != GameState.STARTING && !game.spectators.contains(event.whoClicked as Player)) {
            event.isCancelled = false
        }
    }

}