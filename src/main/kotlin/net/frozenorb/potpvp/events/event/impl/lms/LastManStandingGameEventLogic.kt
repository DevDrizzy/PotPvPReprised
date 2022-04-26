package net.frozenorb.potpvp.events.event.impl.lms

import net.frozenorb.potpvp.events.Game
import net.frozenorb.potpvp.events.GameState
import net.frozenorb.potpvp.events.util.GameEventCountdown
import net.frozenorb.potpvp.events.util.team.GameTeam
import net.frozenorb.potpvp.events.util.team.GameTeamEventLogic
import org.apache.commons.lang.StringUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Statistic
import org.bukkit.scheduler.BukkitRunnable

open class LastManStandingGameEventLogic(val game: Game) : GameTeamEventLogic(game) {

    override fun start() {
        super.start()

        participants.forEachIndexed { index, team ->
            team.starting = true
            for (player in team.players) {
                player.inventory.clear()
                player.setStatistic(Statistic.PLAYER_KILLS, 0)
                player.teleport(game.arena.eventSpawns[index])
            }
        }

        val kit = game.getParameter(LastManStandingGameKitParameter.LastManStandingGameKitOption::class.java)
        GameEventCountdown(5,
                object : BukkitRunnable() {
                    override fun run() {
                        for (participant in participants) {
                            participant.starting = false
                            participant.fighting = true

                            if (kit != null && kit is LastManStandingGameKitParameter.LastManStandingGameKitOption) {
                                participant.players.forEach { kit.apply(it) }
                            }
                        }
                    }
                }, *participants.toTypedArray())
    }

    fun check() {
        val alive = ArrayList<GameTeam>()

        for (team in participants) {
            if (!team.isFinished()) {
                alive.add(team)
            }
        }

        if (alive.size == 1) {
            val winner = alive[0]
            broadcastWinner(winner)
            end()
        } else if (alive.size == 0) {
            end()
        }
    }

    open fun end() {
        game.end()

        for (player in game.players) {
            player.setStatistic(Statistic.PLAYER_KILLS, 0)
        }
    }

    fun getPlayersLeft(): Int {
        if (game.state == GameState.STARTING) return game.players.size

        var toReturn = 0

        for (participant in participants) {
            for (player in participant.players) {
                if (!(participant.hasDied(player))) {
                    toReturn ++
                }
            }
        }

        return toReturn
    }

    open fun broadcastWinner(winner: GameTeam) {
        for (player in Bukkit.getOnlinePlayers()) {
            player.sendMessage(arrayOf("",
                    ChatColor.GRAY.toString() + "███████",
                    ChatColor.GRAY.toString() + "█" + ChatColor.GOLD + "█████" + ChatColor.GRAY + "█" + " " + ChatColor.GOLD + "[${game.event.getName()} Event Winner]",
                    ChatColor.GRAY.toString() + "█" + ChatColor.GOLD + "█" + ChatColor.GRAY + "█████" + " ",
                    ChatColor.GRAY.toString() + "█" + ChatColor.GOLD + "████" + ChatColor.GRAY + "██" + " " + winner.getName() + ChatColor.GRAY + " has won the event!",
                    ChatColor.GRAY.toString() + "█" + ChatColor.GOLD + "█" + ChatColor.GRAY + "█████" + " ",
                    ChatColor.GRAY.toString() + "█" + ChatColor.GOLD + "█████" + ChatColor.GRAY + "█" + " " + ChatColor.GRAY + ChatColor.ITALIC + "Event Type: (" + StringUtils.join(game.parameters.map { it.getDisplayName() }, ", ") + ")",
                    ChatColor.GRAY.toString() + "███████",
                    "")
            )
        }
    }

}