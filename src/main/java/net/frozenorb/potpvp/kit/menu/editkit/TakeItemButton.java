package net.frozenorb.potpvp.kit.menu.editkit;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.kt.menu.Button;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;

final class TakeItemButton extends Button {

    private final ItemStack item;

    TakeItemButton(ItemStack item) {
        this.item = Preconditions.checkNotNull(item, "item");
    }

    // We just override this whole method, as we need to keep enchants/potion data/etc
    @Override
    public ItemStack getButtonItem(Player player) {
        return item;
    }

    // We pass through the item given to us with some lore so all these
    // are unused. The fact we have to do this does represent a gap in
    // the menu api's functionality, but we can save that for another day.
    @Override public String getName(Player player) { return null; }
    @Override public List<String> getDescription(Player player) { return null; }
    @Override public Material getMaterial(Player player) { return null; }

    @Override
    public void clicked(final Player player, final int slot, ClickType clickType, InventoryView view) {
        // make the item show up again
        Bukkit.getScheduler().runTaskLater(PotPvPRP.getInstance(), () -> {
            player.getOpenInventory().getTopInventory().setItem(slot, item);
        }, 4L);
    }

    @Override
    public boolean shouldCancel(Player player, int slot, ClickType clickType) {
        return false;
    }

}