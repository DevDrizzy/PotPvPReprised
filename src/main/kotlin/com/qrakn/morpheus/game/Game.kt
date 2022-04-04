package com.qrakn.morpheus.game

import com.qrakn.morpheus.game.bukkit.event.GameStateChangeEvent
import com.qrakn.morpheus.game.bukkit.event.PlayerJoinGameEvent
import com.qrakn.morpheus.game.event.GameEvent
import com.qrakn.morpheus.game.event.impl.lms.LastManStandingGameEventLogic
import com.qrakn.morpheus.game.util.team.GameTeamSizeParameter
import com.qrakn.morpheus.game.parameter.GameParameterOption
import net.frozenorb.potpvp.arena.Arena
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Game(val event: GameEvent, val host: Player, val parameters: List<GameParameterOption>) {

    var state = GameState.QUEUED
    var startingAt = 0L
    var players = HashSet<Player>()
    val logic = event.getLogic(this)
    val spectators = HashSet<Player>()
    lateinit var arena: Arena

    fun addSpectator(player: Player) {
        if (state == GameState.ENDED) {
            return
        }

        spectators.add(player)
        players.add(player)

        sendMessage(player.displayName + ChatColor.GRAY + " is now spectating.")

        reset(player)

        if (logic is LastManStandingGameEventLogic) {
            player.teleport(arena.team1Spawn)
        } else {
            player.teleport(arena.spectatorSpawn)
        }

        Bukkit.getPluginManager().callEvent(PlayerJoinGameEvent(player, this))
    }

    fun add(player: Player) {
        val other = GameQueue.getCurrentGame(player)

        if (other != null) {
            return
        }

        if (state != GameState.STARTING) {
            return
        }

        players.add(player)

        sendMessage(player.displayName + ChatColor.GRAY + " joined the event.")

        reset(player)

        Bukkit.getPluginManager().callEvent(PlayerJoinGameEvent(player, this))
    }

    private fun resetSpectator(player: Player) {
        player.inventory.clear()
        player.inventory.heldItemSlot = 0
        player.inventory.armorContents = null
        player.gameMode = GameMode.CREATIVE
        player.inventory.addItem(*event.getLobbyItems().toTypedArray())
        player.inventory.setItem(8, GameEvent.leaveItem)
        player.health = player.maxHealth
        player.foodLevel = 20

        for (effect in player.activePotionEffects) {
            player.removePotionEffect(effect.type)
        }

        player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0))

        player.updateInventory()
    }

    fun reset(player: Player) {

        if (spectators.contains(player)) {
            resetSpectator(player)
            return
        }

        player.teleport(arena.spectatorSpawn.clone().add(0.0, -1.0, 0.0))

        player.inventory.clear()
        player.inventory.heldItemSlot = 0
        player.inventory.armorContents = null
        player.gameMode = GameMode.SURVIVAL
        player.inventory.addItem(*event.getLobbyItems().toTypedArray())
        player.inventory.setItem(8, GameEvent.leaveItem)
        player.health = player.maxHealth
        player.foodLevel = 20

        for (effect in player.activePotionEffects) {
            player.removePotionEffect(effect.type)
        }

        player.updateInventory()
    }

    fun start() {
        if (!(event.canStart(this))) {
            end()
            return
        }

        arena.takeSnapshot()
        logic.start()

        Bukkit.getPluginManager().callEvent(GameStateChangeEvent(this, GameState.RUNNING))
    }

    fun end() {
        arena.restore()
        Bukkit.getPluginManager().callEvent(GameStateChangeEvent(this, GameState.ENDED))
    }

    fun getSecondSpawnLocations(): Array<Location> {
        if (getParameter(GameTeamSizeParameter.Duos.javaClass) != null) {
            val direction = arena.team2Spawn.direction
            return arrayOf(
                    arena.team2Spawn.clone().add(direction.clone().setX(-direction.z).setZ(direction.x)),
                    arena.team2Spawn.clone().add(direction.clone().setX(direction.z).setZ(-direction.x))
            )
        } else {
            return arrayOf(arena.team2Spawn)
        }
    }

    fun sendMessage(vararg message: String) {
        for (player in players) {
            player.sendMessage(message)
        }
    }

    fun getFirstSpawnLocations(): Array<Location> {
        if (getParameter(GameTeamSizeParameter.Duos.javaClass) != null) {
            val direction = arena.team1Spawn.direction
            return arrayOf(
                    arena.team1Spawn.clone().add(direction.clone().setX(-direction.z).setZ(direction.x)),
                    arena.team1Spawn.clone().add(direction.clone().setX(direction.z).setZ(-direction.x))
            )
        } else {
            return arrayOf(arena.team1Spawn)
        }
    }

    fun <T> getParameter(clazz: Class<T>): GameParameterOption? {
        for (parameter in parameters) {
            if (parameter.javaClass == clazz || clazz.isAssignableFrom(parameter.javaClass)) {
                return clazz.cast(parameter) as GameParameterOption
            }
        }
        return null
    }

    fun getMaxPlayers(): Int {
        if (logic is LastManStandingGameEventLogic) {
            if (getParameter(GameTeamSizeParameter.Duos.javaClass) != null) {
                return arena.eventSpawns.size * 2
            } else {
                return arena.eventSpawns.size
            }
        }
        return -1
    }

}