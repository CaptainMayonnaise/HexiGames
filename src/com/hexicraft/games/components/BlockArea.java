package com.hexicraft.games.components;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Map;

/**
 * @author Ollie
 * @version 1.0
 */
public class BlockArea extends Area {

    public BlockArea(Location lowPoint, Location highPoint) {
        super(lowPoint, highPoint);
    }

    @SuppressWarnings("unused")
    public BlockArea(Map<String, Object> map) {
        super(map);
    }

    @SuppressWarnings("deprecation")
    public void setBlock(Material material, int data) {
        if (getLowPoint().getWorld() == getHighPoint().getWorld() &&
                getHighPoint().getBlockX() -getLowPoint().getBlockX() < 100 &&
                getHighPoint().getBlockY() -getLowPoint().getBlockY() < 100 &&
                getHighPoint().getBlockZ() -getLowPoint().getBlockZ() < 100) {
            for (int x = getLowPoint().getBlockX(); x <= getHighPoint().getBlockX(); x++) {
                for (int y = getLowPoint().getBlockY(); y <= getHighPoint().getBlockY(); y++) {
                    for (int z = getLowPoint().getBlockZ(); z <= getHighPoint().getBlockZ(); z++) {
                        Block block = getLowPoint().getWorld().getBlockAt(x, y, z);
                        block.setType(material);
                        block.setData((byte) data);
                    }
                }
            }
        }
    }
}
