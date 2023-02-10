package tech.secretgarden.stash.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.secretgarden.stash.Data.GetMethods;
import tech.secretgarden.stash.Data.MapConversion;

public class StashCommand implements CommandExecutor {

    private final GetMethods getMethods = new GetMethods();
    Give give = new Give();

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
                } else {
                    Inventory otherStash = MapConversion.map.get(idString);
                    player.openInventory(otherStash);
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