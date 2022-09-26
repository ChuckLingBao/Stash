package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StashCommand implements CommandExecutor {

    private final GiveMethods giveMethods = new GiveMethods();
    private final GetMethods getMethods = new GetMethods();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            String world = player.getWorld().getName();
            if (getMethods.getWorld(world)) {
                Inventory senderStash = MapConversion.map.get(player.getUniqueId().toString());
                //opens your own stash
                if (args.length == 0) {
                    player.openInventory(senderStash);
                    return false;
                }
            } else {
                player.sendMessage(ChatColor.RED + "You cannot do that in this world!");
            }
            if (args.length == 1 && player.hasPermission("stash.a")) {
                //opens a different player's stash
                String p = args[0];

                String idString = getMethods.getIdString(p);
                if (idString == null) {
                    player.sendMessage(ChatColor.RED + "This is not a valid player!");
                    return false;
                } else {
                    Inventory otherStash = MapConversion.map.get(idString);
                    player.openInventory(otherStash);
                    return false;
                }
            }
        }
        //GIVE ARGS
        Player player = null;
        String giver = null;

        if(sender instanceof Player) {
            player = (Player) sender;
            giver = player.getName();
            if (!player.hasPermission("stash.a")) {
                player.sendMessage(ChatColor.RED + "you don't have permission");
                return false;
            }
        } else {
            giver = "Console";
        }
        if (args.length >= 1) {
            if (player != null) {
                if (!player.hasPermission("stash.a")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission!");
                    return false;
                }
            }
            if (args[0].equals("give")) {
                Material mat = Material.getMaterial(args[2].toUpperCase());
                ItemStack item = new ItemStack(mat);
                String itemName = item.toString();

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

//                if (receiver.equals("all")) {
//                    //giveMethods.giveAllPlayers(args, item, giver, itemName);
//
//
//                } else if (receiver.equals("time")) {
//                    //giveMethods.giveByTime(giver, args, item, itemName, player);
//                    //BELOW IS FOR GIVING ITEMS TO A SINGLE PLAYER
//                } else {
//                    String idString = getMethods.getIdString(contents.getReceiver());
//                    if (idString == null) {
//                        player.sendMessage("This player has not logged in before.");
//                    } else {
//                        contents.setIdString(idString);
//                    }
//                }
            } else {
                if (player != null) {
                    player.sendMessage(ChatColor.RED + "Command is not valid");
                } else {
                    Bukkit.getLogger().warning("Command is not valid");
                }
            }
        }
        return false;
    }
}