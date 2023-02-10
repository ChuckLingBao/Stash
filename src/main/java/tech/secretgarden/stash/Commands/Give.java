package tech.secretgarden.stash.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Give {
    public boolean run(CommandSender sender, String[] args, ItemStack item, String itemName) {
        Player player = null;
        String giver = null;

        if (sender instanceof Player) {
            player = (Player) sender;
            if (!player.hasPermission("stash.a")) {
                player.sendMessage(ChatColor.RED + "you don't have permission");
                return false;
            }
            giver = player.getName();
        } else {
            giver = "Console";
        }
        if (args[0].equals("give")) {

                //Handle too many args
                if (args[1].equalsIgnoreCase("time")) {
                    if (args.length > 5) {
                        if (player != null) {
                            player.sendMessage(ChatColor.RED + "Too many args:");
                            player.sendMessage(ChatColor.RED + "Stash give time <item_name> [quantity] <amount_of_time>");
                        }
                        Bukkit.getLogger().warning("Too many args:");
                        Bukkit.getLogger().warning("Stash give time <item_name> [quantity] <amount_of_time>");
                        return false;
                    }
                } else {
                    if (args.length > 4) {
                        if (player != null) {
                            player.sendMessage(ChatColor.RED + "Too many args:");
                            player.sendMessage(ChatColor.RED + "Stash give <all/player> <item_name> [quantity]");
                        }
                        Bukkit.getLogger().warning("Too many args:");
                        Bukkit.getLogger().warning("Stash give <all/player> <item_name> [quantity]");
                        return false;
                    }
                }

                StashCmdContents contents = new StashCmdContents(giver, item, itemName, args);
                if (contents.error != null) {
                    Bukkit.getLogger().warning(contents.error);
                    return false;
                }
                ReceiverList receiverList = new ReceiverList(contents);
                if (contents.getQuantity() == 0) {
                    if (player != null) {
                        player.sendMessage(ChatColor.RED + "Quantity is invalid");
                    }
                    Bukkit.getLogger().warning("Quantity is invalid");
                    return false;
                }

                receiverList.addItem();

        } else {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Command is not valid: stash give ...");
            } else {
                Bukkit.getLogger().warning("Command is not valid: stash give ...");
            }
        }
        return false;
    }
}
