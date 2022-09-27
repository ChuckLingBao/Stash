package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.nightexpress.excellentcrates.ExcellentCrates;
import su.nightexpress.excellentcrates.ExcellentCratesAPI;
import su.nightexpress.excellentcrates.api.crate.ICrateKey;

public class StashKeyCommand implements CommandExecutor {

    private final Stash plugin;
    public StashKeyCommand(Stash instance) { this.plugin = instance; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

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
            if (plugin.getEcAPI() != null && plugin.getNeAPI() != null) {

                //Handle permissions
                if (player != null) {
                    if (!player.hasPermission("stash.a")) {
                        //Handle player's permission
                        player.sendMessage(ChatColor.RED + "You do not have permission!");
                        return false;
                    }
                }

                //Handle too many args
                if (args[1].equalsIgnoreCase("time")) {
                    if (args.length > 5) {
                        if (player != null) {
                            player.sendMessage(ChatColor.RED + "Too many args:");
                            player.sendMessage(ChatColor.RED + "Stashkey give time <item_name> [quantity] <amount_of_time>");
                        }
                        Bukkit.getLogger().warning("Too many args:");
                        Bukkit.getLogger().warning("Stashkey give time <item_name> [quantity] <amount_of_time>");
                        return false;
                    }
                } else {
                    if (args.length > 4) {
                        if (player != null) {
                            player.sendMessage(ChatColor.RED + "Too many args:");
                            player.sendMessage(ChatColor.RED + "Stashkey give <all/player> <item_name> [quantity]");
                        }
                        Bukkit.getLogger().warning("Too many args:");
                        Bukkit.getLogger().warning("Stashkey give <all/player> <item_name> [quantity]");
                        return false;
                    }
                }

                ICrateKey key = ExcellentCratesAPI.getKeyManager().getKeyById(args[2]);
//                ICrateKey key = ExcellentCrates.getInstance().getKeyManager().getKeyById(args[2]);
                ItemStack item = key.getItem();
                String itemName = item.getItemMeta().getDisplayName();

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
                    player.sendMessage(ChatColor.RED + "Cannot get GoldenCrates instance!");
                }
            }
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
