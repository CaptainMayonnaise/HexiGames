package com.hexicraft.games;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;

/**
 * @author Ollie
 * @version 1.0
 */
public enum ReturnCode {
    SUCCESS("", false),
    NOT_PLAYER("Only players can run this command.", false),
    INVALID_ARGUMENT("The arguments entered were invalid.", true),
    TOO_FEW_ARGUMENTS("You didn't enter enough arguments.", true),
    ALREADY_ACTIVE("A game is already in the process of being created.", false),
    INACTIVE("No game creation was in progress.", false),
    INVALID_LOCATION("The location you entered was not valid.", false),
    FAILURE("An undefined error has occurred, please notify an Admin", false);

    private String message;
    private boolean sendUsage;

    ReturnCode(String message, boolean sendUsage) {
        this.message = message;
        this.sendUsage = sendUsage;
    }

    /**
     * Does the code have a message
     * @return true if has a message, false if empty
     */
    public boolean hasMessage() {
        return !(message.equals(""));
    }

    /**
     * Gets the return message, along with usage if required
     * @param cmd The command that was sent
     * @return The message
     */
    public String getMessage(Command cmd) {
        return message + (sendUsage ? ("\n" + ChatColor.GOLD + "Usage: " + ChatColor.RESET + cmd.getUsage()) : "");
    }
}
