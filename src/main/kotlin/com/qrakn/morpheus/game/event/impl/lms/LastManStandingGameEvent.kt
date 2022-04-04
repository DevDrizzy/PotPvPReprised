package com.qrakn.morpheus.game.event.impl.lms

import com.qrakn.morpheus.game.Game
import com.qrakn.morpheus.game.GameState
import com.qrakn.morpheus.game.event.GameEvent
import com.qrakn.morpheus.game.event.GameEventLogic
import com.qrakn.morpheus.game.event.impl.skywars.SkywarsGameEvent
import com.qrakn.morpheus.game.event.impl.skywars.SkywarsGameEventLogic
import com.qrakn.morpheus.game.util.team.GameTeamSizeParameter
import com.qrakn.morpheus.game.parameter.GameParameter
import net.frozenorb.potpvp.PotPvPLang
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

object LastManStandingGameEvent : GameEvent {

    private const val NAME = "LMS"
    private const val PERMISSION = "com.qrakn.morpheus.host.lms"
    private const val DESCRIPTION = "Compete against other players to be the last man standing."

    override fun getName(): String {
        return NAME
    }

    override fun getPermission(): String {
        return PERMISSION
    }

    override fun getDescription(): String {
        return DESCRIPTION
    }

    override fun getIcon(): ItemStack {
        return ItemStack(Material.TNT)
    }

    override fun canStart(game: Game): Boolean {
        if (game.getParameter(GameTeamSizeParameter.Duos.javaClass) != null) {
            return game.players.size >= 4
        }

        return game.players.size >= 2
    }

    override fun getLogic(game: Game): GameEventLogic {
        return LastManStandingGameEventLogic(game)
    }

    override fun getScoreboardScores(player: Player, game: Game): List<String> {
        val toReturn = ArrayList<String>()
        val logic = game.logic as LastManStandingGameEventLogic
        var name = NAME

        if (game.getParameter(GameTeamSizeParameter.Duos.javaClass) != null) {
            name = "2v2 $name"
        }

        toReturn.add("&cEvent &7($name)")
        // todo fix max players for when game started xd
        toReturn.add("&6 ${PotPvPLang.LEFT_ARROW_NAKED} &fPlayers: &7${logic.getPlayersLeft()}/${game.getMaxPlayers()}")

        if (game.state == GameState.RUNNING) {
            //toReturn.add("&6 ${PotPvPLang.LEFT_ARROW_NAKED} &fKills: &7${player.getStatistic(Statistic.PLAYER_KILLS)}")
        }

        return toReturn
    }

    override fun getNameTag(game: Game, player: Player, viewer: Player): String {
        return ChatColor.RED.toString()
    }

    override fun getListeners(): List<Listener> {
        return arrayListOf(LastManStandingGameEventListeners())
    }

    override fun getParameters(): List<GameParameter> {
        return listOf(GameTeamSizeParameter, LastManStandingGameKitParameter)
    }

    override fun getMaxInstances(): Int {
        return 5
    }

    override fun getLobbyItems(): List<ItemStack> {
        return listOf()
    }

}