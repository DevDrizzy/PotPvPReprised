package net.frozenorb.potpvp.util.scoreboard.api;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import net.frozenorb.potpvp.util.scoreboard.api.events.AssembleBoardCreateEvent;
import net.frozenorb.potpvp.util.scoreboard.api.events.AssembleBoardDestroyEvent;

@Getter
public class AssembleListener implements Listener {

	private final ScoreboardHandler scoreboardHandler;

	/**
	 * ScoreboardHandler Listener.
	 *
	 * @param scoreboardHandler instance.
	 */
	public AssembleListener(ScoreboardHandler scoreboardHandler) {
		this.scoreboardHandler=scoreboardHandler;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		AssembleBoardCreateEvent createEvent = new AssembleBoardCreateEvent(event.getPlayer());

		Bukkit.getPluginManager().callEvent(createEvent);
		if (createEvent.isCancelled()) {
			return;
		}

		getScoreboardHandler().getBoards().put(event.getPlayer().getUniqueId(), new AssembleBoard(event.getPlayer(), getScoreboardHandler()));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		AssembleBoardDestroyEvent destroyEvent = new AssembleBoardDestroyEvent(event.getPlayer());

		Bukkit.getPluginManager().callEvent(destroyEvent);
		if (destroyEvent.isCancelled()) {
			return;
		}

		getScoreboardHandler().getBoards().remove(event.getPlayer().getUniqueId());
		event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

}
