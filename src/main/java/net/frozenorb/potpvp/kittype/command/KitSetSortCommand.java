package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.command.Command;
import net.frozenorb.potpvp.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

public class KitSetSortCommand {

	@Command(names = { "kittype setsort" }, permission = "op", description = "Sets a kit-type's sort")
	public static void execute(Player player, @Parameter(name = "kittype") KitType kitType, @Parameter(name = "sort") int sort) {
		kitType.setSort(sort);
		kitType.saveAsync();

		KitType.getAllTypes().sort(Comparator.comparing(KitType::getSort));

		player.sendMessage(ChatColor.GREEN + "You've updated this kit-type's sort.");
	}

}
