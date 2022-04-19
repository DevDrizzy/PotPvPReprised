package net.frozenorb.potpvp.command.impl.events;

import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.events.menu.HostMenu;
import org.bukkit.entity.Player;
import xyz.refinedev.command.annotation.Command;

public class HostCommand implements PotPvPCommand {

    @Command(name = "", desc = "View event host menu")
    public void host(Player sender) {
        new HostMenu().openMenu(sender);
    }

    @Override
    public String getCommandName() {
        return "host";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}
