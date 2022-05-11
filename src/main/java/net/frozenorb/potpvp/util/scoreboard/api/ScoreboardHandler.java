package net.frozenorb.potpvp.util.scoreboard.api;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.potpvp.util.scoreboard.config.ScoreboardConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import net.frozenorb.potpvp.util.scoreboard.api.events.AssembleBoardCreateEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class ScoreboardHandler {

	private JavaPlugin plugin;

	private ScoreboardConfiguration adapter;
	private AssembleThread thread;
	private AssembleListener listeners;
	private AssembleStyle assembleStyle = AssembleStyle.KOHI;

	private Map<UUID, AssembleBoard> boards;

	private long ticks = 2;
	private boolean hook = false, debugMode = true;

	/**
	 * ScoreboardHandler.
	 *
	 * @param plugin instance.
	 */
	public ScoreboardHandler(JavaPlugin plugin, ScoreboardConfiguration config) {
		if (plugin == null) {
			throw new RuntimeException("ScoreboardHandler can not be instantiated without a plugin instance!");
		}

		this.plugin = plugin;
		this.adapter = config;
		this.boards = new ConcurrentHashMap<>();

		this.setup();
	}

	/**
	 * Setup ScoreboardHandler.
	 */
	public void setup() {
		// Register Events.
		this.listeners = new AssembleListener(this);
		this.plugin.getServer().getPluginManager().registerEvents(listeners, this.plugin);

		// Ensure that the thread has stopped running.
		if (this.thread != null) {
			this.thread.stopExecuting();
			this.thread.interrupt();
			this.thread = null;
		}

		// Register new boards for existing online players.
		for (Player player : Bukkit.getOnlinePlayers()) {
			// Make sure it doesn't double up.
			AssembleBoardCreateEvent createEvent = new AssembleBoardCreateEvent(player);

			Bukkit.getPluginManager().callEvent(createEvent);
			if (createEvent.isCancelled()) {
				return;
			}

			getBoards().putIfAbsent(player.getUniqueId(), new AssembleBoard(player, this));
		}

		// Start Thread.
		this.thread = new AssembleThread(this);
	}

	/**
	 *
	 */
	public void shutdown() {
		// Stop thread.
		if (this.thread != null) {
			this.thread.interrupt();
			this.thread = null;
		}

		// Unregister listeners.
		if (listeners != null) {
			HandlerList.unregisterAll(listeners);
			listeners = null;
		}

		// Destroy player scoreboards.
		for (UUID uuid : getBoards().keySet()) {
			Player player = Bukkit.getPlayer(uuid);

			if (player == null || !player.isOnline()) {
				continue;
			}

			getBoards().remove(uuid);
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
	}

}
