package net.frozenorb.potpvp.events.command;

import net.frozenorb.potpvp.command.Command;
import net.frozenorb.potpvp.events.menu.HostMenu;
import org.bukkit.entity.Player;

public class HostCommand {

    @Command(names = { "host"}, permission = "")
    public static void host(Player sender) {
        new HostMenu().openMenu(sender);
    }

}
