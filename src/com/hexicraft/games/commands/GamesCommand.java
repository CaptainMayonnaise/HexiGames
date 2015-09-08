package com.hexicraft.games.commands;

import com.hexicraft.games.HexiGames;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Objects;

/**
 * @author Ollie
 * @version 1.0
 */
public class GamesCommand implements CommandExecutor {

    private HexiGames plugin;

    public GamesCommand(HexiGames plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                switch (args[0]) {
                    case "join":
                        player.teleport(plugin.getActiveArena().getTeleportLocation());
                        break;
                    case "start":
                        if (player.hasPermission("hexigames.admin")) {
                            if (Objects.equals(plugin.getActiveArena(), plugin.getLobby())) {
                                plugin.getLobby().startNextGame();
                            }
                        }
                        break;
                    case "stop":
                        if (player.hasPermission("hexigames.admin")) {
                            plugin.returnToLobby();
                        }
                        break;
                    case "reload":
                        if (player.hasPermission("hexigames.admin")) {
                            plugin.reload();
                        }
                        break;
                    case "poo":
                        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                        Objective objective = scoreboard.registerNewObjective("tester", "dummy");
                        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                        objective.setDisplayName("narnar");
                        player.setScoreboard(scoreboard);
                        break;
                    default:
                        break;
                }
            }
        }
        return true;
    }
}
