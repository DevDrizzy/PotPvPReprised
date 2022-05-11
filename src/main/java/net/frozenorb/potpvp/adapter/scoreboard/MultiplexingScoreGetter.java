package net.frozenorb.potpvp.adapter.scoreboard;

import java.util.List;
import java.util.function.BiConsumer;

import net.frozenorb.potpvp.events.Game;
import net.frozenorb.potpvp.events.GameHandler;
import net.frozenorb.potpvp.events.GameState;
import net.frozenorb.potpvp.util.scoreboard.construct.ScoreGetter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.profile.setting.Setting;
import net.frozenorb.potpvp.profile.setting.SettingHandler;
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
             scores.add(CC.SB_BAR);
            if (matchHandler.isPlayingOrSpectatingMatch(player)) {
                matchScoreGetter.accept(player, scores);
            } else {
                Game game = PotPvPRP.getInstance().getGameHandler().getCurrentGame(player);

                if (game != null && game.getPlayers().contains(player) && game.getState() != GameState.ENDED) {
                    gameScoreGetter.accept(player, scores);
                } else {
                    lobbyScoreGetter.accept(player, scores);
                }
            }
            scores.add("");
            scores.add("&7test.refinedev.xyz");
            if (player.hasMetadata("ModMode")) {
                scores.add(ChatColor.GRAY.toString() + ChatColor.BOLD + "In Silent Mode");
            }
            scores.add(CC.SB_BAR);
        }
    }

}