package net.frozenorb.potpvp.party.menu.otherparties;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.impl.duel.DuelCommand;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.kt.menu.Button;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final class OtherPartyButton extends Button {

    private final Party party;

    OtherPartyButton(Party party) {
        this.party = Preconditions.checkNotNull(party, "party");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_PURPLE + PotPvPRP.getInstance().getUuidCache().name(party.getLeader());
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();

        description.add("");

        for (UUID member : party.getMembers()) {
            ChatColor color = party.isLeader(member) ? ChatColor.DARK_PURPLE : ChatColor.YELLOW;
            description.add(color + PotPvPRP.getInstance().getUuidCache().name(member));
        }

        description.add("");
        description.add(ChatColor.GREEN + "» Click to duel «");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SKULL_ITEM;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 3; // player head
    }

    @Override
    public int getAmount(Player player) {
        return party.getMembers().size();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        Party senderParty = PotPvPRP.getInstance().getPartyHandler().getParty(player);

        if (senderParty == null) {
            return;
        }

        if (senderParty.isLeader(player.getUniqueId())) {
            DuelCommand.duel(player, Bukkit.getPlayer(party.getLeader()));
        } else {
            player.sendMessage(ChatColor.RED + "Only the leader can duel other parties!");
        }
    }

}