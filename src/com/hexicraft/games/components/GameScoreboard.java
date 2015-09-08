package com.hexicraft.games.components;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * @author Ollie
 * @version 1.0
 */
public class GameScoreboard {

    private Scoreboard scoreboard;
    private Objective objective;
    private String description = "";

    public GameScoreboard(String name, String description) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective(name, "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        setDisplayName(name);
        setDescription(description);
    }

    public void add(String entry) {
        add(entry, 0);
    }

    public void increment(String entry) {
        add(entry, objective.getScore(entry).getScore() + 1);
    }

    public void decrement(String entry) {
        add(entry, objective.getScore(entry).getScore() - 1);
    }

    public void add(String entry, int score) {
        objective.getScore(entry).setScore(score);
    }

    public void remove(String entry) {
        scoreboard.resetScores(entry);
    }

    public Score getScore(String entry) {
        return objective.getScore(entry);
    }

    public void setDisplayName(String name) {
        objective.setDisplayName(
                "  " + ChatColor.GREEN + "⬢" +
                        ChatColor.GRAY + ChatColor.BOLD + " " + name.toUpperCase() + " " +
                        ChatColor.GREEN + "⬢"
        );
    }

    public void setDescription(String description) {
        remove(this.description);
        description = ChatColor.GRAY + description;
        add(description, 999);
        this.description = description;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
