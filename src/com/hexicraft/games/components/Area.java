package com.hexicraft.games.components;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ollie
 * @version 1.0
 */
public abstract class Area implements ConfigurationSerializable {

    private Location lowPoint;
    private Location highPoint;

    public Area(Location lowPoint, Location highPoint) {
        this.lowPoint = lowPoint;
        this.highPoint = highPoint;
    }

    public Location getLowPoint() {
        return lowPoint;
    }

    public Location getHighPoint() {
        return highPoint;
    }

    public Area(Map<String, Object> map) {
        lowPoint = (Location) map.get("lowPoint");
        highPoint = (Location) map.get("highPoint");
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("lowPoint", lowPoint);
        map.put("highPoint", highPoint);
        return map;
    }

    /*public ReturnCode setup(String[] args) {
        if (args.length <= 6) {
            return ReturnCode.TOO_FEW_ARGUMENTS;
        } else if (plugin.getServer().getWorld(args[0]) == null ||
                !HexiGames.isInteger(args[1]) ||
                !HexiGames.isInteger(args[2]) ||
                !HexiGames.isInteger(args[3]) ||
                !HexiGames.isInteger(args[4]) ||
                !HexiGames.isInteger(args[5]) ||
                !HexiGames.isInteger(args[6])) {
            return ReturnCode.INVALID_ARGUMENT;
        } else {
            World world = plugin.getServer().getWorld(args[0]);
            double[] xPos = order(Integer.parseInt(args[1]), Integer.parseInt(args[4]));
            double[] yPos = order(Integer.parseInt(args[2]), Integer.parseInt(args[5]));
            double[] zPos = order(Integer.parseInt(args[3]), Integer.parseInt(args[6]));

            lowPoint = new Location(world, xPos[0], yPos[0], zPos[0]);
            highPoint = new Location(world, xPos[1], yPos[1], zPos[1]);
            return ReturnCode.SUCCESS;
        }
    }

    public double[] order(double low, double high) {
        if (low > high) {
            double temp = low;
            low = high;
            high = temp;
        }
        return new double[]{low, high};
    }*/
}
