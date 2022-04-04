package net.frozenorb.potpvp.scoreboard.construct;

import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public interface ScoreGetter {

    void getScores(List<String> linkedList, Player player);

}