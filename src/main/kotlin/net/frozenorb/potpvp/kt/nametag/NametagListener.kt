package net.frozenorb.potpvp.kt.nametag

import net.frozenorb.potpvp.PotPvPRP
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.bukkit.event.player.PlayerJoinEvent

internal class NametagListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.setMetadata("potpvp-LoggedIn", FixedMetadataValue(PotPvPRP.getInstance(), true) as MetadataValue)
        PotPvPRP.getInstance().nameTagHandler.initiatePlayer(event.player)
        PotPvPRP.getInstance().nameTagHandler.reloadPlayer(event.player)
        PotPvPRP.getInstance().nameTagHandler.reloadOthersFor(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.player.removeMetadata("potpvp-LoggedIn", PotPvPRP.getInstance())
        PotPvPRP.getInstance().nameTagHandler.teamMap.remove(event.player.name)
    }
}