package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

public class EventListener implements Listener {

    private final Database database = new Database();
    private final MapConversion mapConversion = new MapConversion();
    private final GiveMethods giveMethods = new GiveMethods();

    LocalDateTime date = LocalDateTime.now();
    Timestamp timestamp = Timestamp.valueOf(date);

    @EventHandler
    //Stash inventory creation
    public void join(PlayerJoinEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();
        if (MapConversion.map.containsKey(e.getPlayer().getUniqueId().toString())) {
        } else {
            System.out.println(MapConversion.map.get(uuid));

            Inventory inv = Bukkit.createInventory(null, 18, ChatColor.DARK_PURPLE + "Stash");

            String player = e.getPlayer().getName();
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String invName = player + "'s Stash";

            MapConversion.map.put(uuid, inv);
            System.out.println("created inv");
            String stash = mapConversion.inventoryToString(inv);

            try (Connection connection = database.getPool().getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO Players (UUID, Name, Inv, Timestamp) VALUES (?,?,?,?);")) {
                statement.setString(1, uuid);
                statement.setString(2, player);
                statement.setString(3, stash);
                statement.setTimestamp(4, timestamp);
                statement.executeUpdate();

            } catch (Exception x) {
                x.printStackTrace();
            }

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
            } else if (e.getClick().isRightClick()) {
                e.setCancelled(true);
                //if user tries to swap item or right click, cancel event regardless of their permissions!
            } else if (e.getClickedInventory() == null) {
                e.setCancelled(true);
            } else {
                Inventory stash = e.getView().getTopInventory();
                //for (Map.Entry<String, Inventory> entry : MapConversion.map.entrySet()) {
                //if (entry.getValue().equals(stash)) {
                //finds map key value pair
                //value.equals(stash)
                Inventory playerInventory = e.getWhoClicked().getInventory();
                String key = getKey(stash);
                UUID uuid = UUID.fromString(key);
                String owner = getName(uuid);
                String name = e.getWhoClicked().getName();
                String stashStr = mapConversion.inventoryToString(stash);
                int playerKey = getForeignKey(key);

                            /*
                            try (Connection connection = database.getPool().getConnection();
                                 PreparedStatement statement = connection.prepareStatement("SELECT ID FROM Players WHERE UUID = '" + uuid + "'")) {
                                ResultSet rs = statement.executeQuery();
                                while (rs.next()) {
                                    int playerKey = rs.getInt("ID");

                             */
                String cursorBukkitItemName = cursor.toString();
                if (e.getClickedInventory().equals(stash)) {
                    //clicked stash inventory
                    if (cursor.getType().isAir() && slot != null) {
                        String slotBukkitItemName = slot.toString();
                        //removing item from stash
                        int integer = slot.getAmount();
                        String number = Integer.toString(integer);
                        if (e.getClick().isShiftClick()) {
                            if (slot.hasItemMeta()) {
                                String slotCustomItemName = slot.getItemMeta().getDisplayName();
                                //custom item
                                giveMethods.recordItem(name, slotCustomItemName, number, "removed ", owner, playerKey);
                            } else {
                                giveMethods.recordItem(name, slotBukkitItemName, number, "removed ", owner, playerKey);
                            }
                        }
                        giveMethods.updatePlayers(stashStr, key);

                    } else if (!cursor.getType().isAir() && slot == null) {
                        //adding item to stash
                        if (e.getWhoClicked().hasPermission("stash.a")) {
                            int integer = cursor.getAmount();
                            String number = Integer.toString(integer);
                            if (cursor.hasItemMeta()) {
                                String cursorCustomItemName = cursor.getItemMeta().getDisplayName();
                                //custom item
                                giveMethods.recordItem(name, cursorCustomItemName, number, "added ", owner, playerKey);
                            } else {
                                giveMethods.recordItem(name, cursorBukkitItemName, number, "added ", owner, playerKey);
                            }
                            giveMethods.updatePlayers(stashStr, key);
                        } else {
                            e.setCancelled(true);
                        }

                    }
                } else if (e.getClickedInventory().equals(playerInventory)) {
                    //clicked player inventory
                    if (cursor.getType().isAir() && slot != null) {
                        //removing item from player inv
                        if (e.getWhoClicked().hasPermission("stash.a")) {
                            if (e.getClick().isShiftClick()) {
                                int integer = slot.getAmount();
                                String number = Integer.toString(integer);
                                if (slot.hasItemMeta()) {
                                    String slotCustomItemName = slot.getItemMeta().getDisplayName();
                                    giveMethods.recordItem(name, slotCustomItemName, number, "added ", owner, playerKey);
                                } else {
                                    String slotBukkitItemName = slot.toString();
                                    giveMethods.recordItem(name, slotBukkitItemName, number, "added ", owner, playerKey);
                                }

                            }

                            giveMethods.updatePlayers(stashStr, key);
                        } else {
                            e.setCancelled(true);
                        }

                    } else if (!cursor.getType().isAir() && slot == null) {
                        int integer = cursor.getAmount();
                        String number = Integer.toString(integer);
                        if (cursor.hasItemMeta()) {
                            String cursorCustomItemName = cursor.getItemMeta().getDisplayName();
                            //custom item
                            giveMethods.recordItem(name, cursorCustomItemName, number, "removed ", owner, playerKey);
                        } else {
                            giveMethods.recordItem(name, cursorBukkitItemName, number, "removed ", owner, playerKey);
                        }
                        giveMethods.updatePlayers(stashStr, key);
                        //adding item to player inv

                        giveMethods.updatePlayers(stashStr, key);
                    }

                }
            }

                        /*
                            } catch (SQLException x) {
                                x.printStackTrace();
                            }

                         */


        }


    }

            /*
            if (e.getWhoClicked().hasPermission("stash.a")) {
                //checking if player is admin
                e.setCancelled(false);
                int integer = e.getCursor().getAmount();
                String number = Integer.toString(integer);
                if (e.getCursor().hasItemMeta()) {
                    String itemName = e.getCursor().getItemMeta().getDisplayName();
                    giveMethods.recordItem(uuid, itemName, number, "removed");
                    giveMethods.updatePlayers(stashString, uuid);
                } else {
                    String itemName = e.getCursor().toString();
                    giveMethods.recordItem(uuid, itemName, number, "removed");
                    giveMethods.updatePlayers(stashString, uuid);
                }

            } else if (e.getClickedInventory() == null) {
                e.setCancelled(true);
            } else if (e.getClickedInventory().equals(playerInventory)) {
                //if player is trying to click slot in their PlayerInventory:
                if (e.getCurrentItem() == null) {
                    //clicked on an air block
                    if (!e.getCursor().getType().isAir()) {
                        int integer = e.getCursor().getAmount();
                        String number = Integer.toString(integer);
                        //had itemStack in cursor
                        e.setCancelled(false);
                        if (e.getClick().isRightClick()) {
                            e.setCancelled(true);
                        } else if (e.getCursor().hasItemMeta()) {
                            String itemName = e.getCursor().getItemMeta().getDisplayName();
                            giveMethods.recordItem(uuid, itemName, number, "removed");
                            giveMethods.updatePlayers(stashString, uuid);
                        } else {
                            String itemName = e.getCursor().toString();
                            giveMethods.recordItem(uuid, itemName, number, "removed");
                            giveMethods.updatePlayers(stashString, uuid);
                        }

                    } else {
                        //had nothing in cursor
                        e.setCancelled(false);
                    }
                    //e.getWhoClicked().sendMessage("You cannot put items into the Stash");
                } else {
                    //clicked on itemStack
                    e.setCancelled(true);
                }
            } else if (e.getClickedInventory().equals(stashInv)) {
                //player clicks slot in stash
                if (e.getCurrentItem() != null && e.getCursor().getType().isAir()) {
                    if (e.getClick().isShiftClick()) {
                        int integer = e.getCurrentItem().getAmount();
                        String number = Integer.toString(integer);
                        if (e.getCurrentItem().hasItemMeta()) {
                            String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
                            giveMethods.recordItem(uuid, itemName, number, "removed");
                            giveMethods.updatePlayers(stashString, uuid);
                        } else {
                            String itemName = e.getCurrentItem().toString();
                            giveMethods.recordItem(uuid, itemName, number, "removed");
                            giveMethods.updatePlayers(stashString, uuid);
                        }
                    }
                    e.setCancelled(false);
                } else if (!e.getCursor().getType().isAir()) {
                    e.setCancelled(true);
                }
            }

             */


    @EventHandler
    public void drag(InventoryDragEvent e) {
        Inventory stashInv = MapConversion.map.get(e.getWhoClicked().getUniqueId().toString());
        if (stashInv.getViewers().contains(e.getWhoClicked())) {
            e.setCancelled(true);
        }
    }

    public String getName(UUID id) {
        if (Bukkit.getPlayer(id) != null) {
            String owner = Bukkit.getPlayer(id).getName();
            return owner;
        } else {
            String owner = Bukkit.getOfflinePlayer(id).getName();
            return owner;
        }
    }

    public String getKey(Inventory stash) {
        for (Map.Entry<String, Inventory> entry : MapConversion.map.entrySet()) {
            if (stash.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public int getForeignKey(String uuid) {
        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT ID FROM Players WHERE UUID = '" + uuid + "'")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int playerKey = rs.getInt("ID");
                return playerKey;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }
}


