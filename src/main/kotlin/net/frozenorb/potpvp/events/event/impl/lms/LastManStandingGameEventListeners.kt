package net.frozenorb.potpvp.events.event.impl.lms

import net.frozenorb.potpvp.events.GameHandler
import net.frozenorb.potpvp.events.bukkit.event.PlayerGameInteractionEvent
import net.frozenorb.potpvp.events.bukkit.event.PlayerQuitGameEvent
import net.frozenorb.potpvp.PotPvPRP
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class LastManStandingGameEventListeners : Listener {

    @EventHandler
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (event.from.blockX != event.to.blockX || event.from.blockY != event.to.blockY || event.from.blockZ != event.to.blockZ) {
            val game = PotPvPRP.getInstance().gameHandler.getCurrentGame(event.player) ?: return
            val logic = game.logic as? LastManStandingGameEventLogic ?: return

            if (game.spectators.contains(event.player) && event.to.blockY <= 0) {
                event.player.teleport(game.arena.team1Spawn)
                return
            }

            val participant = logic.get(event.player) ?: return

            if (participant.starting) {
                if (event.from.blockX != event.to.blockX || event.from.blockZ != event.to.blockZ) {
                    event.player.teleport(event.from)
                    event.player.velocity = Vector(0, -1, 0)
                    return
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            val game = PotPvPRP.getInstance().gameHandler.getCurrentGame(player) ?: return
            if (game.logic !is LastManStandingGameEventLogic) return
            val participant = game.logic.get(player) ?: return

            if (participant.starting) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            val game = PotPvPRP.getInstance().gameHandler.getCurrentGame(player) ?: return
            if (game.logic !is LastManStandingGameEventLogic) return
            val participant = game.logic.get(player)?: return

            if (game.players.contains(player)) {
                var allowed = event.damager is Player

                if (event.damager is Projectile) {
                    allowed = (event.damager as Projectile).shooter is Player
                }

                if (allowed) {
                    if (participant.players.contains(event.damager)) {
                        event.isCancelled = true
                        return
                    }

                    if (event.damager is Projectile && participant.players.contains((event.damager as Projectile).shooter)) {
                        event.isCancelled = true
                        return
                    }

                    if (event.damager is Projectile && game.spectators.contains((event.damager as Projectile).shooter)) {
                        event.isCancelled = true
                        return
                    }

                    if (event.damager is Player && game.spectators.contains(event.damager as Player)) {
                        event.isCancelled = true
                        return
                    }

                    if (participant.fighting) {
                        event.isCancelled = false
                        return
                    }
                }

                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        val game = PotPvPRP.getInstance().gameHandler.getCurrentGame(event.player) ?: return
        val logic = game.logic as? LastManStandingGameEventLogic ?: return
        val participant = logic.get(event.player) ?: return

        if (!(participant.hasDied(event.player)) && game.event == LastManStandingGameEvent) {
            event.isCancelled = false
            event.itemDrop.remove()
        }
    }

    @EventHandler
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        if (event.whoClicked is Player && event.click == ClickType.DROP && event.inventory == event.whoClicked.inventory) {
            val player = event.whoClicked as Player
            val game = PotPvPRP.getInstance().gameHandler.getCurrentGame(player) ?: return
            val logic = game.logic as? LastManStandingGameEventLogic ?: return
            val participant = logic.get(player) ?: return

            if (!(participant.hasDied(player)) && game.event == LastManStandingGameEvent) {
                val item = event.currentItem

                if (item != null) {
                    if (item.amount > 1) {
                        item.amount = item.amount - 1
                    } else {
                        event.inventory.setItem(event.rawSlot, ItemStack(Material.AIR))
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val game = PotPvPRP.getInstance().gameHandler.getCurrentGame(event.player) ?: return
        val logic = game.logic as? LastManStandingGameEventLogic ?: return
        val participant = logic.get(event.player) ?: return

        participant.died(event.player)
        Bukkit.getPluginManager().callEvent(PlayerQuitGameEvent(event.player, game))
        logic.check()
    }

    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        val game = PotPvPRP.getInstance().gameHandler.getCurrentGame(event.entity) ?: return
        val logic = game.logic as? LastManStandingGameEventLogic ?: return
        val participant = logic.get(event.entity) ?: return

        var location = event.entity.location
        if (location.blockY < 0) {
            location = game.arena.team1Spawn
        }

        if (game.event == LastManStandingGameEvent) {
            for (item in event.drops.toMutableList()) {
                if (item != null && item.type != Material.POTION) {
                    event.drops.remove(item)
                }
            }
        }

        if (event.entity.killer != null) {
            event.entity.killer.health = event.entity.killer.maxHealth
            game.sendMessage("", event.entity.displayName + ChatColor.GRAY + " was killed by " + event.entity.killer.displayName + ChatColor.GRAY + ".", "")
        } else {
            game.sendMessage("", event.entity.displayName + ChatColor.GRAY + " died.", "")
        }

        participant.died(event.entity) // todo throw in spectator, play death animation
        object: BukkitRunnable() {
            override fun run() {
                game.spectators.add(event.entity)
                event.entity.spigot().respawn()
                event.entity.teleport(location)
                game.reset(event.entity)
                Bukkit.getPluginManager().callEvent(PlayerGameInteractionEvent(event.entity, game))
                logic.check()
            }
        }.runTaskLater(PotPvPRP.getInstance(), 2L)
    }

}