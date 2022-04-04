package net.frozenorb.potpvp.scoreboard;


import net.frozenorb.potpvp.scoreboard.config.ScoreboardConfiguration;
import net.frozenorb.potpvp.scoreboard.construct.TitleGetter;
import org.apache.commons.lang.StringEscapeUtils;

public final class PotPvPScoreboardConfiguration {

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration scoreboardConfiguration = new ScoreboardConfiguration();
        scoreboardConfiguration.setTitleGetter(
                new TitleGetter("&6&lMineHQ &7" + StringEscapeUtils.unescapeJava("\u2758") +" &fPractice"));
        scoreboardConfiguration.setScoreGetter(
                new MultiplexingScoreGetter(
                new MatchScoreGetter(),
                new LobbyScoreGetter(),
                new GameScoreGetter()
        ));
        return scoreboardConfiguration;
    }

}
