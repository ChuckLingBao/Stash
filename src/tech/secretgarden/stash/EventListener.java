package tech.secretgarden.stash;

import io.github.thebusybiscuit.exoticgarden.items.BonemealableItem;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
            String date = LocalDateTime.now().toString();

            MapConversion.map.put(uuid, inv);

            //this creates lines in the config to keep track of who has a stash.
            List<String> configList = new ArrayList<>();
            configList.add(uuid);
            configList.add(date);

            plugin.getConfig().createSection(player);
            plugin.getConfig().set(player, configList);
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

    @EventHandler
    public void rightClick(PlayerRightClickEvent e) {
        SlimefunItem item = e.getSlimefunItem().get();
        ItemStack itemStack = SlimefunItem.getById("COAL_PLANT").getItem();
        SlimefunItem coalPlant = BonemealableItem.getById("COAL_PLANT");

        e.getPlayer().sendMessage(item.toString());
        e.getPlayer().getInventory().addItem(itemStack);

    }
}
