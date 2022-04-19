package net.frozenorb.potpvp.command.impl.misc;

import net.frozenorb.potpvp.arena.menu.manage.ManageMenu;
import net.frozenorb.potpvp.command.PotPvPCommand;
import org.bukkit.entity.Player;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.Require;
import xyz.refinedev.command.annotation.Sender;

public class ManageCommand implements PotPvPCommand {

    @Command(name = "", desc = "Manage potpvp")
    @Require("potpvp.admin")
    public void manage(@Sender Player sender) {
        new ManageMenu().openMenu(sender);
    }

    @Override
    public String getCommandName() {
        return "manage";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}