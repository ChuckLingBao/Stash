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
                player.openInventory(MapConversion.map.get(player.getUniqueId().toString()));

                //GIVE ARG
            } else if (args[0].equals("give") && player.hasPermission("stash.a")) {

                //ALL ARG
                if (args[1].equals("all") && (args[2].equals("sf"))) {
                    //ALL SF ARGS
                    //this only executes if argument "all" is used
                    //this will be repeated
                    SlimefunItem sfItem = SlimefunItem.getById(args[3].toUpperCase());
                    ItemStack sfItemStack = sfItem.getItem();
                    if (args.length == 4) {
                        player.sendMessage("this is an sf item");
                        for (Map.Entry entry : MapConversion.map.entrySet()) {
                            Inventory stashInv = (Inventory) entry.getValue();
                            stashInv.addItem(sfItemStack);
                        }
                        for (String p : keys) {
                            plugin.getConfig().createSection(p + ".Added Items." + "- " + sfItemStack);
                            plugin.getConfig().set(p + ".Added Items." + "- " + sfItemStack, dateStr);
                            plugin.saveConfig();
                        }
                    } else if (args.length == 5) {
                        //checking if int arg is an Integer
                        try {
                            Integer.parseInt(args[4]);
                            int integer = Integer.parseInt(args[4]);
                            if (integer <= sfItemStack.getMaxStackSize()) {
                                for (int i = 0; i < integer; i++) {
                                    for (Map.Entry entry : MapConversion.map.entrySet()) {
                                        Inventory stashInv = (Inventory) entry.getValue();
                                        stashInv.addItem(sfItemStack);
                                    }
                                }
                                for (String p : keys) {
                                    plugin.getConfig().createSection(p + ".Added Items." + "- " + sfItemStack);
                                    plugin.getConfig().set(p + ".Added Items." + "- " + "x" + integer + sfItemStack, dateStr);
                                    plugin.saveConfig();
                                }
                                player.sendMessage("these are items");
                            }
                        } catch (NumberFormatException nfe) {
                            player.sendMessage("This argument must be an integer");
                        }

                    }
                    //COMPLETED SF ALL ARGS

                } else if (args[1].equals("all")) {
                    if (args.length == 3) {
                        Material mat = Material.getMaterial(args[2].toUpperCase());
                        ItemStack item = new ItemStack(mat);
                        //argument 0 for a vanilla material
                        //This is argument 1
                        for (Map.Entry entry : MapConversion.map.entrySet()) {
                            Inventory stashInv = (Inventory) entry.getValue();
                            stashInv.addItem(item);
                        }
                        for (String p : keys) {
                            plugin.getConfig().createSection(p + ".Added Items." + "- " + item);
                            plugin.getConfig().set(p + ".Added Items." + "- " + item, dateStr);
                            plugin.saveConfig();
                        }



                    } else if (args.length == 4) {
                        Material mat = Material.getMaterial(args[2].toUpperCase());
                        ItemStack item = new ItemStack(mat);
                        try {
                            Integer.parseInt(args[3]);
                            int integer = Integer.parseInt(args[3]);
                            if (integer <= item.getMaxStackSize()) {
                                ItemStack items = new ItemStack(mat, integer);
                                player.sendMessage("these are items");
                                stash.addItem(items);
                                for (String p : keys) {
                                    plugin.getConfig().createSection(p + ".Added Items." + "- " + item);
                                    plugin.getConfig().set(p + ".Added Items." + "- " + item, dateStr);
                                    plugin.saveConfig();
                                }
                            } else {
                                player.sendMessage("There has been an error");
                            }
                        } catch (NumberFormatException nfe) {
                            player.sendMessage("This argument must be an integer");
                        }
                    }
                }
                /*
                    BELOW IS FOR GIVING ITEMS TO A SINGLE PLAYER
                     */
                else {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (args.length == 3) {
                        if (target != null) {
                            Material mat = Material.getMaterial(args[2].toUpperCase());
                            ItemStack item = new ItemStack(mat);
                            //opens online player's inventory
                            String argPlayer = target.getPlayer().getPlayer().getUniqueId().toString();
                            Inventory targetStash = MapConversion.map.get(target.getUniqueId().toString());
                            targetStash.addItem(item);


                        } else if (target == null) {
                            Material mat = Material.getMaterial(args[2].toUpperCase());
                            ItemStack item = new ItemStack(mat);
                            //checking if argument == offline player
                            if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore() == true) {
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                                String offlinePlayerId = offlinePlayer.getUniqueId().toString();
                                Inventory offlineStash = MapConversion.map.get(offlinePlayerId);
                                //Now see if offlinePlayer String matches map key.
                                if (MapConversion.map.containsKey(offlinePlayerId)) {
                                    offlineStash.addItem(item);
                                } else {
                                    player.sendMessage("This player has not logged in before");
                                }
                            } else {
                                player.sendMessage("This player has not logged in before");
                            }

                        } else {
                            player.sendMessage("Player could not be found");
                        }
                    } else if (args[2].equals("sf")) {
                        SlimefunItem sfItem = SlimefunItem.getById(args[3].toUpperCase());
                        ItemStack sfItemStack = sfItem.getItem();
                        if (args.length == 4) {
                            player.sendMessage("this is an sf item");
                            stash.addItem(sfItemStack);
                        } else if (args.length == 5) {
                            //checking if int arg is an Integer
                            try {
                                Integer.parseInt(args[4]);
                                int integer = Integer.parseInt(args[4]);
                                if (integer <= sfItemStack.getMaxStackSize()) {
                                    for (int i = 0; i < integer; i++) {
                                        stash.addItem(sfItemStack);
                                    }
                                    player.sendMessage("these are items");
                                }
                            } catch (NumberFormatException nfe) {
                                player.sendMessage("This argument must be an integer");
                            }

                        }
                    }

                    else {
                        if (target != null) {
                            Material mat = Material.getMaterial(args[2].toUpperCase());
                            ItemStack item = new ItemStack(mat);
                            //opens online player's inventory
                            String argPlayer = target.getPlayer().getPlayer().getUniqueId().toString();
                            Inventory targetStash = MapConversion.map.get(target.getUniqueId().toString());
                            //ADD INT PARSING HERE
                            try {
                                Integer.parseInt(args[3]);
                                int integer = Integer.parseInt(args[3]);
                                if (integer <= item.getMaxStackSize()) {
                                    ItemStack items = new ItemStack(mat, integer);
                                    player.sendMessage("these are items");
                                    stash.addItem(items);
                                } else {
                                    player.sendMessage("There has been an error");
                                }
                            } catch (NumberFormatException nfe) {
                                player.sendMessage("This argument must be an integer");
                            }
                            targetStash.addItem(item);
                        } else if (target == null) {
                            Material mat = Material.getMaterial(args[2].toUpperCase());
                            ItemStack item = new ItemStack(mat);
                            //checking if argument == offline player
                            if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore() == true) {
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                                String offlinePlayerId = offlinePlayer.getUniqueId().toString();
                                Inventory offlineStash = MapConversion.map.get(offlinePlayerId);
                                //Now see if offlinePlayer String matches map key.
                                if (MapConversion.map.containsKey(offlinePlayerId)) {
                                    offlineStash.addItem(item);
                                } else {
                                    player.sendMessage("This player has not logged in before");
                                }
                            } else {
                                player.sendMessage("This player has not logged in before");
                            }

                        } else {
                            player.sendMessage("Player could not be found");
                        }
                    }

                }
            }
        }
        return false;
    }
}