package net.frozenorb.potpvp.adapter.scoreboard;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;

final class GameScoreGetter implements BiConsumer<Player, List<String>> {

    @Override
    public void accept(Player player, List<String> scores) {
        Game game = GameQueue.INSTANCE.getCurrentGame(player);

        if (game == null) return;
        if (!game.getPlayers().contains(player)) return;

        scores.addAll(game.getEvent().getScoreboardScores(player, game));
    }

}