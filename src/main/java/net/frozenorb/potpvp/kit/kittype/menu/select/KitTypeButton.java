package net.frozenorb.potpvp.kit.kittype.menu.select;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.kit.kittype.KitType;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.Callback;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import xyz.refinedev.spigot.utils.CC;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

final class KitTypeButton extends Button {

    private final KitType kitType;
    private final Callback<KitType> callback;
    private final List<String> descriptionLines;
    private final int amount;
    private final boolean doOriginal;

    KitTypeButton(KitType kitType, Callback<KitType> callback, boolean original) {
        this(kitType, callback, ImmutableList.of(), 1, original);
    }

    KitTypeButton(KitType kitType, Callback<KitType> callback, List<String> descriptionLines, int amount, boolean doOriginal) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.descriptionLines = ImmutableList.copyOf(descriptionLines);
        this.amount = amount;
        this.doOriginal = doOriginal;
    }

    @Override
    public String getName(Player player) {
        if (!doOriginal) return ChatColor.RED + ChatColor.BOLD.toString() + kitType.getDisplayName();
        return kitType.getDisplayColor() + ChatColor.BOLD.toString() + kitType.getDisplayName();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();

        if (doOriginal) {
            if (kitType.isHidden()) {
                description.add("");
                description.add(ChatColor.GRAY + "Hidden from normal players");
            }
            if (!descriptionLines.isEmpty()) {
                description.addAll(descriptionLines.stream().map(CC::translate).collect(Collectors.toList()));
            }
            description.add("");
            description.add(ChatColor.YELLOW + "Click here to select " + ChatColor.YELLOW + ChatColor.BOLD + kitType.getDisplayName() + ChatColor.YELLOW + ".");
        } else {
            if (!descriptionLines.isEmpty()) description.addAll(descriptionLines.stream().map(CC::translate).collect(Collectors.toList()));
        }
        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return kitType.getIcon().getItemType();
    }

    @Override
    public int getAmount(Player player) {
        return amount;
    }

    @Override
    public byte getDamageValue(Player player) {
        return kitType.getIcon().getData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        callback.callback(kitType);
    }

}