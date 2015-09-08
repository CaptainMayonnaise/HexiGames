package com.hexicraft.games.components;

import com.hexicraft.games.HexiGames;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Entry is used when entering in countdown mode
 * Exit is used when removed from game
 * @author Ollie
 * @version 1.0
 */
public class GameArea extends DetectionArea {

    private Mode mode = Mode.INACTIVE;
    private Action reset = new Action() {
        @Override
        public void run(String playerName) {
            // Do nothing by default.
        }
    };
    private Action playerDeath = new Action() {
        @Override
        public void run(String playerName) {
            Player player = Bukkit.getServer().getPlayer(playerName);
            // Make spectator
            loseGame(playerName);
            // Drop the player's items on the floor and clear their inventory
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getData().getItemType() != Material.AIR) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
            }
            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (item != null && item.getData().getItemType() != Material.AIR) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
            }
            player.getInventory().clear();
        }
    };
    private ArrayList<String> spectators = new ArrayList<>();
    private GameScoreboard scoreboard = new GameScoreboard("HexiGames", "Play Hexicraft games!");

    public GameArea(Map<String, Object> map) {
        super(map);
    }

    private void addSpectator(String playerName) {
        spectators.add(playerName);
        Player player = Bukkit.getServer().getPlayer(playerName);
        player.setGameMode(GameMode.SPECTATOR);
        HexiGames.sendMessage(player, "You're now spectating!");
    }

    private void removeSpectator(String playerName) {
        if (spectators.contains(playerName)) {
            Player player = Bukkit.getServer().getPlayer(playerName);
            player.setGameMode(GameMode.SURVIVAL);
            HexiGames.sendMessage(player, "You're no longer spectating.");
            spectators.remove(playerName);
        }
    }

    @Override
    protected void addPlayer(String playerName) {
        Player player = Bukkit.getServer().getPlayer(playerName);
        player.setScoreboard(scoreboard.getScoreboard());
        switch (mode) {
            case GAME:
                addSpectator(playerName);
                break;
            case COUNTDOWN:
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(20);
                player.setMaxHealth(20);
                player.setFoodLevel(20);
                player.getInventory().clear();
                player.getEnderChest().clear();
                super.addPlayer(playerName);
                break;
            case INACTIVE:
                addSpectator(playerName);
                break;
        }
    }

    @Override
    protected void removePlayer(String playerName) {
        Player player = Bukkit.getServer().getPlayer(playerName);
        if (Objects.equals(player.getScoreboard(), scoreboard.getScoreboard())) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
        removeSpectator(playerName);
        scoreboard.remove(playerName);
        reset.run(playerName);
        super.removePlayer(playerName);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void entityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            String playerName = player.getName();
            if (getPlayers().contains(playerName) && (player.getHealth() - event.getFinalDamage()) <= 0) {
                // Make damage low
                event.setDamage(1);
                playerDeath.run(playerName);
            }
        }
    }

    public void loseGame(String playerName) {
        super.removePlayer(playerName);
        playerToSpectator(playerName);
    }

    private void playerToSpectator(String playerName) {
        // Ensure they are removed from game
        getPlayers().remove(playerName);
        // Reset the player
        reset.run(playerName);
        scoreboard.remove(playerName);
        Player player = Bukkit.getServer().getPlayer(playerName);
        player.setHealth(20);
        player.setFoodLevel(20);
        // Add to spectators
        addSpectator(playerName);
    }

    public void spectatorToPlayer(String playerName) {
        if (spectators.contains(playerName)) {
            removeSpectator(playerName);
            addPlayer(playerName);
        }
    }

    @Override
    public ArrayList<String> getAllPlayers() {
        Set<String> playerSet = new LinkedHashSet<>(getPlayers());
        playerSet.addAll(spectators);
        return new ArrayList<>(playerSet);
    }

    public void setMode(Mode mode) {
        switch (mode) {
            case GAME:
                for (String playerName : getPlayers()) {
                    scoreboard.add(playerName);
                }
                this.mode = mode;
                break;
            case COUNTDOWN:
                if (this.mode == Mode.INACTIVE) {
                    this.mode = mode;
                }
                break;
            case INACTIVE:
                while (!getPlayers().isEmpty()) {
                    playerToSpectator(getPlayers().remove(0));
                }
                this.mode = mode;
                break;
        }
    }

    public Mode getMode() {
        return mode;
    }

    public enum Mode {
        GAME, COUNTDOWN, INACTIVE
    }

    public void setReset(Action reset) {
        this.reset = reset;
    }

    public void setPlayerDeath(Action playerDeath) {
        this.playerDeath = playerDeath;
    }

    public GameScoreboard getScoreboard() {
        return scoreboard;
    }
}
