package net.frozenorb.potpvp.party.menu;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.pvpclasses.PvPClass;
import net.frozenorb.potpvp.pvpclasses.PvPClassHandler;
import net.frozenorb.potpvp.pvpclasses.PvPClasses;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.pagination.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.util.*;

public class RosterMenu extends PaginatedMenu {

    private final Party party;

    public RosterMenu(Party party) {
        this.party = party;

        setAutoUpdate(true);
        setUpdateAfterClick(true);
        setPlaceholder(true);
    }



    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Team Roster";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> toReturn = new HashMap<>();

        if (party.getMembers().size() == 1) {
            player.closeInventory();
            return toReturn;
        }

        for (UUID uuid : new ArrayList<>(party.getKits().keySet())) {
            if (!(party.getMembers().contains(uuid))) {
                party.getKits().remove(uuid);
            }
        }

        for (UUID uuid : party.getMembers()) {
            Player member = Bukkit.getPlayer(uuid);

            if (member == null) {
                continue;
            }

            PvPClasses selected = party.getKits().getOrDefault(uuid, PvPClasses.DIAMOND);

            toReturn.put(toReturn.isEmpty() ? 0 : toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return member.getDisplayName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    List<String> description = new ArrayList<>();

                    for (PvPClasses kit : PvPClasses.values()) {
                        if (kit == selected) {
                            description.add(ChatColor.GREEN + "> " + kit.getName());
                        } else {
                            if (kit.allowed(party)) {
                                description.add(ChatColor.GRAY + kit.getName());
                            } else {
                                description.add(ChatColor.RED + ChatColor.STRIKETHROUGH.toString() + kit.getName());
                            }
                        }
                    }

                    return description;
                }

                @Override
                public Material getMaterial(Player player) {
                    return selected.getIcon();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
                    if (party.isLeader(player.getUniqueId())) {
                        List<PvPClasses> kits = Arrays.asList(PvPClasses.values());
                        int index = kits.indexOf(selected);
                        PvPClasses next = null;

                        int times = 0;
                        while (next == null && times <= 50) {
                            times++;
                            if (index+1 < kits.size()) {
                                next = kits.get(index+1);
                                if (!(next.allowed(party))) {
                                    next = null;
                                    index++;
                                }
                            } else {
                                index = -1;
                            }
                        }

                        if (next == null) {
                            next = PvPClasses.DIAMOND;
                        }

                        party.message(player.getDisplayName() + ChatColor.YELLOW + " has set " + member.getDisplayName() +  ChatColor.YELLOW + "'s " + ChatColor.YELLOW + " kit to " + ChatColor.GRAY + next.getName() + ChatColor.YELLOW + ".");

                        party.getKits().put(uuid, next);

                        for (UUID other : party.getMembers()) {
                            PotPvPSI.getInstance().getPartyHandler().updatePartyCache(other, party);
                        }

                    }
                }
            });
        }

        return toReturn;
    }

    @Override
    public int size(Map<Integer, ? extends Button> buttons) {
        return 9 * 5;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 9*4;
    }
}
