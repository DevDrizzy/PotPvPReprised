package net.frozenorb.potpvp.kt.visibility

import net.frozenorb.potpvp.PotPvPRP
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

class VisibilityEngine {

    private val handlers = LinkedHashMap<String, VisibilityHandler>()
    private val overrideHandlers = LinkedHashMap<String, OverrideHandler>()

    fun load() {
        PotPvPRP.getInstance().server.pluginManager.registerEvents(VisibilityListener(), PotPvPRP.getInstance())
    }

    fun registerHandler(identifier: String, handler: VisibilityHandler) {
        handlers[identifier] = handler
    }

    fun registerOverride(identifier: String, handler: OverrideHandler) {
        overrideHandlers[identifier] = handler
    }

    fun update(player: Player) {
        if (handlers.isEmpty() && overrideHandlers.isEmpty()) {
            return
        }
        updateAllTo(player)
        updateToAll(player)
    }

    fun updateAllTo(viewer: Player) {
        for (target in Bukkit.getOnlinePlayers()) {
            if (!shouldSee(target, viewer)) {
                viewer.hidePlayer(target)
            } else {
                viewer.showPlayer(target)
            }
        }
    }

    fun updateToAll(target: Player) {
        for (viewer in Bukkit.getOnlinePlayers()) {
            if (!shouldSee(target, viewer)) {
                viewer.hidePlayer(target)
            } else {
                viewer.showPlayer(target)
            }
        }
    }

    fun treatAsOnline(target: Player, viewer: Player): Boolean {
        return viewer.canSee(target) || !target.hasMetadata("invisible") || viewer.hasPermission("stark.staff")
    }

    private fun shouldSee(target: Player, viewer: Player): Boolean {
        for (handler in overrideHandlers.values) {
            if (handler.getAction(target, viewer) === OverrideAction.SHOW) {
                return true
            }
        }

        for (handler in handlers.values) {
            if (handler.getAction(target, viewer) === VisibilityAction.HIDE) {
                return false
            }
        }

        return true
    }

    fun getDebugInfo(target: Player, viewer: Player): List<String> {
        val debug = ArrayList<String>()
        var canSee: Boolean? = null

        for ((key, handler) in overrideHandlers) {
            val action = handler.getAction(target, viewer)
            var color = ChatColor.GRAY

            if (action === OverrideAction.SHOW && canSee == null) {
                canSee = true
                color = ChatColor.GREEN
            }

            debug.add(color.toString() + "Overriding Handler: $key: $action")
        }

        for ((key, handler) in handlers) {
            val action = handler.getAction(target, viewer)
            var color = ChatColor.GRAY

            if (action === VisibilityAction.HIDE && canSee == null) {
                canSee = false
                color = ChatColor.GREEN
            }

            debug.add(color.toString() + "Normal Handler: $key: $action")
        }

        if (canSee == null) {
            canSee = true
        }

        debug.add(ChatColor.AQUA.toString() + "Result: " + viewer.name + " " + (if (canSee) "can" else "cannot") + " see " + target.name)

        return debug
    }

}
