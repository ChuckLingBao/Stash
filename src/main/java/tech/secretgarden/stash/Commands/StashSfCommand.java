package tech.secretgarden.stash.Commands;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import tech.secretgarden.stash.StashPlugin;

public class StashSfCommand implements CommandExecutor {

    private final StashPlugin plugin;
    public StashSfCommand(StashPlugin instance) { this.plugin = instance; }

    Give give = new Give();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (plugin.getSfAPI() != null) {
            SlimefunItem sfItem = SlimefunItem.getById(args[2].toUpperCase());
            ItemStack item = sfItem.getItem();
            String itemName = item.getItemMeta().getDisplayName();

            give.run(sender, args, item, itemName);
        }
        return false;
    }
}
