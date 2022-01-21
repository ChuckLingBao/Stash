package tech.secretgarden.stash;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class StashSfCommand implements CommandExecutor {
    private final Main plugin;
    public StashSfCommand(Main instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            Player target = Bukkit.getPlayer(args[1]);

            if (args[0].equals("give") && player.hasPermission("stash.a")) {
                SlimefunItem sfItem = SlimefunItem.getById(args[2].toUpperCase());
                ItemStack item = sfItem.getItem();
                String itemName = item.getItemMeta().getDisplayName();
                if (args[1].equals("all")) {
                    //ALL SF ARGS
                    if (args.length == 3) {
                        Integer integer = 1;
                        for (Map.Entry entry : MapConversion.map.entrySet()) {
                            Inventory stashInv = (Inventory) entry.getValue();
                            stashInv.addItem(item);
                        }
                        plugin.configAddAll(itemName, integer);
                    } else if (args.length == 4) {
                        //checking if int arg is an Integer
                        plugin.parseIntegersAll(args, item, player);
                    }
                }
                /*
                    BELOW IS FOR GIVING ITEMS TO A SINGLE PLAYER
                     */
                else if (target != null) {
                    Inventory singleStash = MapConversion.map.get(target.getUniqueId().toString());
                    if (args.length == 3) {
                        Integer integer = 1;
                        singleStash.addItem(item);
                        plugin.configAdd(args, itemName, integer);
                    } else if (args.length == 4) {
                        plugin.parseIntegersSingle(args, singleStash, item, player);
                    }
                } else {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    Inventory singleStash = MapConversion.map.get(offlinePlayer.getUniqueId().toString());
                    String offlinePlayerId = offlinePlayer.getUniqueId().toString();
                    if (args.length == 3) {
                        //checking if argument == offline player
                        if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                            Integer integer = 1;
                            //Now see if offlinePlayer String matches map key.
                            if (MapConversion.map.containsKey(offlinePlayerId)) {
                                singleStash.addItem(item);
                                plugin.configAdd(args, itemName, integer);
                            } else {
                                player.sendMessage("This player has not logged in before");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "This is not a valid player.");
                        }
                    } else if (args.length == 4) {
                        if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                            plugin.parseIntegersSingle(args, singleStash, item, player);
                        } else {
                            player.sendMessage(ChatColor.RED + "This is not a valid player.");
                        }
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "You do not have permission.");
            }
        }
        return false;
    }
}
