package tech.secretgarden.stash.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tech.secretgarden.stash.Data.StashAPI;
import tech.secretgarden.stash.Data.StashMap;
import tech.secretgarden.stash.Stash;

public class StashCommand implements CommandExecutor {

    private final StashAPI stashAPI = new StashAPI();
    Give give = new Give();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            String world = player.getWorld().getName();
            if (stashAPI.getWorld(world)) {
                Stash senderStash = StashMap.map.get(player.getUniqueId().toString());
                //opens your own stash
                if (args.length == 0) {
                    player.openInventory(senderStash.createInventory());
                    senderStash.populateInventoryPage(0);
                    return false;
                }
            } else {
                player.sendMessage(ChatColor.RED + "You cannot do that in this world!");
            }
            if (args.length == 1 && player.hasPermission("stash.a")) {
                //opens a different player's stash
                String p = args[0];

                String idString = stashAPI.getIdString(p);
                System.out.println("ID = " + idString);
                if (idString == null) {
                    player.sendMessage(ChatColor.RED + "This is not a valid player!");
                } else {
                    Stash otherStash = StashMap.map.get(idString);
                    player.openInventory(otherStash.createInventory(true));
                    otherStash.populateInventoryPage(0);
                }
                return false;
            }
        }

        //GIVE ARGS-------------------------------------------------------------------------------------

        Material mat = Material.getMaterial(args[2].toUpperCase());
        ItemStack item = new ItemStack(mat);
        String itemName = item.toString();

        give.run(sender, args, item, itemName);

        return false;
    }

    public boolean hasPermission(Player player) {
        if (player != null) {
            if (!player.hasPermission("stash.a")) {
                //Handle player's permission
                player.sendMessage(ChatColor.RED + "You do not have permission!");
                return false;
            }
        }
        return true;
    }
}