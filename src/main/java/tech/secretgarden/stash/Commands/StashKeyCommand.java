package tech.secretgarden.stash.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import su.nightexpress.excellentcrates.ExcellentCratesAPI;
import su.nightexpress.excellentcrates.key.CrateKey;
import tech.secretgarden.stash.StashPlugin;

public class StashKeyCommand implements CommandExecutor {

    private final StashPlugin plugin;
    public StashKeyCommand(StashPlugin instance) { this.plugin = instance; }

    Give give = new Give();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (plugin.getEcAPI() != null && plugin.getNeAPI() != null) {

//            CrateKey key = new CrateKey(plugin.getEcAPI(), args[2]);
            CrateKey key = ExcellentCratesAPI.getKeyManager().getKeyById(args[2]);
            ItemStack item = key.getItem();
            String itemName = item.getItemMeta().getDisplayName();

            give.run(sender, args, item, itemName);
        }

        return false;
    }
}
