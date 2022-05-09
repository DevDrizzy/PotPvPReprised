package net.frozenorb.potpvp.party;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.util.ItemUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

import static net.frozenorb.potpvp.PotPvPLang.LEFT_ARROW;
import static net.frozenorb.potpvp.PotPvPLang.RIGHT_ARROW;
import static org.bukkit.ChatColor.*;

@UtilityClass
public final class PartyItems {

    public static final Material ICON_TYPE = Material.NETHER_STAR;

    public static final ItemStack LEAVE_PARTY_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());
    public static final ItemStack ASSIGN_CLASSES = new ItemStack(Material.ITEM_FRAME);
    public static final ItemStack START_TEAM_SPLIT_ITEM = new ItemStack(Material.DIAMOND_SWORD);
    public static final ItemStack START_FFA_ITEM = new ItemStack(Material.GOLD_SWORD);
    public static final ItemStack OTHER_PARTIES_ITEM = new ItemStack(Material.SKULL_ITEM);

    static {
        ItemUtils.setDisplayName(LEAVE_PARTY_ITEM, RED + "Leave Party");
        ItemUtils.setDisplayName(ASSIGN_CLASSES, RED + "HCF Kits");
        ItemUtils.setDisplayName(START_TEAM_SPLIT_ITEM, RED + "Start Team Split");
        ItemUtils.setDisplayName(START_FFA_ITEM, RED + "Start Party FFA");
        ItemUtils.setDisplayName(OTHER_PARTIES_ITEM, RED + "Other Parties");
    }

    public static ItemStack icon(Party party) {
        ItemStack item = new ItemStack(ICON_TYPE);

        String leaderName = PotPvPRP.getInstance().getUuidCache().name(party.getLeader());
        String displayName = LEFT_ARROW + RED + BOLD + leaderName + RED + "'s Party" + RIGHT_ARROW;

        ItemUtils.setDisplayName(item, displayName);
        return item;
    }

}
