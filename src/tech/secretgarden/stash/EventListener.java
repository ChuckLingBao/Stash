package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventListener implements Listener {

    private Main plugin;
    public EventListener(Main instance) {
        this.plugin = instance;
    }

    @EventHandler
    //Stash inventory creation
    public void join(PlayerJoinEvent e) {
        if (MapConversion.map.containsKey(e.getPlayer().getUniqueId().toString())) {
            return;
        } else {
            Inventory inv = Bukkit.createInventory(null, 18, ChatColor.DARK_PURPLE + "Stash");
            String uuid = e.getPlayer().getUniqueId().toString();
            String player = e.getPlayer().getName();
            LocalDateTime date = LocalDateTime.now();
            String dateStr = date.format(DateTimeFormatter.ofPattern("EEEE MMMM dd yyyy hh:mm:ss a"));

            MapConversion.map.put(uuid, inv);

            //Sets up config entries for each player
            plugin.getConfig().createSection(player);
            plugin.getConfig().createSection(player + ".Stash Creation");
            plugin.getConfig().createSection(player + ".Added Items");
            plugin.getConfig().set(player + ".Stash Creation", uuid + " " + dateStr);
            plugin.saveConfig();
        }
    }

    @EventHandler
    public void invClick(InventoryClickEvent e) {

        Inventory playerInventory = e.getWhoClicked().getInventory();
        Inventory stashInv = MapConversion.map.get(e.getWhoClicked().getUniqueId().toString());

        if (e.getWhoClicked().hasPermission("stash.a")) {
            //checking if player is admin
            e.setCancelled(false);
        }

        else if (e.getClickedInventory().equals(playerInventory)) {

            if (e.getCursor().getType().isAir()) {
                e.setCancelled(true);
                e.getWhoClicked().sendMessage("You cannot put items into the Stash");
            }
        }
    }
}
