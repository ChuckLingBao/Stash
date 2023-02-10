package tech.secretgarden.stash.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import tech.secretgarden.stash.Data.GetMethods;
import tech.secretgarden.stash.Spawners;

public class RandomSpawner implements CommandExecutor {

    GetMethods getMethods = new GetMethods();
    Spawners spawners = new Spawners();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = null;
        String giver = "console";
        if (sender instanceof Player) {
            player = (Player) sender;
            giver = player.getName();
        }

        // handle wrong args.
        if (args.length != 3) {
            showUsage(player);
            return false;
        }

        // get the receiver
        String receiver = args[1];
        String uuid = getMethods.getIdString(receiver);

        // handle receiver does not exist
        if (uuid == null) {
            tellReceiverNull(player);
            return false;
        }

        // get spawner type
        String type = null;
        if (args[2].equalsIgnoreCase("hostile")) { type = "hostile"; }
        else if (args[2].equalsIgnoreCase("passive")) { type = "passive"; }
        else {
            showUsage(player);
            return false;
        }

        // get exact spawner
        ItemStack spawner = spawners.getSpawner(type);
        String itemName = spawner.getItemMeta().getDisplayName();
        System.out.println(itemName);

        StashCmdContents contents = new StashCmdContents(giver, spawner, itemName, args);

        if (contents.error != null) {
            Bukkit.getLogger().warning(contents.error);
            return false;
        }
        ReceiverList receiverList = new ReceiverList(contents);
        if (contents.getQuantity() == 0) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Quantity is invalid");
            }
            Bukkit.getLogger().warning("Quantity is invalid");
            return false;
        }

        //no errors, give the item(s)
        receiverList.addItem();

        return false;
    }

    private void showUsage(Player player) {
        if (player != null) {
            player.sendMessage(ChatColor.YELLOW + "/randomspawner give <player> <hostile/passive>");
        } else {
            Bukkit.getLogger().info("/randomspawner give <player> <hostile/passive>");
        }

    }

    private void tellReceiverNull(Player player) {
        if (player != null) {
            player.sendMessage(ChatColor.RED + "Player does not exist");
        } else {
            Bukkit.getLogger().info("Player does not exist");
        }
    }
}
