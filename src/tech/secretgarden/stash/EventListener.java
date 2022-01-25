package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
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

        //checks if player is not viewing their stash
        if (!(stashInv.getViewers().contains(e.getWhoClicked()))) {
            e.setCancelled(false);
        }
        //if they are:
        else {
            if (e.getWhoClicked().hasPermission("stash.a")) {
                //checking if player is admin
                e.setCancelled(false);
            }
            else if (e.getClickedInventory().equals(playerInventory)) {
                //if player is not currently holding something with cursor and is trying to click slot in their PlayerInventory:
                if (e.getCursor().getType().isAir()) {
                    e.setCancelled(true);
                    e.getWhoClicked().sendMessage("You cannot put items into the Stash");
                }
            }
        }
    }

    @EventHandler
    public void invMove(InventoryMoveItemEvent e) {

        if (e.getSource().getViewers().get(0) instanceof Player) {

            Player player = (Player) e.getSource().getViewers().get(0);
            //player that opened the stash inventory
            String uuid = player.getUniqueId().toString();
            Inventory stash = MapConversion.map.get(uuid);
            System.out.println(player + uuid + stash);
            if(e.getSource().equals(stash)) {
                String stashString = mapConversion.inventoryToString(stash);
                String itemName = e.getItem().getItemMeta().getDisplayName();
                int integer = e.getItem().getAmount();
                String number = Integer.toString(integer);
                giveMethods.updatePlayers(stashString, uuid);
                giveMethods.recordAddedItem(uuid, itemName, number);

                /*
                try {
                    PreparedStatement ps = database.getConnection().prepareStatement("UPDATE PLAYERS " +
                            "SET INV = ?, " +
                            "DATECREATED = ? " +
                            "WHERE UUID = ?;");
                    ps.setString(1, stashString);
                    ps.setTimestamp(2, timestamp);
                    ps.setString(3, uuid);
                    ps.executeUpdate();

                    PreparedStatement removeRecord = database.getConnection().prepareStatement("INSERT INTO `" +
                            uuid + "` " +
                            "(TIMESTAMP, ITEM_NAME) VALUES (?,?);");
                    removeRecord.setTimestamp(1, timestamp);
                    removeRecord.setString(2, "Gave " + itemName + " x" + number);
                    removeRecord.executeUpdate();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                 */

            }
        }
    }
}
