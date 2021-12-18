package tech.secretgarden.stash;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Bukkit;
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

    private Main plugin;

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
            Inventory stash = MapConversion.map.get(player.getUniqueId().toString());

            //opens your own stash
            if (args.length == 0) {
                player.openInventory(stash);

                //GIVE ARG
            } else if (args[0].equals("give") && player.hasPermission("stash.a")) {

                //ALL ARG
                if (args[1].equals("all") && (args[2].equals("sf"))) {
                    //ALL SF ARGS
                    //this only executes if argument "all" is used
                    //this will be repeated
                    SlimefunItem sfItem = SlimefunItem.getById(args[3].toUpperCase());
                    ItemStack item = sfItem.getItem();
                    if (args.length == 4) {
                        Integer integer = 1;
                        for (Map.Entry entry : MapConversion.map.entrySet()) {
                            Inventory stashInv = (Inventory) entry.getValue();
                            stashInv.addItem(item);
                        }
                        configAdd(keys, item, integer, dateStr);
                    } else if (args.length == 5) {
                        //checking if int arg is an Integer
                        parseIntegers4(args, item, stash, keys, dateStr, player);
                    }
                    //COMPLETED SF ALL ARGS
                } else if (args[1].equals("all")) {
                    Material mat = Material.getMaterial(args[2].toUpperCase());
                    ItemStack item = new ItemStack(mat);
                    if (args.length == 3) {
                        Integer integer = 1;
                        for (Map.Entry entry : MapConversion.map.entrySet()) {
                            Inventory stashInv = (Inventory) entry.getValue();
                            stashInv.addItem(item);
                        }
                        configAdd(keys, item, integer, dateStr);
                    } else if (args.length == 4) {
                        parseIntegers3(args, item, stash, keys, dateStr, player);
                    }
                }
                /*
                    BELOW IS FOR GIVING ITEMS TO A SINGLE PLAYER
                     */
                //VANILLA ONLINE PLAYER
                else {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null && args[2].equals("sf")) {
                        SlimefunItem sfItem = SlimefunItem.getById(args[3].toUpperCase());
                        ItemStack item = sfItem.getItem();
                        Inventory targetStash = MapConversion.map.get(target.getUniqueId().toString());
                        if (args.length == 4) {
                            Integer integer = 1;
                            targetStash.addItem(item);
                            configAdd(keys, item, integer, dateStr);
                        } else if (args.length == 5) {
                            parseIntegers4(args, item, stash, keys, dateStr, player);
                        }
                    } else if (target == null && args[2].equals("sf")) {
                        SlimefunItem sfItem = SlimefunItem.getById(args[3].toUpperCase());
                        ItemStack item = sfItem.getItem();
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                        String offlinePlayerId = offlinePlayer.getUniqueId().toString();
                        Inventory offlineStash = MapConversion.map.get(offlinePlayerId);
                        if (args.length == 4) {
                            //checking if argument == offline player
                            if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore() == true) {
                                Integer integer = 1;
                                //Now see if offlinePlayer String matches map key.
                                if (MapConversion.map.containsKey(offlinePlayerId)) {
                                    offlineStash.addItem(item);
                                    configAdd(keys, item, integer, dateStr);
                                } else {
                                    player.sendMessage("This player has not logged in before");
                                }
                            }
                        } else if (args.length == 5) {
                            parseIntegers4(args, item, stash, keys, dateStr, player);
                        }
                    }
                    //END SF

                    else if (target != null) {
                        Inventory targetStash = MapConversion.map.get(target.getUniqueId().toString());
                        Material mat = Material.getMaterial(args[2].toUpperCase());
                        ItemStack item = new ItemStack(mat);
                        if (args.length == 3) {
                            Integer integer = 1;
                            targetStash.addItem(item);
                            configAdd(keys, item, integer, dateStr);
                        } else if (args.length == 4) {
                            parseIntegers3(args, item, stash, keys, dateStr, player);
                        }
                    } else {
                        //target == null
                        Material mat = Material.getMaterial(args[2].toUpperCase());
                        ItemStack item = new ItemStack(mat);
                        if (args.length == 3) {
                            Integer integer = 1;
                            Inventory targetStash = MapConversion.map.get(target.getUniqueId().toString());
                            targetStash.addItem(item);
                            configAdd(keys, item, integer, dateStr);
                        } else if (args.length == 4) {
                            parseIntegers3(args, item, stash, keys, dateStr, player);
                        }
                    }
                }
            }
        }
        return false;
    }
    private void configAdd(Set<String> keys, ItemStack item, Integer integer, String dateStr) {
        for (String p : keys) {
            plugin.getConfig().createSection(p + ".Added Items." + "- " + item);
            plugin.getConfig().set(p + ".Added Items." + "- " + "x" + integer+ " " + item, dateStr);
            plugin.saveConfig();
        }
    }
    private void parseIntegers3(String[] args, ItemStack item, Inventory stash, Set<String> keys, String dateStr, Player player) {
        try {
            Integer.parseInt(args[3]);
            int integer = Integer.parseInt(args[3]);
            if (integer <= item.getMaxStackSize()) {
                for (int i = 0; i < integer; i++) {
                    stash.addItem(item);
                }
                configAdd(keys, item, integer, dateStr);
            }
        } catch (NumberFormatException nfe) {
            player.sendMessage("This argument must be an integer");
        }
    }
    private void parseIntegers4(String[] args, ItemStack item, Inventory stash, Set<String> keys, String dateStr, Player player) {
        try {
            Integer.parseInt(args[4]);
            int integer = Integer.parseInt(args[4]);
            if (integer <= item.getMaxStackSize()) {
                for (int i = 0; i < integer; i++) {
                    stash.addItem(item);
                }
                configAdd(keys, item, integer, dateStr);
            }
        } catch (NumberFormatException nfe) {
            player.sendMessage("This argument must be an integer");
        }
    }
}