package net.frozenorb.potpvp.match.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.adapter.nametag.NameTagAdapter;
import net.frozenorb.potpvp.profile.setting.Setting;
import net.frozenorb.potpvp.profile.setting.SettingHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class MatchDeathMessageListener implements Listener {

    private static final String NO_KILLER_MESSAGE = ChatColor.translateAlternateColorCodes('&', "%s&7 died.");
    private static final String KILLED_BY_OTHER_MESSAGE = ChatColor.translateAlternateColorCodes('&', "%s&7 killed %s&7.");

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        SettingHandler settingHandler = PotPvPRP.getInstance().getSettingHandler();
        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getEntity());

        if (match == null) {
            return;
        }

        Player killed = event.getEntity();
        Player killer = killed.getKiller();
        PacketContainer lightningPacket = createLightningPacket(killed.getLocation());

        float thunderSoundPitch = 0.8F + ThreadLocalRandom.current().nextFloat() * 0.2F;
        float explodeSoundPitch = 0.5F + ThreadLocalRandom.current().nextFloat() * 0.2F;

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            UUID onlinePlayerUuid = onlinePlayer.getUniqueId();

            // if this player has no relation to the match skip
            if (match.getTeam(onlinePlayerUuid) == null && !match.isSpectator(onlinePlayerUuid)) {
                continue;
            }

            ChatColor killedNameColor = NameTagAdapter.getNameColor(killed, onlinePlayer);
            String killedFormattedName = killedNameColor + killed.getName();

            // if the killer died before the player did we just pretend they weren't
            // involved (their name would show up as a spectator, which would be confusing
            // for players)
            if (killer == null || match.isSpectator(killer.getUniqueId())) {
                onlinePlayer.sendMessage(String.format(NO_KILLER_MESSAGE, killedFormattedName));
            } else {
                ChatColor killerNameColor = NameTagAdapter.getNameColor(killer, onlinePlayer);
                String killerFormattedName = killerNameColor + killer.getName();

                onlinePlayer.sendMessage(String.format(KILLED_BY_OTHER_MESSAGE, killerFormattedName, killedFormattedName));
            }

            if (settingHandler.getSetting(onlinePlayer, Setting.VIEW_OTHERS_LIGHTNING)) {
                onlinePlayer.playSound(killed.getLocation(), Sound.AMBIENCE_THUNDER, 10000F, thunderSoundPitch);
                onlinePlayer.playSound(killed.getLocation(), Sound.EXPLODE, 2.0F, explodeSoundPitch);

                sendLightningPacket(onlinePlayer, lightningPacket);
            }
        }
    }

    private PacketContainer createLightningPacket(Location location) {
        PacketContainer lightningPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_WEATHER);

        lightningPacket.getModifier().writeDefaults();
        lightningPacket.getIntegers().write(0, 128); // entity id of 128
        lightningPacket.getIntegers().write(4, 1); // type of lightning (1)
        lightningPacket.getIntegers().write(1, (int) (location.getX() * 32.0D)); // x
        lightningPacket.getIntegers().write(2, (int) (location.getY() * 32.0D)); // y
        lightningPacket.getIntegers().write(3, (int) (location.getZ() * 32.0D)); // z

        return lightningPacket;
    }

    private void sendLightningPacket(Player target, PacketContainer packet) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(target, packet);
    }

}