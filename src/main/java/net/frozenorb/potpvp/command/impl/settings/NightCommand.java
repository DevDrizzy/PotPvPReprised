package net.frozenorb.potpvp.command.impl.settings;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.profile.setting.Setting;
import net.frozenorb.potpvp.profile.setting.SettingHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.Sender;

/**
 * /night command, allows players to toggle {@link Setting#NIGHT_MODE} setting
 */
public class NightCommand implements PotPvPCommand {

    @Command(name = "", desc = "Set your time to night")
    public void night(@Sender Player sender) {
        if (!Setting.NIGHT_MODE.canUpdate(sender)) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        SettingHandler settingHandler = PotPvPRP.getInstance().getSettingHandler();
        boolean enabled = !settingHandler.getSetting(sender, Setting.NIGHT_MODE);

        settingHandler.updateSetting(sender, Setting.NIGHT_MODE, enabled);

        if (enabled) {
            sender.sendMessage(ChatColor.GREEN + "Night mode on.");
        } else {
            sender.sendMessage(ChatColor.RED + "Night mode off.");
        }
    }

    @Override
    public String getCommandName() {
        return "night";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"nightMode"};
    }
}