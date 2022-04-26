package net.frozenorb.potpvp.events;

import net.frozenorb.potpvp.events.Game;
import net.frozenorb.potpvp.events.GameHandler;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.lobby.LobbyUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class EventTask extends BukkitRunnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            LobbyHandler handler = PotPvPRP.getInstance().getLobbyHandler();

            if (handler.isInLobby(player)) {
                List<Game> games = PotPvPRP.getInstance().getGameHandler().getCurrentGames();
                Game game = PotPvPRP.getInstance().getGameHandler().getCurrentGame(player);

                if (games.isEmpty()) {
                    if (player.getInventory().contains(Material.EMERALD)) {
                        player.getInventory().remove(Material.EMERALD);
                    }
                    continue;
                }

                if (game != null) continue;

                if (!player.getInventory().contains(EventItems.getEventItem()) && !PotPvPRP.getInstance().getPartyHandler().hasParty(player)) {
                    LobbyUtils.resetInventory(player);
                }
            } else {
                Game game = PotPvPRP.getInstance().getGameHandler().getCurrentGame(player);
                if (game != null && game.getPlayers().contains(player) && player.getInventory().contains(EventItems.getEventItem())) {
                    player.getInventory().remove(Material.EMERALD);
                }
            }

        }
    }
}
