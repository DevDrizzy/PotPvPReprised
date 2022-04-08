package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.command.Command;
import net.frozenorb.potpvp.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitDeleteCommand {

	@Command(names = { "kittype delete" }, permission = "op", description = "Deletes an existing kit-type")
	public static void execute(Player player, @Parameter(name = "kittype") KitType kitType) {
		kitType.deleteAsync();
		KitType.getAllTypes().remove(kitType);
		PotPvPRP.getInstance().getQueueHandler().removeQueues(kitType);

		player.sendMessage(ChatColor.GREEN + "You've deleted the kit-type by the ID \"" + kitType.getId() + "\".");
	}

}
