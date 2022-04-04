package net.frozenorb.potpvp.morpheus.command;

import net.frozenorb.potpvp.command.Command;
import net.frozenorb.potpvp.morpheus.menu.HostMenu;
import org.bukkit.entity.Player;

public class HostCommand {

    @Command(names = { "host"}, permission = "")
    public static void host(Player sender) {
        new HostMenu().openMenu(sender);
    }

}
