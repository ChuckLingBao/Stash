package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class EventListener implements Listener {

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

//        if (!MapConversion.map.containsKey(e.getPlayer().getUniqueId().toString())) {
//
//            System.out.println(MapConversion.map.get(uuid));
//
//            Inventory inv = Bukkit.createInventory(null, 18, ChatColor.DARK_PURPLE + "Stash");
//
//            String player = e.getPlayer().getName();
//
//            MapConversion.map.put(uuid, inv);
//            System.out.println("created inv");
//            String stash = mapConversion.inventoryToString(inv);
//
//            try (Connection connection = database.getPool().getConnection();
//                 PreparedStatement statement = connection.prepareStatement("INSERT INTO player (uuid, name, inv, timestamp) VALUES (?,?,?,?);")) {
//                statement.setString(1, uuid);
//                statement.setString(2, player);
//                statement.setString(3, stash);
//                statement.setTimestamp(4, timestamp);
//                statement.executeUpdate();
//
//            } catch (Exception x) {
//                x.printStackTrace();
//            }
//
//        }

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
            }
            if (e.getClick().isRightClick()) {
                e.setCancelled(true);
                return;
                //if user tries to swap item or right click, cancel event regardless of their permissions!
            }
            if (e.getClickedInventory() == null) {
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
//                        if (e.getClick().isShiftClick()) {
//                            if (slot.hasItemMeta()) {
//                                String slotCustomItemName = slot.getItemMeta().getDisplayName();
//                                //custom item
//                                giveMethods.recordItem(name, slotCustomItemName, number, "removed ", owner, playerId);
//                            } else {
//                                giveMethods.recordItem(name, slotBukkitItemName, number, "removed ", owner, playerId);
//                            }
//                        }
                        updatePlayers(stashStr, key);
                        return;
                    }

                    if (!cursor.getType().isAir()) {
                        //adding item to stash
                        if (e.getWhoClicked().hasPermission("stash.a")) {
//                            int integer = cursor.getAmount();
//                            String number = Integer.toString(integer);
//                            if (cursor.hasItemMeta()) {
//                                String cursorCustomItemName = cursor.getItemMeta().getDisplayName();
//                                //custom item
//                                giveMethods.recordItem(name, cursorCustomItemName, number, "added ", owner, playerId);
//                            } else {
//                                giveMethods.recordItem(name, cursorBukkitItemName, number, "added ", owner, playerId);
//                            }
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
//                            if (e.getClick().isShiftClick()) {
//                                int integer = slot.getAmount();
//                                String number = Integer.toString(integer);
//                                if (slot.hasItemMeta()) {
//                                    String slotCustomItemName = slot.getItemMeta().getDisplayName();
//                                    giveMethods.recordItem(name, slotCustomItemName, number, "added ", owner, playerId);
//                                } else {
//                                    String slotBukkitItemName = slot.toString();
//                                    giveMethods.recordItem(name, slotBukkitItemName, number, "added ", owner, playerId);
//                                }
//                            }
                            updatePlayers(stashStr, key);
                            return;
                        } else {
                            e.setCancelled(true);
                        }

                    }

                    if (!cursor.getType().isAir()) {
//                        if (cursor.hasItemMeta()) {
//                            String cursorCustomItemName = cursor.getItemMeta().getDisplayName();
//                            //custom item
//                            giveMethods.recordItem(name, cursorCustomItemName, number, "removed ", owner, playerId);
//                        } else {
//                            giveMethods.recordItem(name, cursorBukkitItemName, number, "removed ", owner, playerId);
//                        }
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
    public void drag(InventoryDragEvent e) {
        Inventory stashInv = MapConversion.map.get(e.getWhoClicked().getUniqueId().toString());
        if (stashInv.getViewers().contains(e.getWhoClicked())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void place(BlockPlaceEvent e) {
        System.out.println(e.getItemInHand().getItemMeta().getDisplayName());
        Block block = e.getBlock();

        if (block.getType().equals(Material.SPAWNER)) {
            NamespacedKey key = new NamespacedKey(Stash.plugin, "hostile");

            ItemMeta itemMeta = e.getItemInHand().getItemMeta();
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            if (container.has(key, PersistentDataType.STRING)) {
                String data = container.get(key, PersistentDataType.STRING);
                System.out.println(data);
            } else {
                System.out.println("No Data");
            }
        }
        /*
        TODO
         */




//        if (block.getType().equals(Material.SPAWNER)) {
//            BlockState bs = block.getState();
//            CreatureSpawner cs = (CreatureSpawner) bs;
//            cs.setSpawnedType(EntityType.BAT);
//            System.out.println("placed bat");
//        }
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


