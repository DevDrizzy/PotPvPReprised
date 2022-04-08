package net.frozenorb.potpvp.morpheus;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameState;
import com.qrakn.morpheus.game.bukkit.event.GameStateChangeEvent;
import com.qrakn.morpheus.game.bukkit.event.PlayerGameInteractionEvent;
import com.qrakn.morpheus.game.bukkit.event.PlayerJoinGameEvent;
import com.qrakn.morpheus.game.bukkit.event.PlayerQuitGameEvent;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.morpheus.menu.EventsMenu;
import net.frozenorb.potpvp.util.VisibilityUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventListeners implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && event.getItem().equals(EventItems.getEventItem()) && PotPvPRP.getInstance().getLobbyHandler().isInLobby(player)) {
            /*if (GameQueue.INSTANCE.getCurrentGames().size() == 1) {
                Game game = GameQueue.INSTANCE.getCurrentGames().get(0);
                if (game.getState() == GameState.STARTING) {
                    if (game.getMaxPlayers() > 0 && game.getPlayers().size() >= game.getMaxPlayers()) {
                        player.sendMessage(ChatColor.RED + "This event is currently full! Sorry!");
                        return;
                    }
                    game.add(player);
                } else {
                    game.addSpectator(player);
                }
                return;
            }*/
            new EventsMenu().openMenu(player);
        }

    }

    @EventHandler
    public void onGameStateChangeEvent(GameStateChangeEvent event) {
        Game game = event.getGame();

        if (event.getTo() == GameState.ENDED) {
            PotPvPRP.getInstance().getArenaHandler().releaseArena(game.getArena());
            for (Player player : game.getPlayers()) {
                PotPvPRP.getInstance().getNameTagEngine().reloadPlayer(player);
                PotPvPRP.getInstance().getNameTagEngine().reloadOthersFor(player);
                VisibilityUtils.updateVisibility(player);
                PotPvPRP.getInstance().getLobbyHandler().returnToLobby(player);
            }
        }
    }

    @EventHandler
    public void onPlayerJoinGameEvent(PlayerJoinGameEvent event) {
        PotPvPRP.getInstance().getNameTagEngine().reloadPlayer(event.getPlayer());
        PotPvPRP.getInstance().getNameTagEngine().reloadOthersFor(event.getPlayer());
        for (Player player : event.getGame().getPlayers()) {
            VisibilityUtils.updateVisibility(player);
        }
    }

    @EventHandler
    public void onPlayerQuitGameEvent(PlayerQuitGameEvent event) {
        PotPvPRP.getInstance().getNameTagEngine().reloadPlayer(event.getPlayer());
        PotPvPRP.getInstance().getNameTagEngine().reloadOthersFor(event.getPlayer());
        PotPvPRP.getInstance().getLobbyHandler().returnToLobby(event.getPlayer());
    }

    @EventHandler
    public void onPlayerGameInteractionEvent(PlayerGameInteractionEvent event) {
        PotPvPRP.getInstance().getNameTagEngine().reloadPlayer(event.getPlayer());
        PotPvPRP.getInstance().getNameTagEngine().reloadOthersFor(event.getPlayer());
        VisibilityUtils.updateVisibility(event.getPlayer());
    }

}
