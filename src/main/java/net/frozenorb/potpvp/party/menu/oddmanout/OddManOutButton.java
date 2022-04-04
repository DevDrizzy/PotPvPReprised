package net.frozenorb.potpvp.party.menu.oddmanout;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.util.Callback;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.util.List;

final class OddManOutButton extends Button {

    private final boolean oddManOut;
    private final Callback<Boolean> callback;

    OddManOutButton(boolean oddManOut, Callback<Boolean> callback) {
        this.oddManOut = oddManOut;
        this.callback = Preconditions.checkNotNull(callback, "callback");
    }

    @Override
    public String getName(Player player) {
        if (oddManOut) {
            return ChatColor.GREEN.toString() + ChatColor.BOLD + "Have a random player sit out";
        } else {
            return ChatColor.RED.toString() + ChatColor.BOLD + "Continue with uneven teams";
        }
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of();
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (oddManOut ? DyeColor.GREEN : DyeColor.RED).getWoolData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        callback.callback(oddManOut);
    }

}