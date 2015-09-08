package com.hexicraft.games.components;

import com.hexicraft.games.HexiGames;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * @author Ollie
 * @version 1.0
 */
public class DetectionArea extends Area implements Listener {

    private Action entry = new Action() {
        @Override
        public void run(String playerName) {
            // Do nothing by default.
        }
    };
    private Action exit = new Action() {
        @Override
        public void run(String playerName) {
            // Do nothing by default.
        }
    };
    private ArrayList<String> players = new ArrayList<>();

    @SuppressWarnings("unused")
    public DetectionArea(Map<String, Object> map) {
        super(map);
    }

    public void setPlugin(HexiGames plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    protected void addPlayer(String playerName) {
        players.add(playerName);
        entry.run(playerName);
    }

    protected void removePlayer(String playerName) {
        if (players.contains(playerName)) {
            players.remove(playerName);
            exit.run(playerName);
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        if (Objects.equals(event.getTo().getWorld(), event.getFrom().getWorld())) {
            playerChangedLocation(event, event.getTo());
        }
    }

    @EventHandler
    public void playerTeleport(PlayerTeleportEvent event) {
        if (Objects.equals(event.getTo().getWorld(), event.getFrom().getWorld())) {
            playerChangedLocation(event, event.getTo());
        }
    }

    @EventHandler
    public void playerChangedWorld(PlayerChangedWorldEvent event) {
        playerChangedLocation(event, event.getPlayer().getLocation());
    }

    private void playerChangedLocation(PlayerEvent event, Location toLocation) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        boolean insideArea = insideArea(toLocation);
        boolean containsPlayer = containsPlayer(playerName);
        if (insideArea && !containsPlayer) {
            addPlayer(playerName);
        } else if (!insideArea && containsPlayer) {
            removePlayer(playerName);
        }
    }

    private boolean insideArea(Location location) {
        return location.getWorld() == getLowPoint().getWorld() &&
                location.getBlockX() >= getLowPoint().getBlockX() &&
                location.getBlockY() >= getLowPoint().getBlockY() &&
                location.getBlockZ() >= getLowPoint().getBlockZ() &&
                location.getBlockX() <= getHighPoint().getBlockX() &&
                location.getBlockY() <= getHighPoint().getBlockY() &&
                location.getBlockZ() <= getHighPoint().getBlockZ();
    }

    @EventHandler
    public void playerKick(PlayerKickEvent event) {
        playerLeftGame(event.getPlayer().getName());
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        playerLeftGame(event.getPlayer().getName());
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        playerLeftGame(event.getEntity().getName());
    }

    private void playerLeftGame(String playerName) {
        boolean containsPlayer = containsPlayer(playerName);
        if (containsPlayer) {
            removePlayer(playerName);
        }
    }

    public boolean containsPlayer(String playerName) {
        return getAllPlayers().contains(playerName);
    }

    public interface Action {
        void run(String playerName);
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public ArrayList<String> getAllPlayers() {
        return players;
    }

    public void setEntry(Action entry) {
        this.entry = entry;
    }

    public void setExit(Action exit) {
        this.exit = exit;
    }
}
