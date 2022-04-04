package com.qrakn.morpheus.game.util.team

import net.frozenorb.potpvp.kt.util.PlayerUtils
import org.apache.commons.lang.StringUtils
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class GameTeam(vararg var players: Player) {
    private var died = ArrayList<Player>()
    var round = 0 // todo remove/rename?
    var kills = 0 // todo remove/rename?
    var fighting = false
    var starting = false

    fun isFinished(): Boolean {
        return died.size == players.size
    }

    fun died(player: Player) {
        if (!(died.contains(player))) {
            died.add(player)
        }
    }

    fun hasDied(player: Player): Boolean {
        return died.contains(player)
    }

    fun reset() {
        died.clear()
        fighting = false
        starting = false
    }

    fun getName(): String {
        return StringUtils.join(players.map {it.displayName}.toTypedArray(), ChatColor.YELLOW.toString() + " + ")
    }

    fun getPing(): Int {
        return PlayerUtils.getPing(players[0])
    }

}