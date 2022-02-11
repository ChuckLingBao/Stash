package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.secretgarden.stash.MapConversion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class GiveMethods {
    private final Database database = new Database();
    private final MapConversion mapConversion = new MapConversion();
    private final GetMethods getMethods = new GetMethods();

    String add = "added";

    public void giveSinglePlayer(String[] args, Inventory singleStash, ItemStack item, Player player, String idString, String itemName) {
        UUID uuid = UUID.fromString(idString);
        String owner = getMethods.getName(uuid);
        String sender = player.getName();
        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id FROM player WHERE uuid = '" + idString + "'")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int playerKey = rs.getInt("id");
                if (args.length == 3) {
                    String number = "1";
                    singleStash.addItem(item);
                    String stash = mapConversion.inventoryToString(singleStash);
                    updatePlayers(stash, idString);

                    recordItem(sender, itemName, number, add, owner, playerKey);
                } else {
                    try {
                        String number = args[3];

                        int integer = Integer.parseInt(number);
                        if (integer <= item.getMaxStackSize()) {
                            for (int i = 0; i < integer; i++) {
                                singleStash.addItem(item);
                            }
                            String stashString = mapConversion.inventoryToString(singleStash);
                            updatePlayers(stashString, idString);
                            recordItem(sender, itemName, number, add, owner, playerKey);
                        }
                    } catch (NumberFormatException nfe) {
                        player.sendMessage("This argument must be an integer");
                    }
                }
            }
        } catch (SQLException x) {
            x.printStackTrace();
        }

    }
    public void giveAllPlayers(String[] args, ItemStack item, Player player, String itemName) {
        if (args[1].equals("all")) {

            String name = player.getDisplayName();

            if (args.length == 3) {
                String number = "1";
                for (Map.Entry<String, Inventory> entry : MapConversion.map.entrySet()) {
                    Inventory stashInv = entry.getValue();
                    String idString = entry.getKey();
                    stashInv.addItem(item);
                    String stashString = mapConversion.inventoryToString(stashInv);
                    String owner = Bukkit.getPlayer(entry.getKey()).getDisplayName();
                    try (Connection connection = database.getPool().getConnection();
                         PreparedStatement statement = connection.prepareStatement("SELECT id FROM player WHERE uuid = '" + idString + "'")) {
                        ResultSet rs = statement.executeQuery();
                        while (rs.next()) {
                            int playerKey = rs.getInt("id");
                            updatePlayers(stashString, idString);
                            recordItem(name, itemName, number, add, owner, playerKey);
                        }
                    } catch (SQLException x) {
                        x.printStackTrace();
                    }
                }
            } else if (args.length == 4) {
                try {
                    String number = args[3];
                    int integer = Integer.parseInt(number);
                    if (integer <= item.getMaxStackSize()) {
                        for (Map.Entry<String, Inventory> entry : MapConversion.map.entrySet()) {
                            Inventory stashInv = entry.getValue();
                            String uuid = entry.getKey();
                            String owner = Bukkit.getPlayer(entry.getKey()).getDisplayName();
                            try (Connection connection = database.getPool().getConnection();
                                 PreparedStatement statement = connection.prepareStatement("SELECT id FROM player WHERE uuid = '" + uuid + "'")) {
                                ResultSet rs = statement.executeQuery();
                                while (rs.next()) {
                                    int playerKey = rs.getInt("id");
                                    for (int i = 0; i < integer; i++) {
                                        stashInv.addItem(item);
                                    }
                                    String stash = mapConversion.inventoryToString(stashInv);
                                    updatePlayers(stash, uuid);
                                    recordItem(name, itemName, number, add, owner, playerKey);
                                }
                            } catch (SQLException x) {
                                x.printStackTrace();
                            }

                        }
                    }
                } catch (NumberFormatException nfe) {
                    player.sendMessage("This argument must be an integer");
                }
            }
        }


    }

    public void updatePlayers(String stashString, String uuid) {
        LocalDateTime date = LocalDateTime.now();

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

    public void recordItem(String name, String itemName, String number, String addOrRemove, String owner, int playerKey) {
        LocalDateTime date = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(date);

        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO log " +
                     "(timestamp, item_name, name, owner, player_key) VALUES (?,?,?,?,?);")) {
            statement.setTimestamp(1, timestamp);
            statement.setString(2, addOrRemove + " " + itemName + " x" + number);
            statement.setString(3, name);
            statement.setString(4, owner);
            statement.setInt(5, playerKey);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
