package net.frozenorb.potpvp.command.impl.misc;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.PotPvPCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.Require;
import xyz.refinedev.command.annotation.Sender;

public class BuildCommand implements PotPvPCommand {

    @Command(name = "", desc = "Toggle build mode")
    @Require("potpvp.build")
    public void buiild(@Sender Player sender) {
        if (sender.hasMetadata("Build")) {
            sender.removeMetadata("Build", PotPvPRP.getInstance());
            sender.sendMessage(ChatColor.RED + "Build mode disabled.");
        } else {
            sender.setMetadata("Build", new FixedMetadataValue(PotPvPRP.getInstance(), true));
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