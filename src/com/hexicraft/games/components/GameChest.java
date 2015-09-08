package com.hexicraft.games.components;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ollie
 * @version 1.0
 */
public class GameChest implements ConfigurationSerializable {

    private Location chestLocation;
    private Location backupLocation;

    @SuppressWarnings("deprecation")
    public void resetChest() {
        Block chestBlock = chestLocation.getBlock();
        Block backupBlock = backupLocation.getBlock();
        chestBlock.setType(Material.CHEST);
        if (backupBlock.getState() instanceof Chest) {
            Chest backupChest = (Chest) backupBlock.getState();
            Chest chest = (Chest) chestBlock.getState();
            chestBlock.setData(backupChest.getData().getData());
            chest.getBlockInventory().setContents(backupChest.getBlockInventory().getContents());
            chest.update();
        } else {
            System.err.println("Block at: " + backupLocation + " was not a chest.");
        }
    }

    @SuppressWarnings("unused")
    public GameChest(Map<String, Object> map) {
        chestLocation = (Location) map.get("chestLocation");
        backupLocation = (Location) map.get("backupLocation");
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("chestLocation", chestLocation);
        map.put("backupLocation", backupLocation);
        return map;
    }

    /*@SuppressWarnings("deprecation")
    public ReturnCode setup(String[] args) {
        if (args.length <= 6) {
            return ReturnCode.TOO_FEW_ARGUMENTS;
        } else if (plugin.getServer().getWorld(args[0]) == null ||
                !HexiGames.isInteger(args[1]) ||
                !HexiGames.isInteger(args[2]) ||
                !HexiGames.isInteger(args[3])) {
            return ReturnCode.INVALID_ARGUMENT;
        } else {
            World world = plugin.getServer().getWorld(args[0]);
            chestLocation = new Location(world,
                    Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]));
            Block block = chestLocation.getBlock();
            if (block instanceof Chest) {
                Chest chestBlock = (Chest) block;
                contents = chestBlock.getBlockInventory().getContents();
                direction = chestBlock.getData().getData();
                return ReturnCode.SUCCESS;
            } else {
                return ReturnCode.INVALID_LOCATION;
            }
        }
    }*/
}
