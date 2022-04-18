package net.frozenorb.potpvp.util.scoreboard.construct;

import org.bukkit.entity.Player;

import java.util.List;

public interface ScoreGetter {

    void getScores(List<String> linkedList, Player player);

}