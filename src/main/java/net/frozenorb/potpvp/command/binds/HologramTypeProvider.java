package net.frozenorb.potpvp.command.binds;

import net.frozenorb.potpvp.hologram.HologramType;
import org.bukkit.ChatColor;
import xyz.refinedev.command.argument.CommandArg;
import xyz.refinedev.command.exception.CommandExitMessage;
import xyz.refinedev.command.parametric.DrinkProvider;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/26/2022
 * Project: potpvp-reprised
 */

public class HologramTypeProvider extends DrinkProvider<HologramType> {

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public HologramType provide(CommandArg arg, List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        try {
            return HologramType.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new CommandExitMessage("A HologramType with that name or code does not exist!");
        }
    }

    @Override
    public String argumentDescription() {
        return "hologramType";
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        return Arrays.stream(HologramType.values()).map(HologramType::name).filter((s) -> prefix.length() == 0 || s.startsWith(prefix)).collect(Collectors.toList());
    }
}
