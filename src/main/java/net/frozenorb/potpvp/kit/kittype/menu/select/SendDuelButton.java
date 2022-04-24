package net.frozenorb.potpvp.kit.kittype.menu.select;

import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.google.common.collect.ImmutableList;

import lombok.AllArgsConstructor;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.Callback;
import org.bukkit.inventory.InventoryView;

@AllArgsConstructor
public class SendDuelButton extends Button {
    
    private Set<String> maps;
    private Callback<Set<String>> mapsCallback;
    
    @Override
    public List<String> getDescription(Player arg0) {
        return ImmutableList.of();
    }

    @Override
    public Material getMaterial(Player arg0) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player arg0) {
        return DyeColor.LIME.getWoolData();
    }
    
    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + "Send duel";
    }
    
    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (maps.size() < 2) {
            player.sendMessage(ChatColor.RED + "You must select at least two maps.");
            return;
        }
        
        mapsCallback.callback(maps);
    }
    
}
