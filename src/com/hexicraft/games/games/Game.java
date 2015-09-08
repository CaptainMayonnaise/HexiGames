package com.hexicraft.games.games;

import com.hexicraft.games.HexiGames;
import com.hexicraft.games.components.Countdown;
import com.hexicraft.games.components.DetectionArea;
import com.hexicraft.games.components.GameArea;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Ollie
 * @version 1.0
 */
public abstract class Game extends Arena {

    private Countdown countdown;

    public Game(Map<String, Object> map) {
        super(map);
        getGameArea().setEntry(new DetectionArea.Action() {
            @Override
            public void run(String playerName) {
                areaEntry(playerName);
            }
        });
        getGameArea().setExit(new DetectionArea.Action() {
            @Override
            public void run(String playerName) {
                areaExit(playerName);
            }
        });
        getGameArea().setReset(new DetectionArea.Action() {
            @Override
            public void run(String playerName) {
                areaReset(playerName);
            }
        });
    }

    protected abstract void areaEntry(String playerName);
    protected abstract void areaExit(String playerName);
    protected abstract void areaReset(String playerName);

    @Override
    public void start() {
        getGameArea().setMode(GameArea.Mode.COUNTDOWN);
        for (String playerName : getPlugin().getLobby().getReadyPlayers()) {
            Bukkit.getServer().getPlayer(playerName).teleport(getTeleportLocation());
        }
        HexiGames.broadcast(getName() + " starting now! You have 30 seconds to type /joingame!");

        countdown = new Countdown(this, 30, new Runnable() {
            @Override
            public void run() {
                getGameArea().setMode(GameArea.Mode.GAME);
                gameStart();
                countdown = null;
                HexiGames.broadcast(getName() + " has started! Type /joingame to spectate!");
            }
        });

    }

    protected abstract void gameStart();

    @Override
    public void tick() {
        if (getGameArea().getPlayers().size() == 0) {
            win(new ArrayList<String>());
        }
    }

    @Override
    public void stop() {
        if (countdown != null) {
            countdown.interrupt();
        }
        getGameArea().setMode(GameArea.Mode.INACTIVE);
        gameStop();
    }

    protected abstract void gameStop();

    public void win(ArrayList<String> players) {
        if (players.size() == 0) {
            announceMessage("An error has occurred, ending game prematurely.");
        } else if (players.size() == 1) {
            HexiGames.broadcast(players.get(0) + " has received $1000 for winning " + getName() + ".");
            getPlugin().getEcon().depositPlayer(Bukkit.getServer().getPlayer(players.get(0)), 1000);
        } else {
            String message = "";
            double amount = 1000 / players.size();
            for (int i = 0; i < players.size(); i++) {
                if (i == 0) {
                    message += players.get(i);
                } else if (i == players.size() - 1) {
                    message += " and " + players.get(i);
                } else {
                    message += ", " + players.get(i);
                }
                getPlugin().getEcon().depositPlayer(Bukkit.getServer().getPlayer(players.get(i)), amount);
            }
            message += " have each received $" + (int) (amount + 0.5d) + " for winning " + getName() + ".";
            HexiGames.broadcast(message);
        }
        getPlugin().returnToLobby();
    }

    public Countdown getCountdown() {
        return countdown;
    }
}
