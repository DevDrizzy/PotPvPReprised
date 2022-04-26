package net.frozenorb.potpvp.events;

import net.frozenorb.potpvp.events.Game;
import net.frozenorb.potpvp.events.GameState;
import net.frozenorb.potpvp.events.bukkit.event.GameStateChangeEvent;
import net.frozenorb.potpvp.events.bukkit.event.PlayerGameInteractionEvent;
import net.frozenorb.potpvp.events.bukkit.event.PlayerJoinGameEvent;
import net.frozenorb.potpvp.events.bukkit.event.PlayerQuitGameEvent;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.events.menu.EventsMenu;
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
            /*if (PotPvPRP.getInstance().gameHandler.getCurrentGames().size() == 1) {
                Game game = PotPvPRP.getInstance().gameHandler.getCurrentGames().get(0);
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
                PotPvPRP.getInstance().getNameTagHandler().reloadPlayer(player);
                PotPvPRP.getInstance().getNameTagHandler().reloadOthersFor(player);
                VisibilityUtils.updateVisibility(player);
                PotPvPRP.getInstance().getLobbyHandler().returnToLobby(player);
            }
        }
    }

    @EventHandler
    public void onPlayerJoinGameEvent(PlayerJoinGameEvent event) {
        PotPvPRP.getInstance().getNameTagHandler().reloadPlayer(event.getPlayer());
        PotPvPRP.getInstance().getNameTagHandler().reloadOthersFor(event.getPlayer());
        for (Player player : event.getGame().getPlayers()) {
            VisibilityUtils.updateVisibility(player);
        }
    }

    @EventHandler
    public void onPlayerQuitGameEvent(PlayerQuitGameEvent event) {
        PotPvPRP.getInstance().getNameTagHandler().reloadPlayer(event.getPlayer());
        PotPvPRP.getInstance().getNameTagHandler().reloadOthersFor(event.getPlayer());
        PotPvPRP.getInstance().getLobbyHandler().returnToLobby(event.getPlayer());
    }

    @EventHandler
    public void onPlayerGameInteractionEvent(PlayerGameInteractionEvent event) {
        PotPvPRP.getInstance().getNameTagHandler().reloadPlayer(event.getPlayer());
        PotPvPRP.getInstance().getNameTagHandler().reloadOthersFor(event.getPlayer());
        VisibilityUtils.updateVisibility(event.getPlayer());
    }

}
