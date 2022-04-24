package net.frozenorb.potpvp.util.menu;

import net.frozenorb.potpvp.PotPvPRP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class ButtonListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onButtonPress(InventoryClickEvent event) {

        final Player player = (Player) event.getWhoClicked();

        final Menu openMenu = Menu.getCurrentlyOpenedMenus().get(player.getUniqueId());

        if (openMenu != null) {

            if (event.getSlot() != event.getRawSlot()) {

                if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);

                    if (openMenu.isNoncancellingInventory()) {

                        if (event.getCurrentItem() != null) {
                            player.getOpenInventory().getTopInventory().addItem(event.getCurrentItem());
                            event.setCurrentItem(null);
                        }
                    }
                }

                return;

            }
            if (openMenu.getButtons().containsKey(event.getSlot())) {

                player.setMetadata("CLICKED_BUTTON",new FixedMetadataValue(PotPvPRP.getInstance(),player.getUniqueId().toString()));

                final Button button = openMenu.getButtons().get(event.getSlot());

                final boolean cancel = button.shouldCancel(player, event.getSlot(), event.getClick());

                if (!cancel && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);

                    if (event.getCurrentItem() != null) {
                        player.getInventory().addItem(event.getCurrentItem());
                    }

                } else {
                    event.setCancelled(cancel);
                }


                button.clicked(player,event.getSlot(),event.getClick());

                if (Menu.getCurrentlyOpenedMenus().containsKey(player.getUniqueId())) {

                    final Menu newMenu = Menu.getCurrentlyOpenedMenus().get(player.getUniqueId());

                    if (newMenu == openMenu && newMenu.isUpdateAfterClick()) {
                        newMenu.openMenu(player);
                    }


                }

                if (event.isCancelled()) {
                    PotPvPRP.getInstance().getServer().getScheduler().runTaskLater(PotPvPRP.getInstance(), player::updateInventory, 1L);
                }

                player.removeMetadata("CLICKED_BUTTON", PotPvPRP.getInstance());

            } else {
                if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);

                    if (openMenu.isNoncancellingInventory()) {

                        if (event.getCurrentItem() != null) {
                            player.getOpenInventory().getTopInventory().addItem(event.getCurrentItem());
                            event.setCurrentItem(null);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        final Player player = (Player)event.getPlayer();
        final Menu openMenu = Menu.getCurrentlyOpenedMenus().get(player.getUniqueId());

        if (openMenu != null) {

            if (!player.hasMetadata("CLICKED_BUTTON")) {
                openMenu.onClose(player);
            }

            Menu.cancelCheck(player);
            Menu.getCurrentlyOpenedMenus().remove(player.getUniqueId());
        }

    }

}