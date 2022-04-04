package net.frozenorb.potpvp.kt.protocol

import net.frozenorb.potpvp.kt.protocol.event.ServerLaggedOutEvent
import net.frozenorb.potpvp.kt.util.PlayerUtils
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class LagCheck : BukkitRunnable() {

    override fun run() {
        val players = Bukkit.getOnlinePlayers()
        if (players.size >= 100) {
            var playersLagging = 0
            for (player in players) {
                if (PlayerUtils.isLagging(player)) {
                    ++playersLagging
                }
            }

            val percentage = playersLagging * 100 / players.size
            if (Math.abs(percentage) >= 30.0) {
                Bukkit.getPluginManager().callEvent(ServerLaggedOutEvent(PingAdapter.averagePing()))
            }
        }
    }

}