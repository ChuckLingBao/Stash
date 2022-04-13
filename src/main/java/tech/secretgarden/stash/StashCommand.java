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
                String p = args[0];

                if (Bukkit.getPlayer(p) != null) {
                    String idString = Bukkit.getPlayer(p).getUniqueId().toString();
                    Inventory otherStash = MapConversion.map.get(idString);
                    player.openInventory(otherStash);
                    return false;
                } else if (Bukkit.getOfflinePlayer(p).hasPlayedBefore()) {
                    String id = Bukkit.getOfflinePlayer(p).getUniqueId().toString();
                    Inventory otherStash = MapConversion.map.get(id);
                    player.openInventory(otherStash);
                    return false;
                } else {
                    player.sendMessage(ChatColor.RED + "This is not a valid player!");
                    return false;
                }
            }
        }
        //GIVE ARGS
        Player player = null;
        String giver;

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
            System.out.println("args greater than 0");
            if (args[0].equals("give")) {
                Material mat = Material.getMaterial(args[2].toUpperCase());
                ItemStack item = new ItemStack(mat);
                String itemName = item.toString();

                //ALL ARG
                if (args[1].equals("all")) {
                    giveMethods.giveAllPlayers(args, item, giver, itemName);

                    //give by time selection
                } else if (args[1].equals("time")) {
                    giveMethods.giveByTime(giver, args, item, itemName, player);

                    //BELOW IS FOR GIVING ITEMS TO A SINGLE PLAYER

                } else {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        String idString = target.getUniqueId().toString();
                        System.out.println(idString);
                        giveMethods.giveSinglePlayer(args, item, giver, idString, itemName);
                    } else {
                        //target == null
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                        if (offlinePlayer.hasPlayedBefore()) {
                            String idString = offlinePlayer.getUniqueId().toString();
                            giveMethods.giveSinglePlayer(args, item, giver, idString, itemName);
                        } else {
                            if (player != null) {
                                player.sendMessage("This player has not logged in before.");
                            }
                        }
                    }
                }
            } else {
                if (player != null) {
                    player.sendMessage(ChatColor.RED + "You do not have permission.");
                }
            }
        }
        return false;
    }
}