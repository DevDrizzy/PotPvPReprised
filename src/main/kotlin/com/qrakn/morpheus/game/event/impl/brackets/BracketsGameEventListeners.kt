package com.qrakn.morpheus.game.event.impl.brackets

import com.qrakn.morpheus.game.GameQueue
import com.qrakn.morpheus.game.GameState
import com.qrakn.morpheus.game.bukkit.event.PlayerGameInteractionEvent
import com.qrakn.morpheus.game.bukkit.event.PlayerQuitGameEvent
import com.qrakn.morpheus.game.util.team.GameTeam
import com.qrakn.morpheus.game.event.impl.sumo.SumoGameEventLogic
import com.qrakn.morpheus.game.util.team.GameTeamEventLogic
import com.qrakn.morpheus.game.util.team.GameTeamSizeParameter
import net.frozenorb.potpvp.PotPvPSI
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
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class BracketsGameEventListeners : Listener {

    @EventHandler
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (event.from.blockX != event.to.blockX || event.from.blockY != event.to.blockY || event.from.blockZ != event.to.blockZ) {
            val game = GameQueue.getCurrentGame(event.player) ?: return
            val logic = game.logic as? BracketsGameEventLogic ?: return
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
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            val game = GameQueue.getCurrentGame(player) ?: return
            if (game.logic !is GameTeamEventLogic) return
            val participant = game.logic.get(player)

            if (game.spectators.contains(player)) {
                event.isCancelled = true
                return
            }

            if (event.damager is Player && game.spectators.contains(event.damager as Player)) {
                event.isCancelled = true
                return
            }

            if (game.players.contains(player)) {
                var opponent: Player? = null

                if (event.damager is Player) {
                    opponent = event.damager as Player
                }

                if (event.damager is Projectile && (event.damager as Projectile).shooter is Player) {
                    opponent = (event.damager as Projectile).shooter as Player
                }

                if (participant != null && opponent != null) {
                    if (participant.players.contains(opponent)) {
                        event.isCancelled = true
                        return
                    }

                    if (participant.fighting) {
                        val opponentParticipant = game.logic.get(opponent)
                        if (opponentParticipant != null && opponentParticipant.fighting) {
                            event.isCancelled = false
                            return
                        }
                    }
                }

                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerDamageEvent(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            val game = GameQueue.getCurrentGame(player) ?: return
            val logic = game.logic as? BracketsGameEventLogic ?: return

            if (game.players.contains(player) && game.state != GameState.STARTING) {
                val participant = logic.get(player)

                if (participant != null) {
                    if (participant.fighting && !participant.hasDied(player)) {
                        event.isCancelled = false
                        return
                    }
                }

                event.isCancelled = true
            }
        }
    }

    @EventHandler // todo move out of this class
    fun onPlayerInteractEntityEvent(event: PlayerInteractEntityEvent) {
        val player = event.player
        if (event.rightClicked is Player && (player.itemInHand == null || player.itemInHand.type == Material.AIR)) {
            val clicked = event.rightClicked as Player
            val game = GameQueue.getCurrentGame(player) ?: return
            val logic = game.logic
            game.getParameter(GameTeamSizeParameter.Duos.javaClass) ?: return //todo add support for other bracket sizes
            if (logic !is GameTeamEventLogic) return

            if (game.players.contains(player) && game.players.contains(clicked) && game.state == GameState.STARTING) {

                if (logic.contains(player)) {
                    player.sendMessage(ChatColor.RED.toString() + "You're already in a team.")
                    return
                }

                if (logic.contains(clicked)) {
                    player.sendMessage(ChatColor.RED.toString() + clicked.name + " is already in a team!")
                    return
                }

                if (logic.invites[player.uniqueId] == clicked.uniqueId) {
                    player.sendMessage(ChatColor.RED.toString() + "You've already sent " + clicked.name + " a team request.")
                    return
                }

                if (logic.invites[clicked.uniqueId] == player.uniqueId) {
                    player.sendMessage(ChatColor.GREEN.toString() + "You joined " + clicked.name + "'s team.")
                    clicked.sendMessage(ChatColor.GREEN.toString() + player.name + " joined your team.")

                    logic.invites.remove(player.uniqueId)
                    logic.invites.remove(clicked.uniqueId)

                    Bukkit.getPluginManager().callEvent(PlayerGameInteractionEvent(player, game))
                    Bukkit.getPluginManager().callEvent(PlayerGameInteractionEvent(clicked, game))

                    logic.participants.add(GameTeam(player, clicked))
                    return
                }

                logic.invites[player.uniqueId] = clicked.uniqueId
                clicked.sendMessage("")
                clicked.sendMessage(player.displayName + ChatColor.YELLOW + " would like to team with you.")
                clicked.sendMessage(ChatColor.YELLOW.toString() + "Right-click them to accept.")
                clicked.sendMessage("")
                player.sendMessage(ChatColor.YELLOW.toString() + "You sent a team request to " + clicked.displayName + ChatColor.YELLOW + ".")

                Bukkit.getPluginManager().callEvent(PlayerGameInteractionEvent(player, game))
                Bukkit.getPluginManager().callEvent(PlayerGameInteractionEvent(clicked, game))
            }

        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val game = GameQueue.getCurrentGame(event.player) ?: return
        val logic = game.logic as? BracketsGameEventLogic ?: return
        val participant = logic.get(event.player) ?: return

        if (participant.fighting || participant.starting) {
            participant.died(event.player)

            logic.check()
        } else {
            if (participant.players.size == 1 || game.state == GameState.STARTING) {
                logic.participants.remove(participant)
            } else {
                val newPlayers = participant.players.toMutableList()
                newPlayers.remove(event.player)
                participant.players = newPlayers.toTypedArray()
            }
        }
    }

    @EventHandler
    fun onPlayerQuitGameEvent(event: PlayerQuitGameEvent) {
        val game = GameQueue.getCurrentGame(event.player) ?: return
        val logic = game.logic as? BracketsGameEventLogic ?: return
        val participant = logic.get(event.player) ?: return

        if (participant.fighting || participant.starting) {
            participant.died(event.player)

            logic.check()
        } else {
            if (participant.players.size == 1 || game.state == GameState.STARTING) {
                logic.participants.remove(participant)
            } else {
                val newPlayers = participant.players.toMutableList()
                newPlayers.remove(event.player)
                participant.players = newPlayers.toTypedArray()
            }
        }
    }

    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        val game = GameQueue.getCurrentGame(event.entity) ?: return
        val logic = game.logic as? SumoGameEventLogic ?: return
        val participant = logic.get(event.entity) ?: return

        event.drops.clear()

        if (participant.fighting) {
            participant.died(event.entity)

            if (participant.isFinished()) {
                event.entity.health = event.entity.maxHealth
                logic.check()
            } else {
                object: BukkitRunnable() {
                    override fun run() {
                        event.entity.spigot().respawn()
                        event.entity.teleport(game.arena.spectatorSpawn)
                        Bukkit.getPluginManager().callEvent(PlayerGameInteractionEvent(event.entity, game))
                    }
                }.runTaskLater(PotPvPSI.getInstance(), 2L)
            }
        }
    }

    @EventHandler
    fun onFoodLevelChangeEvent(event: FoodLevelChangeEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            val game = GameQueue.getCurrentGame(player) ?: return
            val logic = game.logic as? BracketsGameEventLogic ?: return
            var participant = logic.get(player)?: return

            if (!(participant.fighting)) {
                event.foodLevel = 20
            }
        }
    }

}