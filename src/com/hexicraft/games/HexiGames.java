package com.hexicraft.games;

import com.earth2me.essentials.Essentials;
import com.hexicraft.games.commands.GamesCommand;
import com.hexicraft.games.commands.JoinGameCommand;
import com.hexicraft.games.components.BlockArea;
import com.hexicraft.games.components.DetectionArea;
import com.hexicraft.games.components.GameArea;
import com.hexicraft.games.components.GameChest;
import com.hexicraft.games.games.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Ollie
 * @version 1.0
 */
public class HexiGames extends JavaPlugin {

    private Economy econ;
    private Essentials essentials;
    private HashMap<String, LinkedBlockingQueue<Game>> gameMap;
    private LinkedBlockingQueue<LinkedBlockingQueue<Game>> gameQueue;
    private Lobby lobby;

    private Arena activeArena;

    public Game cycleGame() {
        LinkedBlockingQueue<Game> queue = gameQueue.poll();
        if (queue == null) {
            return null;
        } else {
            Game game = queue.poll();
            queue.offer(game);
            gameQueue.offer(queue);
            return game;
        }
    }

    @Override
    public void onDisable() {
        activeArena.stop();
    }

    @Override
    public void onEnable() {
        getCommand("games").setExecutor(new GamesCommand(this));
        getCommand("joingame").setExecutor(new JoinGameCommand(this));

        ConfigurationSerialization.registerClass(GameChest.class);
        ConfigurationSerialization.registerClass(BlockArea.class);
        ConfigurationSerialization.registerClass(DetectionArea.class);
        ConfigurationSerialization.registerClass(GameArea.class);
        ConfigurationSerialization.registerClass(TreeGame.class);
        ConfigurationSerialization.registerClass(Ghost.class);
        ConfigurationSerialization.registerClass(OneInTheQuiver.class);
        ConfigurationSerialization.registerClass(Lobby.class);

        reload();
    }

    public void reload() {
        if (!setupEconomy()) {
            getLogger().severe("Missing dependency: Vault and/or compatible economy plugin.");
            return;
        }

        essentials = Essentials.getPlugin(Essentials.class);

        setupGames();
        setupClock();
    }

    /**
     * Sets the economy plugin
     * @return True if Vault is found, false otherwise
     */
    private boolean setupEconomy() {
        econ = null;
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        econ = rsp.getProvider();
        return econ != null;
    }

    @SuppressWarnings("unchecked")
    public void setupGames() {
        YamlFile savedGames = new YamlFile(this, "savedGames.yml", false);

        lobby = (Lobby) savedGames.get("lobby");
        lobby.setPlugin(this);

        gameMap = new HashMap<>();
        gameMap.put("treegame", new LinkedBlockingQueue<>(
                (Collection<Game>) savedGames.getList("games.treegame")));
        gameMap.put("ghost", new LinkedBlockingQueue<>(
                (Collection<Game>) savedGames.getList("games.ghost")));
        gameMap.put("oneInTheQuiver", new LinkedBlockingQueue<>(
                (Collection<Game>) savedGames.getList("games.oneInTheQuiver")));
        gameQueue = new LinkedBlockingQueue<>(gameMap.values());

        activateGames();
        returnToLobby();
    }

    private void activateGames() {
        for (LinkedBlockingQueue<Game> queue : gameQueue) {
            for (Game game : queue) {
                game.setPlugin(this);
            }
        }
    }

    public void returnToLobby() {
        setActiveArena(lobby);
    }

    public void setActiveArena(Arena arena) {
        if (activeArena != null) {
            activeArena.stop();
        }
        activeArena = arena;
        activeArena.start();
    }

    public Arena getActiveArena() {
        return activeArena;
    }

    private void setupClock() {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                activeArena.tick();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.MINUTE, 1);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long delay = (calendar.getTimeInMillis() - System.currentTimeMillis()) / 40;
                Bukkit.getServer().getScheduler().runTaskLater(HexiGames.this, this, delay);
            }
        };
        Bukkit.getServer().getScheduler().runTask(this, run);
    }

    public Lobby getLobby() {
        return lobby;
    }

    public Economy getEcon() {
        return econ;
    }

    public Essentials getEssentials() {
        return essentials;
    }

    public static void broadcast(String message) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            sendMessage(player, message);
        }
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "HEXIGAMES: " + ChatColor.GRAY + message);
    }

    public static void sendTitle(String title, String subTitle, String player) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                "title " + player + " times 0 25 10");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                "title " + player + " subtitle {text:\"" + subTitle + "\",color:green}");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                "title " + player + " title {text:\"" + title + "\",color:gray}");
    }

    /**
     * Source: http://stackoverflow.com/questions/237159/whats-the-best-way-to-check-to-see-if-a-string-represents-an-integer-in-java
     * @param str String to be tested.
     * @return Whether or not the string is an integer.
     */
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    // TODO: Allow people to determine the next game
}
