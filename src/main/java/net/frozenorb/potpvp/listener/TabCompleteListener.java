package net.frozenorb.potpvp.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.util.StringUtil;

import java.util.Collection;

public final class TabCompleteListener implements Listener {

    // we basically copy+paste the CraftBukkit code but modify the
    // visiblity check to work better with how PotPvP uses invis
    @EventHandler
    public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent event) {
        String token = event.getLastToken();
        Collection<String> completions = event.getTabCompletions();

        completions.clear();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!event.getPlayer().canSee(player) && player.hasMetadata("invisible")) {
                continue;
            }

            if (StringUtil.startsWithIgnoreCase(player.getName(), token)) {
                completions.add(player.getName());
            }
        }
    }

}