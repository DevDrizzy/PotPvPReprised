package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.command.Command;
import net.frozenorb.potpvp.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitSetDisplayNameCommand {

	@Command(names = { "kittype setdisplayname" }, permission = "op", description = "Sets a kit-type's display name")
	public static void execute(Player player, @Parameter(name = "kittype") KitType kitType, @Parameter(name = "displayName", wildcard = true) String displayName) {
		kitType.setDisplayName(displayName);
		kitType.saveAsync();

		player.sendMessage(ChatColor.GREEN + "You've updated this kit-type's display name.");
	}

}
