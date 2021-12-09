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
import java.util.Set;

public class StashCommand implements CommandExecutor {

    private Main plugin;
    public StashCommand(Main instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {

        LocalDateTime date = LocalDateTime.now();
        String dateStr = date.format(DateTimeFormatter.ofPattern("EEEE MMMM dd yyyy hh:mm:ss a"));



        if (sender instanceof Player) {
            Player player = (Player) sender;
            Inventory stash = MapConversion.map.get(player.getUniqueId().toString());
            //args check below
            if (args.length == 0) {
                player.openInventory(MapConversion.map.get(player.getUniqueId().toString()));
            } else if (args[0].equals("giveall") && player.hasPermission("stash.a")) {

                Set<String> keys = plugin.getConfig().getKeys(false);



                /*
                SLIMEFUN ARGUMENTS BELOW
                 */

                if (args[1].equals("sf")) {
                    SlimefunItem sfItem = SlimefunItem.getById(args[2].toUpperCase());
                    ItemStack sfItemStack = sfItem.getItem();
                    if (args.length == 3) {
                        player.sendMessage("this is an sf item");
                        stash.addItem(sfItemStack);
                    } else if (args.length == 4) {
                        //checking if int arg is an Integer
                        try {
                            Integer.parseInt(args[3]);
                            int integer = Integer.parseInt(args[3]);
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

                    /*
                    VANILLA ARGS BELOW
                     */
                } else {
                    //argument 0 for a vanilla material
                    Material mat = Material.getMaterial(args[1].toUpperCase());
                    //This is argument 1
                    ItemStack item = new ItemStack(mat);
                    if (mat.isItem()) {

                        if (args.length == 2) {
                            player.sendMessage("this is an item");
                            stash.addItem(item);

                            for (String p : keys) {
                                plugin.getConfig().createSection(p + ".Added Items." + "- " + item);
                                plugin.getConfig().set(p + ".Added Items." + "- " + item, dateStr);
                                plugin.saveConfig();
                            }


                        }
                        else if (args.length == 3) {
                            //checking if int arg is an Integer
                            try {
                                Integer.parseInt(args[2]);
                                int integer = Integer.parseInt(args[2]);
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

                        } else {
                            player.sendMessage("There has been an error");
                        }

                    }

                }

                /*
                OTHER PLAYER STASH BELOW
                 */
            } else if (args[0].length() > 0 && player.hasPermission("stash.a")) {
                //checking if command has an argument
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    //opens online player's inventory
                    String argPlayer = target.getPlayer().getPlayer().getUniqueId().toString();
                    player.openInventory(MapConversion.map.get(argPlayer));
                } else if (target == null) {
                    //checking if argument == offline player
                    if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore() == true) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                        String offlinePlayerId = offlinePlayer.getUniqueId().toString();
                        //Now see if offlinePlayer String matches map key.
                        if (MapConversion.map.containsKey(offlinePlayerId)) {
                            player.openInventory(MapConversion.map.get(offlinePlayerId));
                        } else {
                            player.sendMessage("This player has not logged in before");
                        }
                    } else {
                        player.sendMessage("This player has not logged in before");
                    }

                } else {
                    player.sendMessage("Player could not be found");
                }

            } else {
                player.sendMessage("You cannot do this");
            }
        } else {
            System.out.println("This command can only be used by players!");
        }
        return false;
    }
}
