package net.frozenorb.potpvp.command.impl;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.hologram.HologramMeta;
import net.frozenorb.potpvp.hologram.HologramType;
import net.frozenorb.potpvp.hologram.PracticeHologram;
import net.frozenorb.potpvp.hologram.impl.GlobalHologram;
import net.frozenorb.potpvp.hologram.impl.KitHologram;
import net.frozenorb.potpvp.kit.kittype.KitType;
import org.bukkit.entity.Player;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.OptArg;
import xyz.refinedev.command.annotation.Sender;
import xyz.refinedev.command.util.CC;

import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/26/2022
 * Project: potpvp-reprised
 */

public class HologramCommands implements PotPvPCommand {

    @Command(name = "create", usage = "<name> <type> [kit]", desc = "Create a hologram")
    public void create(@Sender Player sender, String name, HologramType type, @OptArg KitType kitType) {
        PracticeHologram practiceHologram;
        if (type == HologramType.GLOBAL) {
            practiceHologram = new GlobalHologram(PotPvPRP.getInstance());
        } else {
            if (kitType == null) {
                sender.sendMessage(CC.translate("&cPlease provide a valid kitType!"));
                return;
            }
            practiceHologram = new KitHologram(PotPvPRP.getInstance(), kitType);
        }

        HologramMeta meta = new HologramMeta(UUID.randomUUID());
        meta.setLocation(sender.getLocation());
        meta.setName(CC.translate(name));

        practiceHologram.setMeta(meta);
        practiceHologram.spawn();
    }

    @Override
    public String getCommandName() {
        return "prachologram";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"practicehologram", "ph"};
    }
}
