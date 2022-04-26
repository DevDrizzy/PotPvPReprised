package net.frozenorb.potpvp.events

import net.frozenorb.potpvp.PotPvPRP
import net.frozenorb.potpvp.events.task.GameStartTask
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class GameHandler {

    private val runningGames = ArrayList<Game>()
    val games = LinkedList<Game>()

    init {
        object: BukkitRunnable() {
            override fun run() {
                check(PotPvPRP.getInstance())
            }
        }.runTaskTimer(PotPvPRP.getInstance(), 20L, 20L)
    }

    private fun check(plugin: JavaPlugin) {
        val game = games.peek()
        if (game != null) {
            if (game.state == GameState.QUEUED) {

                var count = 0
                var cancelled = false
                for (other in runningGames) {
                    if (other.state == GameState.STARTING || other.state == GameState.ENDED) {
                        cancelled = true
                        break
                    }

                    if (other.event == game.event) {
                        count++
                    }
                }

                if (count >= game.event.getMaxInstances()) {
                    cancelled = true
                }

                if (!(game.host.isOnline)) {
                    games.remove()
                    cancelled = true
                }

                if (!(cancelled)) {
                    games.remove()
                    runningGames.add(game)
                    GameStartTask(plugin, game)
                }
            }
        }

        val iterator = runningGames.iterator()
        while (iterator.hasNext()) {
            val runningGame = iterator.next()

            if (runningGame.state == GameState.ENDED) {
                iterator.remove()
                continue
            }

            var onlinePlayers = 0
            for (player in runningGame.players) {
                if (player.isOnline) onlinePlayers++
            }

            if (runningGame.state != GameState.STARTING && (runningGame.players.isEmpty() || onlinePlayers == 0)) {
                iterator.remove()
                game.end()
                continue
            }

        }

    }

    fun add(game: Game) {
        games.add(game)
    }

    fun size(): Int {
        return games.size
    }

    fun getCurrentGames(): List<Game> {
        return runningGames
    }

    fun getCurrentGame(player: Player): Game? {
        runningGames.filter { it.players.contains(player) }.find { return it }

        return null
    }

}