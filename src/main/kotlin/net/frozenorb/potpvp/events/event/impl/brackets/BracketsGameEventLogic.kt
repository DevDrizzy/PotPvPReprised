package net.frozenorb.potpvp.events.event.impl.brackets

import net.frozenorb.potpvp.events.Game
import net.frozenorb.potpvp.events.GameState
import net.frozenorb.potpvp.events.bukkit.event.PlayerGameInteractionEvent
import net.frozenorb.potpvp.events.util.GameEventCountdown
import net.frozenorb.potpvp.events.util.team.GameTeam
import net.frozenorb.potpvp.events.util.team.GameTeamEventLogic
import org.apache.commons.lang.StringUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

open class BracketsGameEventLogic(val game: Game) : GameTeamEventLogic(game) {

    override fun start() {
        super.start()

        next()
    }

    fun check() {
        val winner = getWinner() ?: return
        val loser = getLoser() ?: return

        winner.reset()
        winner.round += 1
        winner.fighting = false

        participants.remove(loser)

        for (player in winner.players) {
            game.reset(player)
        }

        for (player in loser.players) {
            game.addSpectator(player)
        }

        if (getNextParticipant(winner) == null) {
            game.end()
            broadcastWinner(winner)
        } else {
            game.sendMessage("", winner.getName() + ChatColor.YELLOW.toString() + " beat " + loser.getName() + ChatColor.YELLOW + "!", "")
            next()
        }
    }

    private fun broadcastWinner(winner: GameTeam) {
        for (player in Bukkit.getOnlinePlayers()) {
            player.sendMessage(arrayOf("",
                    ChatColor.GRAY.toString() + "███████",
                    ChatColor.GRAY.toString() + "█" + ChatColor.GOLD + "█████" + ChatColor.GRAY + "█" + " " + ChatColor.GOLD + "[${game.event.getName()} Event Winner]",
                    ChatColor.GRAY.toString() + "█" + ChatColor.GOLD + "█" + ChatColor.GRAY + "█████" + " ",
                    ChatColor.GRAY.toString() + "█" + ChatColor.GOLD + "████" + ChatColor.GRAY + "██" + " " + winner.getName() + ChatColor.GRAY + " has won the event!",
                    ChatColor.GRAY.toString() + "█" + ChatColor.GOLD + "█" + ChatColor.GRAY + "█████" + " " + ChatColor.GRAY + ChatColor.ITALIC + "Opponents defeated: " + (getRound()?.minus(1)),
                    ChatColor.GRAY.toString() + "█" + ChatColor.GOLD + "█████" + ChatColor.GRAY + "█" + " " + ChatColor.GRAY + ChatColor.ITALIC + "Event Type: (" + StringUtils.join(game.parameters.map { it.getDisplayName() }, ", ") + ")",
                    ChatColor.GRAY.toString() + "███████",
                    "")
            )
        }
    }

    private fun next() {
        val fighter = getNextParticipant(null)
        val opponent = getNextParticipant(fighter)

        if (fighter != opponent && fighter != null && opponent != null) {

            if (fighter.round != opponent.round) {
                fighter.round = maxOf(fighter.round, opponent.round)
                opponent.round = fighter.round
            }

            game.sendMessage("", ChatColor.YELLOW.toString() + "" + ChatColor.BOLD + "Next Matchup:", fighter.getName() + ChatColor.YELLOW.toString() + " vs. " + opponent.getName() + ChatColor.YELLOW + "!", "")

            fighter.starting = true
            opponent.starting = true

            GameEventCountdown(5,
                    object : BukkitRunnable() {
                        override fun run() {
                            fighter.starting = false
                            fighter.fighting = true

                            opponent.starting = false
                            opponent.fighting = true
                        }
                    }, fighter, opponent)

            val kit = game.getParameter(BracketsGameKitParameter.BracketsGameKitOption::class.java)

            fighter.players.forEachIndexed { index, player ->
                game.spectators.remove(player)
                player.inventory.clear()
                player.isSprinting = false
                player.updateInventory()
                player.velocity = Vector()
                player.teleport(game.getFirstSpawnLocations()[index])

                game.spectators.remove(player)
                Bukkit.getPluginManager().callEvent(PlayerGameInteractionEvent(player, game))

                if (kit != null && kit is BracketsGameKitParameter.BracketsGameKitOption) {
                    kit.apply(player)
                }
            }

            opponent.players.forEachIndexed { index, player ->
                game.spectators.remove(player)
                player.inventory.clear()
                player.isSprinting = false
                player.velocity = Vector()
                player.updateInventory()
                player.teleport(game.getSecondSpawnLocations()[index])

                game.spectators.remove(player)
                Bukkit.getPluginManager().callEvent(PlayerGameInteractionEvent(player, game))

                if (kit != null && kit is BracketsGameKitParameter.BracketsGameKitOption) {
                    kit.apply(player)
                }
            }

            return
        }

        game.end()
    }

    fun getRound(): Int? {
        return 1 + (getNextParticipant(null)?.round ?: 0)
    }

    fun getNextParticipant(exclude: GameTeam?): GameTeam? {
        var current: GameTeam? = null

        for (participant in participants) {
            if (participant != exclude) {
                if (current == null || participant.round < current.round) {
                    current = participant
                }
            }
        }

        return current
    }

    private fun getWinner(): GameTeam? {
        for (participant in participants) {
            if (participant.fighting && !participant.isFinished()) {
                return participant
            }
        }
        return null
    }

    private fun getLoser(): GameTeam? {
        for (participant in participants) {
            if (participant.fighting && participant.isFinished()) {
                return participant
            }
        }

        return null
    }

    fun getPlayersLeft(): Int {
        if (game.state == GameState.STARTING) return game.players.size

        var toReturn = 0

        for (participant in participants) {
            toReturn += participant.players.size
        }

        return toReturn
    }

}