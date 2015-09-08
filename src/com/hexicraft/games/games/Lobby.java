package com.hexicraft.games.games;

import com.hexicraft.games.HexiGames;
import com.hexicraft.games.components.DetectionArea;
import com.hexicraft.games.components.GameArea;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Ollie
 * @version 1.0
 */
public class Lobby extends Arena {

    private Game nextGame;
    private Calendar startTime;
    private int round = 5;

    public static final int NO_OF_ROUNDS = 3;
    public static final int MIN_PLAYERS = 3;

    public Lobby(Map<String, Object> map) {
        super(map);
        getGameArea().setMode(GameArea.Mode.COUNTDOWN);
        getGameArea().setEntry(new DetectionArea.Action() {
            @Override
            public void run(String playerName) {
                getGameArea().getScoreboard().add(playerName);
            }
        });
    }

    @Override
    public void start() {
        nextGame = getPlugin().cycleGame();
        getGameArea().getScoreboard().setDescription("Next: " + nextGame.getName());
        round++;

        startTime = Calendar.getInstance();
        startTime.setTime(new Date());
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);
        long minutesTilStart;
        if (round > NO_OF_ROUNDS) {
            round = 1;
            startTime.add(Calendar.HOUR_OF_DAY, 1);
            startTime.set(Calendar.MINUTE, 0);
            long timeDiff = startTime.getTime().getTime() - System.currentTimeMillis();
            minutesTilStart = TimeUnit.MILLISECONDS.toMinutes(timeDiff + TimeUnit.MINUTES.toMillis(1));
        } else {
            startTime.add(Calendar.MINUTE, 2);
            minutesTilStart = 2;
        }

        HexiGames.broadcast("Round " + round + " of " + NO_OF_ROUNDS + " is " + nextGame.getName() +
                " and will be played in " + minutesTilStart + " minutes." +
                (minutesTilStart == 2 ? " Type /joingame to join!" : ""));
    }

    @Override
    public void tick() {
            if (minutesInFuture(0).after(startTime)) {
                startNextGame();
            } else if (minutesInFuture(1).after(startTime) && minutesInFuture(0).before(startTime)) {
                broadcastCountdown(1);
            } else if (minutesInFuture(2).after(startTime) && minutesInFuture(1).before(startTime) &&
                    round == 1) {
                broadcastCountdown(2);
            } else if (minutesInFuture(5).after(startTime) && minutesInFuture(4).before(startTime)) {
                broadcastCountdown(5);
            } else if (minutesInFuture(10).after(startTime) && minutesInFuture(9).before(startTime)) {
                broadcastCountdown(10);
            }
    }

    private Calendar minutesInFuture(int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, minutes);
        return calendar;
    }

    public void startNextGame() {
        if (getReadyPlayers().size() >= MIN_PLAYERS) {
            getPlugin().setActiveArena(nextGame);
        } else {
            HexiGames.broadcast("There weren't enough players in the Lobby to start the game. :(");
            start();
        }
    }

    private void broadcastCountdown(int minutes) {
        int playerNumber = getReadyPlayers().size();
        String message = nextGame.getName() + " will be played in " +
                plural(minutes, "minute") + "! Type /joingame to join. " +
                plural(playerNumber, "player") + " in lobby" +
                (playerNumber < MIN_PLAYERS ? ", " + (MIN_PLAYERS - playerNumber) + " more needed to play." : ".");
        HexiGames.broadcast(message);
    }

    public ArrayList<String> getReadyPlayers() {
        ArrayList<String> readyPlayers = new ArrayList<>();
        for (String playerName : getGameArea().getAllPlayers()) {
            if (!getPlugin().getEssentials().getUser(playerName).isAfk()) {
                readyPlayers.add(playerName);
            }
        }
        return readyPlayers;
    }

    private String plural(int number, String word) {
        return number + " " + word + (number == 1 ? "" : "s");
    }

    @Override
    public void stop() {
        // Do nothing
    }
}
