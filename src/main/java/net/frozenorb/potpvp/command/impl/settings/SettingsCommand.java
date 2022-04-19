package net.frozenorb.potpvp.command.impl.settings;

import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.profile.setting.menu.SettingsMenu;

import org.bukkit.entity.Player;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.Sender;

/**
 * /settings, accessible by all users, opens a {@link SettingsMenu}
 */
public class SettingsCommand implements PotPvPCommand {

    @Command(name = "", desc = "Open settings menu")
    public void settings(@Sender Player sender) {
        new SettingsMenu().openMenu(sender);
    }

    @Override
    public String getCommandName() {
        return "settings";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"preferences", "prefs", "options"};
    }
}