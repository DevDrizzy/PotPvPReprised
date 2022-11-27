package net.frozenorb.potpvp.listener;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.util.StringUtil;

import java.util.Collection;

public final class TabCompleteListener implements Listener {

    @EventHandler
    public void onPlayerChatTabComplete(AsyncTabCompleteEvent event) {
        if (!(event.getSender() instanceof Player)) return;

        Player sender = (Player) event.getSender();
        String token = event.getBuffer();
        Collection<String> completions = event.getCompletions();

        completions.clear();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!sender.canSee(player) && player.hasMetadata("invisible")) {
                continue;
            }

            if (StringUtil.startsWithIgnoreCase(player.getName(), token)) {
                completions.add(player.getName());
            }
        }
    }

}