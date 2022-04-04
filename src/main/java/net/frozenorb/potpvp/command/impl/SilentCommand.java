package net.frozenorb.potpvp.command.impl;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.util.VisibilityUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import xyz.refinedev.api.annotation.Command;
import xyz.refinedev.api.annotation.Require;

public class SilentCommand implements PotPvPCommand {

    @Command(name = "", desc = "Silent mode")
    @Require("potpvp.silent")
    public void silent(Player sender) {
        if (sender.hasMetadata("ModMode")) {
            sender.removeMetadata("ModMode", PotPvPSI.getInstance());
            sender.removeMetadata("invisible", PotPvPSI.getInstance());

            sender.sendMessage(ChatColor.RED + "Silent mode disabled.");
        } else {
            sender.setMetadata("ModMode", new FixedMetadataValue(PotPvPSI.getInstance(), true));
            sender.setMetadata("invisible", new FixedMetadataValue(PotPvPSI.getInstance(), true));
            
            sender.sendMessage(ChatColor.GREEN + "Silent mode enabled.");
        }

        VisibilityUtils.updateVisibility(sender);
    }

    @Override
    public String getCommandName() {
        return "silent";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}