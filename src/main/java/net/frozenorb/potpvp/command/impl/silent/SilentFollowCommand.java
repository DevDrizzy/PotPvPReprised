package net.frozenorb.potpvp.command.impl.silent;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.impl.match.LeaveCommand;
import net.frozenorb.potpvp.command.Command;
import net.frozenorb.potpvp.command.param.Parameter;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class SilentFollowCommand {

    @Command(names = "silentfollow", permission = "potpvp.silent")
    public static void silentfollow(Player sender, @Parameter(name = "target") Player target) {
        sender.setMetadata("ModMode", new FixedMetadataValue(PotPvPRP.getInstance(), true));
        sender.setMetadata("invisible", new FixedMetadataValue(PotPvPRP.getInstance(), true));

        if (PotPvPRP.getInstance().getPartyHandler().hasParty(sender)) {
            LeaveCommand.leave(sender);
        }

        FollowCommand.follow(sender, target);
    }

}
