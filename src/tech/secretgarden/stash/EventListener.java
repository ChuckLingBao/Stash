package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
            try {
                PreparedStatement ps = database.getConnection().prepareStatement("INSERT INTO players (UUID, NAME, INV, INVNAME, DATECREATED) VALUES (?,?,?,?,?);");
                ps.setString(1, uuid);
                ps.setString(2, player);
                ps.setString(3, stash);
                ps.setString(4, invName);
                ps.setTimestamp(5, timestamp);
                ps.executeUpdate();

                PreparedStatement createTable = database.getConnection().prepareStatement("CREATE TABLE " + "`" + uuid + "`"  + " (" +
                        "ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "TIMESTAMP TIMESTAMP NOT NULL, " +
                        "ITEM_NAME VARCHAR(255)," +
                        "NAME VARCHAR(255));");
                createTable.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        if (!MapConversion.map.get(e.getPlayer().getUniqueId().toString()).isEmpty()) {
            e.getPlayer().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You have Items in your Stash, use '/stash' to get your items.");
        }
    }

    @EventHandler
    public void invClick(InventoryClickEvent e) {

        Inventory playerInventory = e.getWhoClicked().getInventory();
        Inventory stashInv = MapConversion.map.get(e.getWhoClicked().getUniqueId().toString());
        String stashString = mapConversion.inventoryToString(stashInv);
        String uuid = e.getWhoClicked().getUniqueId().toString();

        //checks if player is  viewing their stash
        if (stashInv.getViewers().contains(e.getWhoClicked())) {
            if (e.getWhoClicked().hasPermission("stash.a")) {
                //checking if player is admin
                e.setCancelled(false);

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
        }
    }

    @EventHandler
    public void drag(InventoryDragEvent e) {
        Inventory stashInv = MapConversion.map.get(e.getWhoClicked().getUniqueId().toString());
        if (stashInv.getViewers().contains(e.getWhoClicked())) {
            e.setCancelled(true);
        }
    }
}


