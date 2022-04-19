package net.frozenorb.potpvp.command.impl.settings;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.profile.setting.Setting;
import net.frozenorb.potpvp.profile.setting.SettingHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.command.annotation.Command;

/**
 * /toggleglobalchat command, allows players to toggle {@link Setting#ENABLE_GLOBAL_CHAT} setting
 */
public class ToggleGlobalChatCommand implements PotPvPCommand {

    @Command(name = "", desc = "Toggle global chat for your profile")
    public void toggleGlobalChat(Player sender) {
        if (!Setting.ENABLE_GLOBAL_CHAT.canUpdate(sender)) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        SettingHandler settingHandler = PotPvPRP.getInstance().getSettingHandler();
        boolean enabled = !settingHandler.getSetting(sender, Setting.ENABLE_GLOBAL_CHAT);

        settingHandler.updateSetting(sender, Setting.ENABLE_GLOBAL_CHAT, enabled);

        if (enabled) {
            sender.sendMessage(ChatColor.GREEN + "Toggled global chat on.");
        } else {
            sender.sendMessage(ChatColor.RED + "Toggled global chat off.");
        }
    }

    @Override
    public String getCommandName() {
        return "toggleGlobalChat";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"tgc", "togglechat"};
    }
}