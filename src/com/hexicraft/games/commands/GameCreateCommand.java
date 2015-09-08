package com.hexicraft.games.commands;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Ollie
 * @version 1.0
 */
public class GameCreateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
            Selection selection = worldEdit.getSelection(player);

            if (selection != null) {
                World world = selection.getWorld();
                Location min = selection.getMinimumPoint();
                Location max = selection.getMaximumPoint();

                // Do something with min/max
            } else {
                // No selection available
            }
        }
        return false;
    }
}
