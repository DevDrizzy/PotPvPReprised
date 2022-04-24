package net.frozenorb.potpvp.util.nametag.listener;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.util.packet.ScoreboardTeamPacketMod;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.lang.reflect.Field;
import java.util.Collections;

public class NameTagListener implements Listener {

    private final PotPvPRP plugin = PotPvPRP.getInstance();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setMetadata("potpvp-LoggedIn", new FixedMetadataValue(plugin, true));

        plugin.getNameTagHandler().initiatePlayer(event.getPlayer());
        plugin.getNameTagHandler().reloadPlayer(event.getPlayer());
        plugin.getNameTagHandler().reloadOthersFor(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().removeMetadata("potpvp-LoggedIn", plugin);
        plugin.getNameTagHandler().getTeamMap().remove(event.getPlayer().getName());
    }

}