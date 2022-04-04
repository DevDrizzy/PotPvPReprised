package net.frozenorb.potpvp.arena.menu.select;

import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import net.frozenorb.potpvp.kt.menu.Button;
import org.bukkit.inventory.InventoryView;

@AllArgsConstructor
public class ArenaButton extends Button {

    private String mapName;
    private Set<String> maps;
    
    @Override
    public String getName(Player player) {
        return mapName;
    }
    
    @Override
    public List<String> getDescription(Player player) {
        List<String> lines = Lists.newLinkedList();
        
        boolean isEnabled = maps.contains(mapName);
        
        if (isEnabled) {
            lines.add(ChatColor.GRAY + "Click here to " + ChatColor.RED + "remove" + ChatColor.GRAY + " this arena from the selection.");
        } else {
            lines.add(ChatColor.GRAY + "Click here to " + ChatColor.GREEN + "add" + ChatColor.GRAY + " this arena to the selection.");
        }
        
        return lines;
    }

    @Override
    public Material getMaterial(Player player) {
        boolean isEnabled = maps.contains(mapName);
        
        return isEnabled ? Material.MAP : Material.EMPTY_MAP;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        if (maps.contains(mapName)) {
            maps.remove(mapName);
            
            player.sendMessage(ChatColor.RED + "Removed " + mapName + " from the selection.");
        } else {
            maps.add(mapName);
            
            player.sendMessage(ChatColor.GREEN + "Added " + mapName + " to your selection.");
        }
        
    }
}
