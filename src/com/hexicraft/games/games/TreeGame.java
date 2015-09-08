package com.hexicraft.games.games;

import com.hexicraft.games.HexiGames;
import com.hexicraft.games.components.BlockArea;
import com.hexicraft.games.components.DetectionArea;
import com.hexicraft.games.components.GameArea;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

/**
 * @author Ollie
 * @version 1.0
 */
public class TreeGame extends Game implements Listener {

    private BlockArea leaves;
    private BlockArea logs;
    private DetectionArea standingArea;
    private Type type;

    public TreeGame(Map<String, Object> map) {
        super(map);
        leaves = (BlockArea) map.get("leaves");
        logs = (BlockArea) map.get("logs");
        standingArea = (DetectionArea) map.get("standingArea");
        type = Type.valueOf((String) map.get("type"));

        getGameArea().getScoreboard().setDescription("Don't fall down!");
        standingArea.setExit(new DetectionArea.Action() {
            @Override
            public void run(String playerName) {
                getGameArea().loseGame(playerName);
            }
        });

        gameStop();
    }

    @Override
    protected void areaEntry(String playerName) {
        // Do nothing
    }

    @Override
    protected void areaExit(String playerName) {
        announceTitle("", playerName + " has fallen!");
        if (getGameArea().getPlayers().size() == 1) {
            win(getGameArea().getPlayers());
        }
    }

    @Override
    protected void areaReset(String playerName) {
        Player player = Bukkit.getServer().getPlayer(playerName);
        player.removePotionEffect(PotionEffectType.JUMP);
        player.setWalkSpeed(0.2F);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("leaves", leaves);
        map.put("logs", logs);
        map.put("standingArea", standingArea);
        map.put("type", type.name());
        return map;
    }

    @Override
    public void gameStart() {
        if (type == Type.NOMOVE) {
            for (String player : getGameArea().getPlayers()) {
                Bukkit.getServer().getPlayer(player).setWalkSpeed(0.0F);
            }
        }
        if (type == Type.NOJUMP || type == Type.NOMOVE) {
            for (String player : getGameArea().getPlayers()) {
                Bukkit.getServer().getPlayer(player).addPotionEffect(
                        new PotionEffect(PotionEffectType.JUMP, 10000, 128));
            }
        }
        logs.setBlock(Material.AIR, 0);
    }

    @Override
    public void gameStop() {
        leaves.setBlock(Material.LEAVES, 0);
        logs.setBlock(Material.LOG, 0);
    }

    @Override
    public void setPlugin(HexiGames plugin) {
        super.setPlugin(plugin);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        Location from = event.getFrom();
        if (getGameArea().getMode() == GameArea.Mode.GAME &&
                type == Type.NOMOVE &&
                getGameArea().containsPlayer(event.getPlayer().getName()) &&
                (to.getX() != from.getX() ||
                to.getZ() != from.getZ()) &&
                to.getY() >= from.getY()) {
            to.setX(from.getX());
            to.setZ(from.getZ());
        }
    }

    public enum Type {
        CLASSIC, NOJUMP, NOMOVE
    }
}
