package net.frozenorb.potpvp.command.impl.stats;

import com.google.common.collect.ImmutableSet;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.profile.elo.EloHandler;
import net.frozenorb.potpvp.profile.elo.repository.MongoEloRepository;
import net.frozenorb.potpvp.kit.kittype.KitType;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;

import net.frozenorb.potpvp.util.MongoUtils;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.Require;
import xyz.refinedev.command.annotation.Sender;

import java.util.*;

public class EloSetCommands implements PotPvPCommand {

    @Command(name = "setSolo", usage = "<target> <kitType> <newElo>", desc = "Set a target's elo")
    @Require("potpvp.elo.admin")
    public void eloSetSolo(@Sender Player sender, Player target, KitType kitType, int newElo) {
        EloHandler eloHandler = PotPvPRP.getInstance().getEloHandler();
        eloHandler.setElo(target, kitType, newElo);
        sender.sendMessage(ChatColor.YELLOW + "Set " + target.getName() + "'s " + kitType.getDisplayName() + " elo to " + newElo + ".");
    }

    @Command(name = "setTeam", usage = "<targetParty> <kitType> <newElo>", desc = "Set a target party's elo")
    @Require("potpvp.elo.admin")
    public void eloSetTeam(@Sender Player sender, Player target, KitType kitType, int newElo) {
        PartyHandler partyHandler = PotPvPRP.getInstance().getPartyHandler();
        EloHandler eloHandler = PotPvPRP.getInstance().getEloHandler();

        Party targetParty = partyHandler.getParty(target);

        if (targetParty == null) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not in a party.");
            return;
        }

        eloHandler.setElo(targetParty.getMembers(), kitType, newElo);
        sender.sendMessage(ChatColor.YELLOW + "Set " + kitType.getDisplayName() + " elo of " + PotPvPRP.getInstance().getUuidCache().name(targetParty.getLeader()) + "'s party to " + newElo + ".");
    }

    @Command(name = "recalcGlobalElo", desc = "Recalculate everyone's global ELO")
    @Require("potpvp.elo.admin")
    public void recalcGlobalElo(@Sender Player sender) {
        List<Document> documents = MongoUtils.getCollection(MongoEloRepository.MONGO_COLLECTION_NAME).find().into(new ArrayList<>());
        sender.sendMessage(ChatColor.GREEN + "Recalculating " + documents.size() + " players global elo...");
        final int[] wrapper = new int[2];
        documents.forEach(document -> {
            try {
                UUID uuid = UUID.fromString((String) document.get("players", ArrayList.class).get(0));
                Set<UUID> uuidSet = ImmutableSet.of(uuid);
                Map<KitType, Integer> eloMap = MongoEloRepository.getInstance().loadElo(uuidSet);
                MongoEloRepository.getInstance().saveElo(uuidSet, eloMap);

                wrapper[0]++;
                if (wrapper[0] % 100 == 0) {
                    sender.sendMessage(ChatColor.GREEN + "Finished " + wrapper[0] + " out of " + documents.size() +" players...");
                }
            } catch (Exception e) {
                e.printStackTrace();
                wrapper[1]++;
            }
        });
    }

    @Override
    public String getCommandName() {
        return "elo";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}