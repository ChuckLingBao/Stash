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
import tech.secretgarden.stash.Stash;

import java.lang.reflect.Field;

public class RandomSpawnerCommand implements CommandExecutor {

    Stash plugin;
    public RandomSpawnerCommand(Stash instance) {
        this.plugin = instance;
    }

    GetMethods getMethods = new GetMethods();
    Spawners spawners = new Spawners();
    Give give = new Give();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = null;
        String giver = "console";
        if (sender instanceof Player) {
            player = (Player) sender;
            giver = player.getName();
        }

        // handle wrong args.
        if (args.length < 3 || args.length > 4) {
            showUsage(player);
            return false;
        }

        // get spawner type
        String type;
        if (args[2].equalsIgnoreCase("hostile")) { type = "hostile"; }
        else if (args[2].equalsIgnoreCase("passive")) { type = "passive"; }
        else if (args[2].equalsIgnoreCase("rare")) { type = "rare"; }
        else if (args[2].equalsIgnoreCase("both")) { type = "both"; }
        else {
            showUsage(player);
            return false;
        }

        // get exact spawner
        ItemStack spawner = spawners.getSpawner(type);
        String itemName = spawner.getItemMeta().getDisplayName();

        give.run(sender, args, spawner, itemName);
        return false;
    }

    private void showUsage(Player player) {
        if (player != null) {
            player.sendMessage(ChatColor.YELLOW + "/randomspawner give <player> <hostile/passive>");
        } else {
            Bukkit.getLogger().info("/randomspawner give <player> <hostile/passive>");
        }

    }

    public void initList(Class<?> instance, String[] entityList) throws IllegalAccessException {

        Field[] fields = instance.getFields();

        for (int i = 0; i < fields.length; i++) {
            Class<?> type = fields[i].getType();
            Object value = fields[i].get(instance);
            if (type.isAssignableFrom(String.class)) {
                entityList[i] = (String) value;
            }
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
