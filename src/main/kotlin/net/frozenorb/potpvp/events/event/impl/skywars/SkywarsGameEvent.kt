package net.frozenorb.potpvp.events.event.impl.skywars

import net.frozenorb.potpvp.events.Game
import net.frozenorb.potpvp.events.GameState
import net.frozenorb.potpvp.events.event.GameEvent
import net.frozenorb.potpvp.events.event.GameEventLogic
import net.frozenorb.potpvp.events.event.impl.brackets.BracketsGameEventLogic
import net.frozenorb.potpvp.events.event.impl.sumo.SumoGameEvent
import net.frozenorb.potpvp.events.event.impl.sumo.SumoGameEventLogic
import net.frozenorb.potpvp.events.util.team.GameTeamSizeParameter
import net.frozenorb.potpvp.events.parameter.GameParameter
import net.frozenorb.potpvp.events.util.team.GameTeamEventLogic
import net.frozenorb.potpvp.PotPvPLang
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

object SkywarsGameEvent : GameEvent {

    private const val NAME = "Skywars"
    private const val PERMISSION = "com.qrakn.morpheus.host.skywars"
    private const val DESCRIPTION = "Compete against other players to be the last man standing (on an island)."

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
        return ItemStack(Material.GRASS)
    }

    override fun canStart(game: Game): Boolean {
        if (game.getParameter(GameTeamSizeParameter.Duos.javaClass) != null) {
            return game.players.size >= 4
        }

        return game.players.size >= 2
    }

    override fun getLogic(game: Game): GameEventLogic {
        return SkywarsGameEventLogic(game)
    }

    override fun getScoreboardScores(player: Player, game: Game): List<String> {
        val toReturn = ArrayList<String>()
        val logic = game.logic as SkywarsGameEventLogic
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
        val logic = game.logic as? GameTeamEventLogic ?: return ""

        if (logic.invites[player.uniqueId] == viewer.uniqueId || logic.invites[viewer.uniqueId] == player.uniqueId) {
            return ChatColor.YELLOW.toString()
        }

        val participant = logic.get(player)
        if (participant != null && participant.players.contains(viewer)) {
            return ChatColor.GREEN.toString()
        }

        if (participant == null && game.state != GameState.STARTING) {
            return ChatColor.GRAY.toString()
        }

        return ChatColor.RED.toString()
    }

    override fun getListeners(): List<Listener> {
        return arrayListOf(SkywarsGameEventListeners())
    }

    override fun getParameters(): List<GameParameter> {
        return listOf(GameTeamSizeParameter, SkywarsGameEventTypeParameter)
    }

    override fun getMaxInstances(): Int {
        return 2
    }

    override fun getLobbyItems(): List<ItemStack> {
        return listOf()
    }

}