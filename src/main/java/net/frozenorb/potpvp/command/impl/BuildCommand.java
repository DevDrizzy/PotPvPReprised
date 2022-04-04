package net.frozenorb.potpvp.command.impl;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.command.PotPvPCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import xyz.refinedev.api.annotation.Command;
import xyz.refinedev.api.annotation.Require;

public class BuildCommand implements PotPvPCommand {

    @Command(name = "", desc = "Toggle build mode")
    @Require("potpvp.build")
    public void buiild(Player sender) {
        if (sender.hasMetadata("Build")) {
            sender.removeMetadata("Build", PotPvPSI.getInstance());
            sender.sendMessage(ChatColor.RED + "Build mode disabled.");
        } else {
            sender.setMetadata("Build", new FixedMetadataValue(PotPvPSI.getInstance(), true));
            sender.sendMessage(ChatColor.GREEN + "Build mode enabled.");
        }
    }

    @Override
    public String getCommandName() {
        return "build";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"buildmode", "builder"};
    }
}