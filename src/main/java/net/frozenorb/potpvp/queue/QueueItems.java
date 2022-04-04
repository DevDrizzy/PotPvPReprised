package net.frozenorb.potpvp.queue;

import net.frozenorb.potpvp.kt.util.ItemUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

import static org.bukkit.ChatColor.*;

@UtilityClass
public final class QueueItems {

    public static final ItemStack JOIN_SOLO_UNRANKED_QUEUE_ITEM = new ItemStack(Material.IRON_SWORD);
    public static final ItemStack LEAVE_SOLO_UNRANKED_QUEUE_ITEM = new ItemStack(Material.INK_SACK, 1, (byte) DyeColor.RED.getDyeData());

    public static final ItemStack JOIN_SOLO_RANKED_QUEUE_ITEM = new ItemStack(Material.DIAMOND_SWORD);
    public static final ItemStack LEAVE_SOLO_RANKED_QUEUE_ITEM = new ItemStack(Material.INK_SACK, 1, (byte) DyeColor.RED.getDyeData());

    public static final ItemStack JOIN_PARTY_UNRANKED_QUEUE_ITEM = new ItemStack(Material.IRON_SWORD);
    public static final ItemStack LEAVE_PARTY_UNRANKED_QUEUE_ITEM = new ItemStack(Material.ARROW);

    public static final ItemStack JOIN_PARTY_RANKED_QUEUE_ITEM = new ItemStack(Material.DIAMOND_SWORD);
    public static final ItemStack LEAVE_PARTY_RANKED_QUEUE_ITEM = new ItemStack(Material.ARROW);

    static {
        ItemUtils.setDisplayName(JOIN_SOLO_UNRANKED_QUEUE_ITEM, BLUE.toString() + BOLD + "» " + GREEN + BOLD + "Join " + GRAY + BOLD + "Unranked" + GREEN + BOLD + " Queue" + BLUE.toString() + BOLD + " «");
        ItemUtils.setDisplayName(LEAVE_SOLO_UNRANKED_QUEUE_ITEM, BLUE.toString() + BOLD + "» " + RED + BOLD + "Leave Unranked Queue" + BLUE.toString() + BOLD + " «");

        ItemUtils.setDisplayName(JOIN_SOLO_RANKED_QUEUE_ITEM, BLUE.toString() + BOLD + "» " + GREEN + BOLD + "Join " + AQUA + BOLD + "Ranked" + GREEN + BOLD + " Queue" + BLUE.toString() + BOLD + " «");
        ItemUtils.setDisplayName(LEAVE_SOLO_RANKED_QUEUE_ITEM, BLUE.toString() + BOLD + "» " + RED + BOLD + "Leave Ranked Queue" + BLUE.toString() + BOLD + " «");

        ItemUtils.setDisplayName(JOIN_PARTY_UNRANKED_QUEUE_ITEM, BLUE.toString() + BOLD + "» " + GREEN + BOLD + "Play 2v2 Unranked" + BLUE.toString() + BOLD + " «");
        ItemUtils.setDisplayName(LEAVE_PARTY_UNRANKED_QUEUE_ITEM, BLUE.toString() + BOLD + "» " + RED + BOLD + "Leave 2v2 Unranked" + BLUE.toString() + BOLD + " «");

        ItemUtils.setDisplayName(JOIN_PARTY_RANKED_QUEUE_ITEM, BLUE.toString() + BOLD + "» " + GREEN + BOLD + "Play 2v2 Ranked" + BLUE.toString() + BOLD + " «");
        ItemUtils.setDisplayName(LEAVE_PARTY_RANKED_QUEUE_ITEM, BLUE.toString() + BOLD + "» " + RED + BOLD + "Leave 2v2 Ranked" + BLUE.toString() + BOLD + " «");
    }

}