package net.frozenorb.potpvp.command.binds;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
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
 * Created: 4/13/2022
 * Project: potpvp-reprised
 */

public class ChatColorProvider extends DrinkProvider<ChatColor> {

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public ChatColor provide(CommandArg arg, List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        try {
            return ChatColor.valueOf(name);
        } catch (IllegalArgumentException e) {
            try {
                return ChatColor.getByChar(name);
            } catch (IllegalArgumentException e2) {
                throw new CommandExitMessage("A ChatColor with that name or code does not exist!");
            }
        }
    }

    @Override
    public String argumentDescription() {
        return "ChatColor";
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        return Arrays.stream(ChatColor.values()).map(ChatColor::name).filter((s) -> prefix.length() == 0 || s.startsWith(prefix)).collect(Collectors.toList());
    }
}
