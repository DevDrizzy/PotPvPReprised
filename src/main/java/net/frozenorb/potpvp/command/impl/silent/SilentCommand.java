package net.frozenorb.potpvp.command.impl.silent;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.util.VisibilityUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.Require;
import xyz.refinedev.command.annotation.Sender;

public class SilentCommand implements PotPvPCommand {

    @Command(name = "", desc = "Silent mode")
    @Require("potpvp.staff.silent")
    public void silent(@Sender Player sender) {
        if (sender.hasMetadata("ModMode")) {
            sender.removeMetadata("ModMode", PotPvPRP.getInstance());
            sender.removeMetadata("invisible", PotPvPRP.getInstance());

            sender.sendMessage(ChatColor.RED + "Silent mode disabled.");
        } else {
            sender.setMetadata("ModMode", new FixedMetadataValue(PotPvPRP.getInstance(), true));
            sender.setMetadata("invisible", new FixedMetadataValue(PotPvPRP.getInstance(), true));
            
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