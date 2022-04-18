package net.frozenorb.potpvp.command.impl.settings;

import net.frozenorb.potpvp.profile.setting.menu.SettingsMenu;
import net.frozenorb.potpvp.command.Command;

import org.bukkit.entity.Player;

/**
 * /settings, accessible by all users, opens a {@link SettingsMenu}
 */
public final class SettingsCommand {

    @Command(names = {"settings", "preferences", "prefs", "options"}, permission = "")
    public static void settings(Player sender) {
        new SettingsMenu().openMenu(sender);
    }

}