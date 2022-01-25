package tech.secretgarden.stash;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

public class GiveMethods {
    Database database = new Database();
    MapConversion mapConversion = new MapConversion();
    public void giveSinglePlayer(String[] args, Inventory singleStash, ItemStack item, Player player, String uuid, String itemName) {
        if (args.length == 3) {
            String number = "1";
            singleStash.addItem(item);
            String stash = mapConversion.inventoryToString(singleStash);
            updatePlayers(stash, uuid);
            recordAddedItem(uuid, itemName, number);
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
                    recordAddedItem(uuid, itemName, number);
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
                    recordAddedItem(uuid, itemName, number);
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
                            recordAddedItem(uuid, itemName, number);
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
                    "DATECREATED = ? " +
                    "WHERE UUID = ?;");
            ps.setString(1, stashString);
            ps.setTimestamp(2, timestamp);
            ps.setString(3, uuid);
            ps.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void recordAddedItem(String uuid, String itemName, String number) {
        LocalDateTime date = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(date);
        try {
            PreparedStatement ps = database.getConnection().prepareStatement("INSERT INTO `" + uuid + "` " +
                    "(TIMESTAMP, ITEM_NAME) VALUES (?,?);");
            ps.setTimestamp(1, timestamp);
            ps.setString(2, "Gave " + itemName + " x" + number);
            ps.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
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
