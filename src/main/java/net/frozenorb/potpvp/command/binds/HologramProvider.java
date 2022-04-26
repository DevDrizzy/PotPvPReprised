package net.frozenorb.potpvp.command.binds;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.hologram.HologramHandler;
import net.frozenorb.potpvp.hologram.HologramMeta;
import net.frozenorb.potpvp.hologram.PracticeHologram;
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

public class HologramProvider extends DrinkProvider<PracticeHologram> {

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public PracticeHologram provide(CommandArg arg, List<? extends Annotation> annotations) throws CommandExitMessage {
        HologramHandler hologramHandler = PotPvPRP.getInstance().getHologramHandler();
        String name = arg.get();

        PracticeHologram hologram = hologramHandler.getByName(name);
        if (hologram == null) {
            throw new CommandExitMessage("Invalid Hologram!");
        }

        return hologram;
    }

    @Override
    public String argumentDescription() {
        return "hologram";
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        return PotPvPRP.getInstance().getHologramHandler().getHolograms().stream()
                .map(PracticeHologram::getMeta)
                .map(HologramMeta::getName)
                .filter((s) -> prefix.length() == 0 || s.startsWith(prefix))
                .collect(Collectors.toList());
    }
}
