package net.frozenorb.potpvp.kt.menu

import net.frozenorb.potpvp.PotPvPSI
import org.bukkit.event.Listener
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.EventPriority

class ButtonListeners : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onButtonPress(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val openMenu = Menu.currentlyOpenedMenus[player.name]

        if (openMenu != null) {
            if (event.slot != event.rawSlot) {
                if (event.click == ClickType.SHIFT_LEFT || event.click == ClickType.SHIFT_RIGHT) {
                    event.isCancelled = true

                    if (event.currentItem != null) {
                        if (openMenu.acceptsShiftClickedItem(event.whoClicked as Player, event.currentItem)) {
                            player.openInventory.topInventory.addItem(event.currentItem)
                            event.currentItem = null
                        }
                    }
                }
                return
            }

            if (openMenu.buttons.containsKey(event.slot)) {
                val button = openMenu.buttons[event.slot]!!
                val cancel = button.shouldCancel(player, event.slot, event.click)

                if (!cancel && (event.click == ClickType.SHIFT_LEFT || event.click == ClickType.SHIFT_RIGHT)) {
                    event.isCancelled = true

                    if (event.currentItem != null) {
                        player.inventory.addItem(event.currentItem)
                    }
                } else {
                    event.isCancelled = cancel
                }

                button.clicked(player, event.slot, event.click, event.view)

                if (Menu.currentlyOpenedMenus.containsKey(player.name)) {
                    val newMenu = Menu.currentlyOpenedMenus[player.name]
                    if (newMenu === openMenu && newMenu.updateAfterClick) {
                        newMenu.openMenu(player)
                    }
                }

                if (event.isCancelled) {
                    Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), { player.updateInventory() }, 1L)
                }
            } else if (event.click == ClickType.SHIFT_LEFT || event.click == ClickType.SHIFT_RIGHT) {
                event.isCancelled = true

                if (openMenu.noncancellingInventory && event.currentItem != null) {
                    player.openInventory.topInventory.addItem(event.currentItem)
                    event.currentItem = null
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as Player
        val openMenu = Menu.currentlyOpenedMenus[player.name]

        if (openMenu != null) {
            if (event.view.cursor != null) {
                event.player.inventory.addItem(event.view.cursor)
                event.view.cursor = null
            }

            val manualClose = openMenu.manualClose
            openMenu.manualClose = true

            openMenu.onClose(player, manualClose)
            Menu.cancelCheck(player)
            Menu.currentlyOpenedMenus.remove(player.name)
        }
    }

}