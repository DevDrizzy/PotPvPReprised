package net.frozenorb.potpvp.kt.visibility

import net.frozenorb.potpvp.PotPvPSI
import org.apache.commons.lang.StringUtils
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatTabCompleteEvent
import org.bukkit.event.player.PlayerJoinEvent

class VisibilityListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        PotPvPSI.getInstance().visibilityEngine.update(event.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onTabComplete(event: PlayerChatTabCompleteEvent) {
        val token = event.lastToken
        val completions = event.tabCompletions
        completions.clear()

        for (target in Bukkit.getOnlinePlayers()) {
            if (!PotPvPSI.getInstance().visibilityEngine.treatAsOnline(target, event.player)) {
                continue
            }

            if (!StringUtils.startsWithIgnoreCase(target.name, token)) {
                continue
            }

            completions.add(target.name)
        }
    }

}