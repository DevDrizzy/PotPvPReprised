package net.frozenorb.potpvp.morpheus.command;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import net.frozenorb.potpvp.command.Command;
import org.bukkit.entity.Player;

public class ForceEndCommand {

    @Command(names = { "forceend"}, permission = "op")
    public static void host(Player sender) {
        Game game = GameQueue.INSTANCE.getCurrentGame(sender);

        if (game == null) {
            sender.sendMessage("You're not in a game");
            return;
        }

        game.end();
    }

}
