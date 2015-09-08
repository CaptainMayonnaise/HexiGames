package com.hexicraft.games.games;

import com.hexicraft.games.HexiGames;
import com.hexicraft.games.components.BlockArea;
import com.hexicraft.games.components.GameChest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * @author Ollie
 * @version 1.0
 */
public class Ghost extends Game implements Listener {

    private BlockArea gate;
    private Location ghostLocation;
    private ArrayList<GameChest> chests;
    private String ghostName = "";

    @SuppressWarnings("unchecked")
    public Ghost(Map<String, Object> map) {
        super(map);
        gate = (BlockArea) map.get("gate");
        ghostLocation = (Location) map.get("ghostLocation");
        chests = (ArrayList<GameChest>) map.get("chests");

        getGameArea().getScoreboard().setDescription("Kill the invisible player!");
        gameStop();
    }

    @Override
    protected void areaEntry(String playerName) {
        // Do nothing
    }

    @Override
    protected void areaExit(String playerName) {
        announceTitle("", playerName + " was slain!");
        if (Objects.equals(playerName, ghostName)) {
            win(getGameArea().getPlayers());
        } else if (getGameArea().getPlayers().size() == 1) {
            win(getGameArea().getPlayers());
        }
    }

    @Override
    protected void areaReset(String playerName) {
        Bukkit.getServer().getPlayer(playerName).removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("gate", gate);
        map.put("ghostLocation", ghostLocation);
        map.put("chests", chests);
        return map;
    }

    @Override
    protected void gameStart() {
        for (GameChest chest : chests) {
            chest.resetChest();
        }
        ArrayList<String> players = getGameArea().getPlayers();
        gate.setBlock(Material.AIR, 0);
        Random random = new Random();
        ghostName = players.get(random.nextInt(players.size()));
        announceTitle("", ghostName + " is the ghost!");
        Player ghost = Bukkit.getServer().getPlayer(ghostName);
        ghost.teleport(ghostLocation);
        ghost.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000, 1));
        ghost.getInventory().addItem(new MaterialData(Material.STONE_SWORD).toItemStack(1));
        ghost.getInventory().addItem(new MaterialData(Material.COOKED_BEEF).toItemStack(8));
    }

    @Override
    protected void gameStop() {
        gate.setBlock(Material.FENCE, 0);
    }

    @Override
    public void setPlugin(HexiGames plugin) {
        super.setPlugin(plugin);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        String attackedName = event.getEntity().getName();
        String attackerName = event.getDamager().getName();
        if (getGameArea().getPlayers().contains(attackedName) &&
                getGameArea().getPlayers().contains(attackerName) &&
                ((!Objects.equals(attackedName, ghostName) &&
                !Objects.equals(attackerName, ghostName)) ||
                getCountdown() != null)) {
            event.setCancelled(true);
        }
    }
}
