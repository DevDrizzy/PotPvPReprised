package net.frozenorb.potpvp.lobby;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.profile.follow.FollowHandler;
import net.frozenorb.potpvp.command.impl.silent.UnfollowCommand;
import net.frozenorb.potpvp.lobby.listener.LobbyGeneralListener;
import net.frozenorb.potpvp.lobby.listener.LobbyItemListener;
import net.frozenorb.potpvp.lobby.listener.LobbyParkourListener;
import net.frozenorb.potpvp.lobby.listener.LobbySpecModeListener;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.util.PatchedPlayerUtils;
import net.frozenorb.potpvp.util.VisibilityUtils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class LobbyHandler {

    /**
     * Stores players who are in "spectator mode", which gives them fly mode
     * and a different lobby hotbar. This setting is purely cosmetic, it doesn't
     * change what a player can/can't do (with the exception of not giving them
     * certain clickable items - but that's just a UX decision)
     */
    private final Set<UUID> spectatorMode = new HashSet<>();
    private final Map<UUID, Long> returnedToLobby = new HashMap<>();

    public LobbyHandler() {
        Bukkit.getPluginManager().registerEvents(new LobbyGeneralListener(this), PotPvPRP.getInstance());
        Bukkit.getPluginManager().registerEvents(new LobbyItemListener(this), PotPvPRP.getInstance());
        Bukkit.getPluginManager().registerEvents(new LobbySpecModeListener(), PotPvPRP.getInstance());
        Bukkit.getPluginManager().registerEvents(new LobbyParkourListener(), PotPvPRP.getInstance());
    }

    /**
     * Returns a player to the main lobby. This includes performing
     * the teleport, clearing their inventory, updating their nametag,
     * etc. etc.
     * @param player the player who is to be returned
     */
    public void returnToLobby(Player player) {
        returnToLobbySkipItemSlot(player);
        player.getInventory().setHeldItemSlot(0);
    }

    private void returnToLobbySkipItemSlot(Player player) {
        player.teleport(getLobbyLocation());

        PotPvPRP.getInstance().getNameTagHandler().reloadPlayer(player);
        PotPvPRP.getInstance().getNameTagHandler().reloadOthersFor(player);

        VisibilityUtils.updateVisibility(player);
        PatchedPlayerUtils.resetInventory(player, GameMode.SURVIVAL, true);
        InventoryUtils.resetInventoryDelayed(player);

        player.setGameMode(GameMode.SURVIVAL);

        returnedToLobby.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public long getLastLobbyTime(Player player) {
        return returnedToLobby.getOrDefault(player.getUniqueId(), 0L);
    }

    public boolean isInLobby(Player player) {
        return !PotPvPRP.getInstance().getMatchHandler().isPlayingOrSpectatingMatch(player);
    }

    public boolean isInSpectatorMode(Player player) {
        return spectatorMode.contains(player.getUniqueId());
    }

    public void setSpectatorMode(Player player, boolean mode) {
        boolean changed;

        if (mode) {
            changed = spectatorMode.add(player.getUniqueId());
        } else {
            FollowHandler followHandler = PotPvPRP.getInstance().getFollowHandler();
            followHandler.getFollowing(player).ifPresent(i -> new UnfollowCommand().unfollow(player));

            changed = spectatorMode.remove(player.getUniqueId());
        }

        if (changed) {
            InventoryUtils.resetInventoryNow(player);

            if (!mode) {
                returnToLobbySkipItemSlot(player);
            }
        }
    }

    public Location getLobbyLocation() {
        Location spawn = Bukkit.getWorlds().get(0).getSpawnLocation();
        spawn.add(0.5, 0, 0.5); // 'prettify' so players spawn in middle of block
        return spawn;
    }

}