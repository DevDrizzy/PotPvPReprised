package net.frozenorb.potpvp.command.impl.misc;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.profile.setting.Setting;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.Sender;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/14/2022
 * Project: potpvp-reprised
 */

public class DJMCommand implements PotPvPCommand {

    @Command(name = "", desc = "Toggle tournament elimination messages")
    public void joinMessages(@Sender Player sender) {
        boolean oldValue = PotPvPRP.getInstance().getSettingHandler().getSetting(sender, Setting.SEE_TOURNAMENT_ELIMINATION_MESSAGES);
        if (!oldValue) {
            sender.sendMessage(ChatColor.RED + "You have already disabled tournament elimination messages.");
            return;
        }

        PotPvPRP.getInstance().getSettingHandler().updateSetting(sender, Setting.SEE_TOURNAMENT_ELIMINATION_MESSAGES, false);
        sender.sendMessage(ChatColor.GREEN + "Disabled tournament elimination messages.");
    }

    @Override
    public String getCommandName() {
        return "djm";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
