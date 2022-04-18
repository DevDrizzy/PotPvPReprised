package net.frozenorb.potpvp.command.binds;

import net.frozenorb.potpvp.kit.kittype.KitType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.refinedev.command.argument.CommandArg;
import xyz.refinedev.command.exception.CommandExitMessage;
import xyz.refinedev.command.parametric.DrinkProvider;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/4/2022
 * Project: potpvp-reprised
 */

public class KitTypeProvider extends DrinkProvider<KitType> {

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public KitType provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        Player sender = arg.getSenderAsPlayer();
        for (KitType kitType : KitType.getAllTypes()) {
            if (!sender.isOp() && kitType.isHidden()) {
                continue;
            }

            if (kitType.getId().equalsIgnoreCase(arg.get())) {
                return kitType;
            }
        }

        sender.sendMessage(ChatColor.RED + "No kit type with the name " + arg.get() + " found.");
        return null;
    }

    @Override
    public String argumentDescription() {
        return "kitType";
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        List<String> completions = new ArrayList<>();

        for (KitType kitType : KitType.getAllTypes()) {
            if (StringUtils.startsWithIgnoreCase(kitType.getId(), prefix)) {
                completions.add(kitType.getId());
            }
        }

        return completions;
    }
}
