package net.frozenorb.potpvp.command.impl;

import net.frozenorb.potpvp.command.PotPvPCommand;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/8/2022
 * Project: potpvp-reprised
 */

public class KitCommands implements PotPvPCommand {
    @Override
    public String getCommandName() {
        return "kit";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"kitType"};
    }
}
