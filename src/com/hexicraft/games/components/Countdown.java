package com.hexicraft.games.components;

import com.hexicraft.games.games.Game;
import org.bukkit.Bukkit;

/**
 * @author Ollie
 * @version 1.0
 */
public class Countdown {

    private Game game;
    private int count;
    private Runnable start;
    private boolean interrupted;

    public Countdown(Game game, int count, Runnable start) {
        this.game = game;
        this.count = count;
        this.start = start;
        this.interrupted = false;

        countdown();
    }

    private void countdown() {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                if (interrupted) {
                    game.announceTitle("", "Game Starting Aborted");
                } else if (0 > count) {
                    game.announceTitle("", "Go!");
                    start.run();
                } else {
                    game.announceTitle("", String.valueOf(count));
                    Bukkit.getServer().getScheduler().runTaskLater(game.getPlugin(), this, 20);
                }
                count--;
            }
        };
        Bukkit.getServer().getScheduler().runTask(game.getPlugin(), run);
    }

    public void interrupt() {
        interrupted = true;
    }
}
