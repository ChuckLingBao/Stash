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

import java.util.ArrayList;

public class StashCommand implements CommandExecutor {

    private final MapConversion mapConversion = new MapConversion();
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

                } else if (args.length == 1 && player.hasPermission("stash.a")) {
                    String p = args[0];

                    if (Bukkit.getPlayer(p) != null) {
                        String id = Bukkit.getPlayer(p).getUniqueId().toString();
                        Inventory otherStash = MapConversion.map.get(id);
                        player.openInventory(otherStash);
                    } else if (Bukkit.getOfflinePlayer(p).hasPlayedBefore()) {
                        String id = Bukkit.getOfflinePlayer(p).getUniqueId().toString();
                        Inventory otherStash = MapConversion.map.get(id);
                        player.openInventory(otherStash);
                    } else {
                        player.sendMessage(ChatColor.RED + "This is not a valid player!");
                    }
                    //GIVE ARGS
                } else if (args[0].equals("give") && player.hasPermission("stash.a")) {
                    Material mat = Material.getMaterial(args[2].toUpperCase());
                    ItemStack item = new ItemStack(mat);
                    String itemName = item.toString();

                    //ALL ARG
                    if (args[1].equals("all")) {
                        giveMethods.giveAllPlayers(args, item, player, itemName);
                /*
                    BELOW IS FOR GIVING ITEMS TO A SINGLE PLAYER
                     */
                    } else {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            String uuid = target.getUniqueId().toString();
                            Inventory singleStash = MapConversion.map.get(target.getUniqueId().toString());
                            giveMethods.giveSinglePlayer(args, singleStash, item, player, uuid, itemName);
                        } else {
                            //target == null
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                            if (offlinePlayer.hasPlayedBefore()) {
                                String uuid = offlinePlayer.getUniqueId().toString();
                                Inventory singleStash = MapConversion.map.get(offlinePlayer.getUniqueId().toString());
                                giveMethods.giveSinglePlayer(args, singleStash, item, player, uuid, itemName);
                            } else {
                                player.sendMessage("This player has not logged in before.");
                            }
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You cannot do that in this world!");
            }
        }
        return false;
    }
}