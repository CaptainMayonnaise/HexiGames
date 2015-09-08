package com.hexicraft.games.games;

import com.hexicraft.games.HexiGames;
import com.hexicraft.games.components.GameArea;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ollie
 * @version 1.0
 */
public abstract class Arena implements ConfigurationSerializable {

    private String name;
    private HexiGames plugin;
    private GameArea gameArea;
    private Location teleportLocation;

    public Arena(Map<String, Object> map) {
        name = (String) map.get("name");
        gameArea = (GameArea) map.get("gameArea");
        teleportLocation = (Location) map.get("teleportLocation");

        gameArea.getScoreboard().setDisplayName(name);
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("gameArea", gameArea);
        map.put("teleportLocation", teleportLocation);
        return map;
    }

    public void announceMessage(String message) {
        for (String player : gameArea.getAllPlayers()) {
            Bukkit.getServer().getPlayer(player).sendMessage(
                    ChatColor.GREEN  + "" + ChatColor.BOLD + name.toUpperCase() + " " + ChatColor.GRAY + message);
        }
    }

    public void announceTitle(String title, String subTitle) {
        for (String player : gameArea.getAllPlayers()) {
            HexiGames.sendTitle(title, subTitle, player);
        }
    }

    public abstract void start();
    public abstract void tick();
    public abstract void stop();

    public void setPlugin(HexiGames plugin) {
        this.plugin = plugin;
        gameArea.setPlugin(plugin);
    }

    public String getName() {
        return name;
    }

    public Location getTeleportLocation() {
        return teleportLocation;
    }

    public void setTeleportLocation(Location teleportLocation) {
        this.teleportLocation = teleportLocation;
    }

    public HexiGames getPlugin() {
        return plugin;
    }

    public GameArea getGameArea() {
        return gameArea;
    }
}
