package tech.secretgarden.stash;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.secretgarden.stash.MapConversion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

public class GiveMethods {
    Database database = new Database();
    MapConversion mapConversion = new MapConversion();
    String add = "added";
    public void giveSinglePlayer(String[] args, Inventory singleStash, ItemStack item, Player player, String uuid, String itemName) {
        if (args.length == 3) {
            String number = "1";
            singleStash.addItem(item);
            String stash = mapConversion.inventoryToString(singleStash);
            updatePlayers(stash, uuid);
            recordItem(uuid, itemName, number, add);
        } else {
            try {
                String number = args[3];

                int integer = Integer.parseInt(number);
                if (integer <= item.getMaxStackSize()) {
                    for (int i = 0; i < integer; i++) {
                        singleStash.addItem(item);
                    }
                    String stashString = mapConversion.inventoryToString(singleStash);
                    updatePlayers(stashString, uuid);
                    recordItem(uuid, itemName, number, add);
                }
            } catch (NumberFormatException nfe) {
                player.sendMessage("This argument must be an integer");
            }
        }
    }
    public void giveAllPlayers(String[] args, ItemStack item, Player player, String itemName) {
        if (args[1].equals("all")) {

            if (args.length == 3) {
                String number = "1";
                for (Map.Entry entry : mapConversion.map.entrySet()) {
                    Inventory stashInv = (Inventory) entry.getValue();
                    String uuid = (String) entry.getKey();
                    stashInv.addItem(item);
                    String stashString = mapConversion.inventoryToString(stashInv);

                    updatePlayers(stashString, uuid);
                    recordItem(uuid, itemName, number, add);
                }
            } else if (args.length == 4) {
                try {
                    String number = args[3];
                    int integer = Integer.parseInt(number);
                    if (integer <= item.getMaxStackSize()) {
                        for (Map.Entry entry : mapConversion.map.entrySet()) {
                            Inventory stashInv = (Inventory) entry.getValue();
                            String uuid = (String) entry.getKey();
                            for (int i = 0; i < integer; i++) {
                                stashInv.addItem(item);
                            }
                            String stash = mapConversion.inventoryToString(stashInv);
                            updatePlayers(stash, uuid);
                            recordItem(uuid, itemName, number, add);
                        }
                        //configAddAll(itemName, integer);
                    }
                } catch (NumberFormatException nfe) {
                    player.sendMessage("This argument must be an integer");
                }
            }
            //plugin.configAddAll(itemName, integer);
        }


    }

    public void updatePlayers(String stashString, String uuid) {
        LocalDateTime date = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(date);

        try {
            PreparedStatement ps = database.getConnection().prepareStatement("UPDATE players " +
                    "SET INV = ?, " +
                    "TIMESTAMP = ? " +
                    "WHERE UUID = ?;");
            ps.setString(1, stashString);
            ps.setTimestamp(2, timestamp);
            ps.setString(3, uuid);
            ps.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        /*
        try (Connection connection = database.getHikari().getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE players " +
                    "SET INV = ?, " +
                    "DATECREATED = ? " +
                    "WHERE UUID = ?;")) {
            statement.setString(1, stashString);
            statement.setTimestamp(2, timestamp);
            statement.setString(3, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

         */
    }

    public void recordItem(String uuid, String itemName, String number, String addOrRemove) {
        LocalDateTime date = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(date);
        try {
            PreparedStatement ps = database.getConnection().prepareStatement("INSERT INTO `" + uuid + "` " +
                    "(TIMESTAMP, ITEM_NAME) VALUES (?,?);");
            ps.setTimestamp(1, timestamp);
            ps.setString(2, addOrRemove + " " + itemName + " x" + number);
            ps.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        /*
        try (Connection connection = database.getHikari().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO `" + uuid + "` " +
                     "(TIMESTAMP, ITEM_NAME) VALUES (?,?);")) {
            statement.setTimestamp(1, timestamp);
            statement.setString(2, addOrRemove + " " + itemName + " x" + number);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

         */
    }

    public void updateAllPlayers() {
        for (Map.Entry<String, Inventory> entry : MapConversion.map.entrySet()) {
            String uuid = entry.getKey();
            Inventory inv = entry.getValue();
            String stashString = mapConversion.inventoryToString(inv);
            updatePlayers(stashString, uuid);
        }
    }
}
