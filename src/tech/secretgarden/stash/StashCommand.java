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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public class StashCommand implements CommandExecutor {

    private final Main plugin;

    public StashCommand(Main instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {

        LocalDateTime date = LocalDateTime.now();
        String dateStr = date.format(DateTimeFormatter.ofPattern("EEEE MMMM dd yyyy hh,mm,ss a"));
        Set<String> keys = plugin.getConfig().getKeys(false);

        if (sender instanceof Player) {
            Player player = (Player) sender;
            Inventory senderStash = MapConversion.map.get(player.getUniqueId().toString());
            //opens your own stash
            if (args.length == 0) {
                player.openInventory(senderStash);

                //GIVE ARG
            } else if (args[0].equals("give") && player.hasPermission("stash.a")) {

                //ALL ARG
                if (args[1].equals("all")) {
                    Material mat = Material.getMaterial(args[2].toUpperCase());
                    ItemStack item = new ItemStack(mat);
                    if (args.length == 3) {
                        Integer integer = 1;
                        for (Map.Entry entry : MapConversion.map.entrySet()) {
                            Inventory stashInv = (Inventory) entry.getValue();
                            stashInv.addItem(item);
                        }
                        configAddAll(keys, item, integer, dateStr);
                    } else if (args.length == 4) {
                        parseIntegersAll(args, item, keys, dateStr, player);
                    }
                /*
                    BELOW IS FOR GIVING ITEMS TO A SINGLE PLAYER
                     */
                } else {
                    Player target = Bukkit.getPlayer(args[1]);
                    Material mat = Material.getMaterial(args[2].toUpperCase());
                    ItemStack item = new ItemStack(mat);
                    if (target != null) {
                        Inventory singleStash = MapConversion.map.get(target.getUniqueId().toString());
                        if (args.length == 3) {
                            Integer integer = 1;
                            singleStash.addItem(item);
                            configAdd(args, item, integer, dateStr);
                        } else if (args.length == 4) {
                            parseIntegersSingle(args, singleStash, item, dateStr, player);
                        }
                    } else {
                        //target == null
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                        String offlinePlayerId = offlinePlayer.getUniqueId().toString();
                        Inventory singleStash = MapConversion.map.get(offlinePlayer.getUniqueId().toString());
                        if (args.length == 3) {
                            if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                                Integer integer = 1;
                                //Now see if offlinePlayer String matches map key.
                                if (MapConversion.map.containsKey(offlinePlayerId)) {
                                    singleStash.addItem(item);
                                    configAdd(args, item, integer, dateStr);
                                } else {
                                    player.sendMessage("This player has not logged in before");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "This is not a valid player.");
                            }
                        } else if (args.length == 4) {
                            if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                                parseIntegersSingle(args, singleStash, item, dateStr, player);
                            } else {
                                player.sendMessage(ChatColor.RED + "This is not a valid player.");
                            }
                        }
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "You do not have permission.");
            }
        }
        return false;
    }

    private void configAddAll(Set<String> keys, ItemStack item, Integer integer, String dateStr) {
        for (String p : keys) {
            plugin.getConfig().createSection(p + ".Added Items." + "- " + item);
            plugin.getConfig().set(p + ".Added Items." + "- " + "x" + integer+ " " + item, dateStr);
            plugin.saveConfig();
        }
    }
    private void configAdd(String[] args, ItemStack item, Integer integer, String dateStr) {
        String p = args[1];
        plugin.getConfig().createSection(p + ".Added Items." + "- " + item);
        plugin.getConfig().set(p + ".Added Items." + "- " + "x" + integer+ " " + item, dateStr);
        plugin.saveConfig();
    }
    private void parseIntegersAll(String[] args, ItemStack item, Set<String> keys, String dateStr, Player player) {
        try {
            Integer.parseInt(args[3]);
            int integer = Integer.parseInt(args[3]);
            if (integer <= item.getMaxStackSize()) {
                for (Map.Entry entry : MapConversion.map.entrySet()) {
                    Inventory stashInv = (Inventory) entry.getValue();
                    for (int i = 0; i < integer; i++) {
                        stashInv.addItem(item);
                    }
                }
                configAddAll(keys, item, integer, dateStr);
            }
        } catch (NumberFormatException nfe) {
            player.sendMessage("This argument must be an integer");
        }
    }
    private void parseIntegersSingle(String[] args, Inventory singleStash, ItemStack item, String dateStr, Player player) {
        try {
            Integer.parseInt(args[3]);
            int integer = Integer.parseInt(args[3]);
            if (integer <= item.getMaxStackSize()) {
                for (int i = 0; i < integer; i++) {
                    singleStash.addItem(item);
                }
                configAdd(args, item, integer, dateStr);
            }
        } catch (NumberFormatException nfe) {
            player.sendMessage("This argument must be an integer");
        }
    }
}