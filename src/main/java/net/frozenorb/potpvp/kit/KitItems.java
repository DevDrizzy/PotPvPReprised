package net.frozenorb.potpvp.kit;

import net.frozenorb.potpvp.kt.util.ItemUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

import static org.bukkit.ChatColor.*;

@UtilityClass
public final class KitItems {

    public static final ItemStack OPEN_EDITOR_ITEM = new ItemStack(Material.BOOK);

    static {
        ItemUtils.setDisplayName(OPEN_EDITOR_ITEM, BLUE.toString() + BOLD + "» " + YELLOW + BOLD + "Kit Editor" + BLUE.toString() + BOLD + " «");
    }

}