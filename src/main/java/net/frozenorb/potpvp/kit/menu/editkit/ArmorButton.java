package net.frozenorb.potpvp.kit.menu.editkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.util.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

final class ArmorButton extends Button {
    
    private final ItemStack item;
    
    ArmorButton(ItemStack item) {
        this.item = Preconditions.checkNotNull(item, "item");
    }
    
    // We just override this whole method, as we need to keep enchants/potion
    // data/etc
    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack newItem = item.clone();
        ItemMeta itemMeta = newItem.getItemMeta();
        
        if (itemMeta != null) {
            itemMeta.setLore(ImmutableList.of("", ChatColor.YELLOW + "This is automatically equipped."));
            newItem.setItemMeta(itemMeta);
        }
        
        return newItem;
    }
    
    // We pass through the item given to us with some lore so all these
    // are unused. The fact we have to do this does represent a gap in
    // the menu api's functionality, but we can save that for another day.
    @Override
    public String getName(Player player) {
        return null;
    }
    
    @Override
    public List<String> getDescription(Player player) {
        return null;
    }
    
    @Override
    public Material getMaterial(Player player) {
        return null;
    }
    
}