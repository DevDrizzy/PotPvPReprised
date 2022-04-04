package net.frozenorb.potpvp.scoreboard.construct;

import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class TitleGetter {

    private String defaultTitle;

    public TitleGetter(String defaultTitle) {
        this.defaultTitle = ChatColor.translateAlternateColorCodes('&', defaultTitle);
    }

    public String getTitle(Player player) {
        return defaultTitle;
    }

}