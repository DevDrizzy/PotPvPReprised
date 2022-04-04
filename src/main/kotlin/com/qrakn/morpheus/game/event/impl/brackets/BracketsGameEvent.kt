package com.qrakn.morpheus.game.event.impl.brackets

import com.qrakn.morpheus.game.Game
import com.qrakn.morpheus.game.GameState
import com.qrakn.morpheus.game.event.GameEvent
import com.qrakn.morpheus.game.event.GameEventLogic
import com.qrakn.morpheus.game.event.impl.sumo.SumoGameEventLogic
import com.qrakn.morpheus.game.util.team.GameTeamSizeParameter
import com.qrakn.morpheus.game.parameter.GameParameter
import net.frozenorb.potpvp.PotPvPLang
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

object BracketsGameEvent : GameEvent {

    private const val NAME = "Brackets"
    private const val PERMISSION = "com.qrakn.morpheus.host.brackets"
    private const val DESCRIPTION = "Compete against other players in brackets."

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
        return ItemStack(Material.IRON_SWORD)
    }

    override fun canStart(game: Game): Boolean {
        if (game.getParameter(GameTeamSizeParameter.Duos.javaClass) != null) {
            return game.players.size >= 4
        }

        return game.players.size >= 2
    }

    override fun getLogic(game: Game): GameEventLogic {
        return SumoGameEventLogic(game)
    }

    override fun getScoreboardScores(player: Player, game: Game): List<String> {
        val toReturn = ArrayList<String>()
        val logic = game.logic as SumoGameEventLogic
        var name = NAME

        if (game.getParameter(GameTeamSizeParameter.Duos.javaClass) != null) {
            name = "2v2 $name"
        }

        toReturn.add("&cEvent &7($name)")
        toReturn.add("&6 ${PotPvPLang.LEFT_ARROW_NAKED} &fPlayers: &7${logic.getPlayersLeft()}/${game.players.size}")

        if (game.state == GameState.RUNNING) {
            toReturn.add("&6 ${PotPvPLang.LEFT_ARROW_NAKED} &fRound: &7${logic.getRound()}")
            if (game.getParameter(GameTeamSizeParameter.Duos.javaClass) == null) {
                val fighter = logic.getNextParticipant(null)
                val opponent = logic.getNextParticipant(fighter)

                if (opponent != null && fighter != null) {
                    toReturn.add("&a&r&7&m--------------------")
                    toReturn.add("${fighter.getName()}&7 vs ${opponent.getName()}")
                }
            }
        }

        return toReturn
    }

    override fun getNameTag(game: Game, player: Player, viewer: Player): String {
        val logic = game.logic as? BracketsGameEventLogic ?: return ""

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
        return arrayListOf(BracketsGameEventListeners())
    }

    override fun getParameters(): List<GameParameter> {
        return listOf(GameTeamSizeParameter, BracketsGameKitParameter)
    }

    override fun getLobbyItems(): List<ItemStack> {
        return listOf()
    }

}