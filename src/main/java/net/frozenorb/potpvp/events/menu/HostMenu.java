package net.frozenorb.potpvp.events.menu;

import com.qrakn.morpheus.game.event.GameEvent;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HostMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.DARK_PURPLE + "Host an event";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> toReturn = new HashMap<>();

        for (GameEvent event : GameEvent.getEvents()) {
            toReturn.put(toReturn.size(), new HostEventButton(event));
        }

        return toReturn;
    }

}
