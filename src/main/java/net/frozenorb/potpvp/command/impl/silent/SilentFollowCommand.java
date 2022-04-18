package net.frozenorb.potpvp.command.impl.silent;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.command.impl.match.LeaveCommand;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.Require;
import xyz.refinedev.command.annotation.Sender;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class SilentFollowCommand implements PotPvPCommand {

    @Command(name = "", usage = "<target>", desc = "Silently follow a target")
    @Require("potpvp.staff.follow")
    public void silentfollow(@Sender Player sender, Player target) {
        new SilentCommand().silent(sender);

        if (PotPvPRP.getInstance().getPartyHandler().hasParty(sender)) {
            new LeaveCommand().leave(sender);
        }

        new FollowCommand().follow(sender, target);
    }

    @Override
    public String getCommandName() {
        return "silentfollow";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}
