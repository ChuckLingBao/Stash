package tech.secretgarden.stash;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StashSfCommand implements CommandExecutor {
    private final GiveMethods giveMethods = new GiveMethods();

    private final Stash plugin;
    public StashSfCommand(Stash instance) { this.plugin = instance; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = null;
        String giver;

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
                    if (plugin.getSfAPI() != null) {
                        SlimefunItem sfItem = SlimefunItem.getById(args[2].toUpperCase());
                        ItemStack item = sfItem.getItem();
                        String itemName = item.getItemMeta().getDisplayName();
                        if (args[1].equals("all")) {
                            //ALL SF ARGS
                            giveMethods.giveAllPlayers(args, item, giver, itemName);
                        } else if (args[1].equals("time")) {
                            giveMethods.giveByTime(giver, args, item, itemName, player);
                        }
                /*
                    BELOW IS FOR GIVING ITEMS TO A SINGLE PLAYER
                     */
                        else {
                            Player target = Bukkit.getPlayer(args[1]);
                            if (target != null) {
                                String uuid = target.getUniqueId().toString();
                                giveMethods.giveSinglePlayer(args, item, giver, uuid, itemName);
                            } else {
                                //target == null
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                                if (offlinePlayer.hasPlayedBefore()) {
                                    String uuid = offlinePlayer.getUniqueId().toString();
                                    giveMethods.giveSinglePlayer(args, item, giver, uuid, itemName);
                                } else {
                                    if (player != null) {
                                        player.sendMessage("This player has not logged in before.");
                                    }
                                }
                            }
                        }
                    } else {
                        if (player != null) {
                            player.sendMessage(ChatColor.RED + "Cannot get Slimefun instance!");
                        }
                    }

                } else {
                    if (player != null) {
                        player.sendMessage(ChatColor.RED + "You do not have permission.");
                    }
                }
        return false;
    }
}
