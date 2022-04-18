package net.frozenorb.potpvp.events.menu;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import com.qrakn.morpheus.game.event.GameEvent;
import net.frozenorb.potpvp.events.menu.parameter.HostParametersMenu;
import net.frozenorb.potpvp.kt.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HostEventButton extends Button {

    private final GameEvent event;

    HostEventButton(GameEvent event) {
        this.event = event;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        if (player.hasPermission(event.getPermission())) {
            if (event.getParameters().isEmpty()) {

                for (Game game : GameQueue.INSTANCE.getGames()) {
                    if (game.getHost().equals(player)) {
                        player.sendMessage(ChatColor.RED + "You've already queued an event!");
                        player.closeInventory();
                        return;
                    }
                }

                if (GameQueue.INSTANCE.size() > 9) {
                    player.sendMessage(ChatColor.RED + "The game queue is currently full! Try again later.");
                } else {
                    GameQueue.INSTANCE.add(new Game(event, player, new ArrayList<>()));
                    player.sendMessage(ChatColor.GREEN + "You've added a " + event.getName().toLowerCase() + " event to the queue.");
                }

                player.closeInventory();
            } else {
                new HostParametersMenu(event).openMenu(player);
            }
        }
    }

    @Override
    public String getName(Player player) {
        if (player.hasPermission(event.getPermission())) {
            return ChatColor.GREEN + event.getName();
        }
        return ChatColor.RED + event.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        return Collections.singletonList(ChatColor.GRAY + event.getDescription());
    }

    @Override
    public Material getMaterial(Player player) {
        return event.getIcon().getType();
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) event.getIcon().getDurability();
    }
}
