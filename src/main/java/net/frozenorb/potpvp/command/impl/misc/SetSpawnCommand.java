package net.frozenorb.potpvp.command.impl.misc;

import net.frozenorb.potpvp.command.PotPvPCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.Require;
import xyz.refinedev.command.annotation.Sender;

public class SetSpawnCommand implements PotPvPCommand {

    @Command(name = "", desc = "Set spawn command")
    @Require("potpvp.setspawn")
    public void setSpawn(@Sender Player sender) {
        Location loc = sender.getLocation();

        sender.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        ((CraftWorld)sender.getWorld()).getHandle().setSpawn(
            loc.getBlockX(),
            loc.getBlockY(),
            loc.getBlockZ(),
            loc.getYaw(),
            loc.getPitch()
        );

        sender.sendMessage(ChatColor.YELLOW + "Spawn point updated!");
    }

    @Override
    public String getCommandName() {
        return "setspawn";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}