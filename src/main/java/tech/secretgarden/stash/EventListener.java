package tech.secretgarden.stash;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import tech.secretgarden.stash.Data.Database;
import tech.secretgarden.stash.Data.GetMethods;
import tech.secretgarden.stash.Data.InsertData;
import tech.secretgarden.stash.Data.MapConversion;
import tech.secretgarden.stash.SpawnerNames.Hostile;
import tech.secretgarden.stash.SpawnerNames.Passive;
import tech.secretgarden.stash.SpawnerNames.Rare;

import java.sql.*;
import java.time.LocalDateTime;

public class EventListener implements Listener {

    private final Stash plugin;
    public EventListener(Stash instance) { this.plugin = instance; }

    private final Database database = new Database();
    private final MapConversion mapConversion = new MapConversion();
    private final GetMethods getMethods = new GetMethods();
    private final InsertData insertData = new InsertData();

    LocalDateTime date = LocalDateTime.now();
    Timestamp timestamp = Timestamp.valueOf(date);

    @EventHandler
    //Stash inventory creation
    public void join(PlayerJoinEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();
        String name = e.getPlayer().getName();

        if (getMethods.getPlayerId(uuid) == 0) {
            insertData.addPlayer(uuid, name, timestamp);
        } else {
            // player exists in db
            Inventory stash = getMethods.getStashInv(uuid);
            MapConversion.map.put(uuid, stash);
        }

        if (!MapConversion.map.get(e.getPlayer().getUniqueId().toString()).isEmpty()) {
            e.getPlayer().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You have Items in your Stash, use '/stash' to get your items.");
        }
    }

    @EventHandler
    public void invClick(InventoryClickEvent e) {

        String title = e.getView().getTitle();

        if (title.contains(ChatColor.DARK_PURPLE + "Stash")) {
            //top inv title includes "stash"
            ItemStack cursor = e.getCursor();
            ItemStack slot = e.getCurrentItem();

            if (!cursor.getType().isAir() && slot != null) {
                e.setCancelled(true);
                return;
            } else if (e.getClick().isRightClick()) {
                e.setCancelled(true);
                return;
                //if user tries to swap item or right click, cancel event regardless of their permissions!
            } else if (e.getClickedInventory() == null) {
                e.setCancelled(true);
                return;
            } else {

                Inventory stash = e.getView().getTopInventory();
                Inventory playerInventory = e.getWhoClicked().getInventory();

                String key = getMethods.getMapKey(stash);

                String stashStr = mapConversion.inventoryToString(stash);

                if (e.getClickedInventory().equals(stash)) {
                    //clicked stash inventory
                    if (cursor.getType().isAir() && slot != null) {
                        //removing item from stash
                        updatePlayers(stashStr, key);
                        return;
                    }

                    if (!cursor.getType().isAir()) {
                        //adding item to stash
                        if (e.getWhoClicked().hasPermission("stash.a")) {

                            updatePlayers(stashStr, key);
                        } else {
                            e.setCancelled(true);
                        }
                        return;
                    }
                    return;
                }

                if (e.getClickedInventory().equals(playerInventory)) {
                    //clicked player inventory
                    if (cursor.getType().isAir() && (slot != null)) {
                        //removing item from player inv
                        if (e.getWhoClicked().hasPermission("stash.a")) {
                            updatePlayers(stashStr, key);
                            return;
                        } else {
                            e.setCancelled(true);
                        }
                    }

                    if (!cursor.getType().isAir()) {
                        updatePlayers(stashStr, key);
                        //adding item to player inv
                    } else {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void close(InventoryCloseEvent e) {
        // if player closes stash, update db
        if (e.getView().getTitle().contains(ChatColor.DARK_PURPLE + "Stash")) {
            Inventory stash = e.getView().getTopInventory();
            String stashStr = mapConversion.inventoryToString(stash);
            String key = getMethods.getMapKey(stash);

            updatePlayers(stashStr, key);
        }
    }

    @EventHandler
    public void drag(InventoryDragEvent e) {
        Inventory stashInv = MapConversion.map.get(e.getWhoClicked().getUniqueId().toString());
        if (stashInv.getViewers().contains(e.getWhoClicked())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void place(BlockPlaceEvent e) {

        Block block = e.getBlock();

        if (block.getType().equals(Material.SPAWNER)) {

            ItemMeta itemMeta = e.getItemInHand().getItemMeta();
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();

            String data;
            EntityType entity;
            BlockState bs = block.getState();
            CreatureSpawner cs = (CreatureSpawner) bs;

            if (container.has(plugin.getKey("hostile"), PersistentDataType.STRING)) {
                data = container.get(plugin.getKey("hostile"), PersistentDataType.STRING);
                entity = Hostile.entityMap.get(data);
                cs.setSpawnedType(entity);
                System.out.println(data);

            } else if (container.has(plugin.getKey("passive"), PersistentDataType.STRING)) {

                // passive
                data = container.get(plugin.getKey("passive"), PersistentDataType.STRING);
                entity = Passive.entityMap.get(data);
                cs.setSpawnedType(entity);
                System.out.println(data);

            } else if (container.has(plugin.getKey("rare"), PersistentDataType.STRING)) {

                // rare
                data = container.get(plugin.getKey("rare"), PersistentDataType.STRING);
                entity = Rare.entityMap.get(data);
                cs.setSpawnedType(entity);
                System.out.println(data);

            }
        }
    }

    private void updatePlayers(String stashString, String uuid) {

        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE player " +
                     "SET inv = ? " +
                     "WHERE uuid = ?;")) {
            statement.setString(1, stashString);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


