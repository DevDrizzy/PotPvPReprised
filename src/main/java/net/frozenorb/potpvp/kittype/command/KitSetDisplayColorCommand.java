package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.command.Command;
import net.frozenorb.potpvp.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitSetDisplayColorCommand {

	@Command(names = { "kittype setdisplaycolor" }, permission = "op", description = "Sets a kit-type's display color")
	public static void execute(Player player, @Parameter(name = "kittype") KitType kitType, @Parameter(name = "displayColor", wildcard = true) String color) {
		kitType.setDisplayColor(ChatColor.valueOf(color.toUpperCase().replace(" ", "_")));
		kitType.saveAsync();

		player.sendMessage(ChatColor.GREEN + "You've updated this kit-type's display color.");
	}

}
