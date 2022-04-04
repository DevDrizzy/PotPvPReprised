package net.frozenorb.potpvp.kittype;

import net.frozenorb.potpvp.command.param.ParameterType;
import org.apache.commons.lang3.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class KitTypeParameterType implements ParameterType<KitType> {

    @Override
    public KitType transform(CommandSender sender, String source) {
        for (KitType kitType : KitType.getAllTypes()) {
            if (!sender.isOp() && kitType.isHidden()) {
                continue;
            }

            if (kitType.getId().equalsIgnoreCase(source)) {
                return kitType;
            }
        }

        sender.sendMessage(ChatColor.RED + "No kit type with the name " + source + " found.");
        return null;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        for (KitType kitType : KitType.getAllTypes()) {
            if (!player.isOp() && kitType.isHidden()) {
                continue;
            }

            if (StringUtils.startsWithIgnoreCase(kitType.getId(), source)) {
                completions.add(kitType.getId());
            }
        }

        return completions;
    }

}