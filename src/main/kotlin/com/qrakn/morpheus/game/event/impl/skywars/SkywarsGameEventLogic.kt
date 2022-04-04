package com.qrakn.morpheus.game.event.impl.skywars

import com.qrakn.morpheus.game.Game
import com.qrakn.morpheus.game.event.impl.lms.LastManStandingGameEventLogic
import com.qrakn.morpheus.game.util.team.GameTeam
import com.qrakn.morpheus.game.util.team.GameTeamEventLogic
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