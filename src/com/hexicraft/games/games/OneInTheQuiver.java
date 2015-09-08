package com.hexicraft.games.games;

import com.hexicraft.games.HexiGames;
import com.hexicraft.games.components.DetectionArea;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.MaterialData;

import java.util.*;

/**
 * @author Ollie
 * @version 1.0
 */
public class OneInTheQuiver extends Game implements Listener {

    private HashMap<String, Integer> lives = new HashMap<>();
    private ArrayList<Location> spawns;

    @SuppressWarnings("unchecked")
    public OneInTheQuiver(Map<String, Object> map) {
        super(map);
        spawns = (ArrayList<Location>) map.get("spawns");

        getGameArea().getScoreboard().setDescription("Use your arrow wisely!");

        // When a player dies they respawn with full health and hunger as well as an arrow.
        getGameArea().setPlayerDeath(new DetectionArea.Action() {
            @Override
            public void run(String playerName) {
                Player player = Bukkit.getServer().getPlayer(playerName);

                lives.put(playerName, lives.get(playerName) - 1);
                getGameArea().getScoreboard().decrement(playerName);

                if (lives.get(playerName) > 0) {
                    player.teleport(getTeleportLocation());
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    Bukkit.getServer().getPlayer(playerName).getInventory()
                            .setItem(1, new MaterialData(Material.ARROW).toItemStack(1));
                } else {
                    getGameArea().loseGame(playerName);
                }
            }
        });
        gameStop();
    }

    /**
     * Gives the player an arrow
     * @param playerName The player to receive the arrow.
     */
    private void giveArrow(String playerName) {
        Bukkit.getServer().getPlayer(playerName).getInventory()
                .addItem(new MaterialData(Material.ARROW).toItemStack(1));
    }

    /**
     * Give players who enter a bow in slot 0.
     * @param playerName The player who entered.
     */
    @Override
    protected void areaEntry(String playerName) {
        Inventory playerInventory = Bukkit.getServer().getPlayer(playerName).getInventory();
        playerInventory.setItem(0, new MaterialData(Material.BOW).toItemStack(1));
    }

    @Override
    protected void areaExit(String playerName) {
        announceTitle("", playerName + " has died!");
        if (getGameArea().getPlayers().size() == 1) {
            win(getGameArea().getPlayers());
        }
    }

    @Override
    protected void areaReset(String playerName) {
        lives.remove(playerName);
    }

    /**
     * When the game starts, all players are given an arrow and 3 lives.
     */
    @Override
    protected void gameStart() {
        for (String playerName : getGameArea().getPlayers()) {
            giveArrow(playerName);
            lives.put(playerName, 3);
            getGameArea().getScoreboard().add(playerName, 3);
        }
    }

    @Override
    protected void gameStop() {
        lives.clear();
    }

    /**
     * Sets the plugin for the superclass and registers this object as Listener.
     * @param plugin The HexiGames object.
     */
    @Override
    public void setPlugin(HexiGames plugin) {
        super.setPlugin(plugin);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Listens for entity damage by entity events, checks they are between two players in the game and then modifies
     * them based on the game state and the type of attack.
     * @param event The event.
     */
    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        String entityName = event.getEntity().getName();
        String damagerName = getDamagerName(event);

        if (getGameArea().getPlayers().contains(entityName) &&
                getGameArea().getPlayers().contains(damagerName)) {
            if (getCountdown() != null) {
                // No PvP before game starts
                event.setCancelled(true);
            } else if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                event.setDamage(20);
                // Give arrow if it wasn't suicide
                if (!Objects.equals(damagerName, entityName)) {
                    giveArrow(damagerName);
                }
            } else if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                event.setDamage(12);
                // Give arrow if the player will die
                if (event.getEntity() instanceof Player &&
                        event.getFinalDamage() > ((Player) event.getEntity()).getHealth()) {
                    giveArrow(damagerName);
                }
            }
        }
    }

    /**
     * Get damager player's name, requires checking to see if the damager was an arrow and getting the shooter.
     * @param event The entity damage event.
     * @return The damager player's name.
     */
    private String getDamagerName(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow && (((Arrow) event.getDamager()).getShooter() instanceof Player)) {
            return ((Player) ((Arrow) event.getDamager()).getShooter()).getName();
        } else {
            return event.getDamager().getName();
        }
    }

    @EventHandler
    public void projectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player &&
                getGameArea().getPlayers().contains(((Player) event.getEntity().getShooter()).getName())) {
            event.getEntity().remove();
        }
    }

    /**
     * Listens for food level change events, if the player was in the game, set food level to full. Ensures that players
     * in the game remain on full food.
     * @param event The event.
     */
    @EventHandler
    public void foodLevelChange(FoodLevelChangeEvent event) {
        if (getGameArea().getPlayers().contains(event.getEntity().getName())) {
            event.setFoodLevel(20);
        }
    }

    @Override
    public Location getTeleportLocation() {
        return spawns.get(new Random().nextInt(spawns.size()));
    }
}
