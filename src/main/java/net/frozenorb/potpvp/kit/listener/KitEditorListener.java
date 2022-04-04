package net.frozenorb.potpvp.kit.listener;

import net.frozenorb.potpvp.kit.menu.editkit.EditKitMenu;
import net.frozenorb.potpvp.kt.menu.Menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * "Modifications" needed to make the EditKitMenu work as expected
 */
public final class KitEditorListener implements Listener {

    /**
     * Prevents placing items into the top inventory
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
            return;
        }

        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        if (Menu.getCurrentlyOpenedMenus().get(player.getName()) instanceof EditKitMenu) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents all forms of dragging (the goal of this is
     * to prevent items being put into the top inventory,
     * but item dragging overall is too complicated to deal
     * with properly so we just disallow dragging.)
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (Menu.getCurrentlyOpenedMenus().get(player.getName()) instanceof EditKitMenu) {
            event.setCancelled(true);
        }
    }

}