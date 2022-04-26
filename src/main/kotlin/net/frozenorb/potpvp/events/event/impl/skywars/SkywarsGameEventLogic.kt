package net.frozenorb.potpvp.events.event.impl.skywars

import net.frozenorb.potpvp.events.Game
import net.frozenorb.potpvp.events.event.impl.lms.LastManStandingGameEventLogic
import net.frozenorb.potpvp.events.util.team.GameTeam
import net.frozenorb.potpvp.events.util.team.GameTeamEventLogic
import org.apache.commons.lang.StringUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.material.MaterialData
import org.bukkit.scheduler.BukkitRunnable

class SkywarsGameEventLogic(game: Game) : LastManStandingGameEventLogic(game) {

    val chests = ArrayList<Location>()

}