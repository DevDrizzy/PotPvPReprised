package net.frozenorb.potpvp.command.impl.events;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import net.frozenorb.potpvp.command.PotPvPCommand;
import org.bukkit.entity.Player;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.Require;
import xyz.refinedev.command.annotation.Sender;

public class ForceEndCommand implements PotPvPCommand {

    @Command(name = "", desc = "Force-end an on-going event")
    @Require("potpvp.event.forceend")
    public static void host(@Sender Player sender) {
        Game game = GameQueue.INSTANCE.getCurrentGame(sender);

        if (game == null) {
            sender.sendMessage("You're not in a game");
            return;
        }

        game.end();
    }

    @Override
    public String getCommandName() {
        return "forceend";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
