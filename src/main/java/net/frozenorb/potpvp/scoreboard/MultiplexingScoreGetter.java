package net.frozenorb.potpvp.scoreboard;

import java.util.List;
import java.util.function.BiConsumer;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import com.qrakn.morpheus.game.GameState;
import net.frozenorb.potpvp.scoreboard.construct.ScoreGetter;
import org.bukkit.entity.Player;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import xyz.refinedev.spigot.utils.CC;

final class MultiplexingScoreGetter implements ScoreGetter {

    private final BiConsumer<Player, List<String>> matchScoreGetter;
    private final BiConsumer<Player, List<String>> lobbyScoreGetter;
    private final BiConsumer<Player, List<String>> gameScoreGetter;

    MultiplexingScoreGetter(
        BiConsumer<Player, List<String>> matchScoreGetter,
        BiConsumer<Player, List<String>> lobbyScoreGetter,
        BiConsumer<Player, List<String>> gameScoreGetter

    ) {
        this.matchScoreGetter = matchScoreGetter;
        this.lobbyScoreGetter = lobbyScoreGetter;
        this.gameScoreGetter = gameScoreGetter;
    }

    @Override
    public void getScores(List<String> scores, Player player) {
        if (PotPvPRP.getInstance() == null) return;
        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();
        SettingHandler settingHandler = PotPvPRP.getInstance().getSettingHandler();

        if (settingHandler.getSetting(player, Setting.SHOW_SCOREBOARD)) {
        scores.add("&a" + CC.SB_BAR);
            if (matchHandler.isPlayingOrSpectatingMatch(player)) {
                matchScoreGetter.accept(player, scores);
            } else {
                Game game = GameQueue.INSTANCE.getCurrentGame(player);

                if (game != null && game.getPlayers().contains(player) && game.getState() != GameState.ENDED) {
                    gameScoreGetter.accept(player, scores);
                } else {
                    lobbyScoreGetter.accept(player, scores);
                }
            }
        }
        scores.add("&f" + CC.SB_BAR);
    }

}