package com.hexicraft.games.commands;

import com.hexicraft.games.HexiGames;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Ollie
 * @version 1.0
 */
public class JoinGameCommand implements CommandExecutor {

    private HexiGames plugin;

    public JoinGameCommand(HexiGames plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.teleport(plugin.getActiveArena().getTeleportLocation());
        }
        return true;
    }
}
