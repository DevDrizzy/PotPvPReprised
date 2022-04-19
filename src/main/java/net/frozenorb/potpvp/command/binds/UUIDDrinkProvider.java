package net.frozenorb.potpvp.command.binds;

import org.jetbrains.annotations.Nullable;
import xyz.refinedev.command.argument.CommandArg;
import xyz.refinedev.command.exception.CommandExitMessage;
import xyz.refinedev.command.parametric.DrinkProvider;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/19/2022
 * Project: potpvp-reprised
 */

public class UUIDDrinkProvider extends DrinkProvider<UUID> {
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
    public UUID provide(CommandArg arg, List<? extends Annotation> annotations) throws CommandExitMessage {
        String string = arg.get();
        try {
            return UUID.fromString(string);
        } catch (Exception e) {
            throw new CommandExitMessage("Invalid UUID!");
        }
    }

    @Override
    public String argumentDescription() {
        return "uuid";
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        return null;
    }
}
