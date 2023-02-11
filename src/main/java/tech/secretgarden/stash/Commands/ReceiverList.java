package tech.secretgarden.stash.Commands;

import org.bukkit.inventory.Inventory;
import tech.secretgarden.stash.Data.Database;
import tech.secretgarden.stash.Data.GetMethods;
import tech.secretgarden.stash.Data.MapConversion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class ReceiverList {

    Database database = new Database();
    GetMethods getMethods = new GetMethods();
    MapConversion mapConversion = new MapConversion();

    ArrayList<String> list = null;
    StashCmdContents contents = null;
    int quantity = 0;

    public ReceiverList(StashCmdContents contents) {
        this.contents = contents;
        this.quantity = contents.getQuantity();
        Timestamp timestamp = contents.getTimestamp();

        ArrayList<String> list = new ArrayList<>();
        String receiver = contents.getReceiver();

        if (receiver.equalsIgnoreCase("all")) {
            for (Map.Entry<String, Inventory> entry : MapConversion.map.entrySet()) {
                list.add(entry.getKey());
            }
        } else if (receiver.equalsIgnoreCase("time")) {
            //find database timestamps after the one created in contents.
            System.out.println("time receiver list creation started");
            try (Connection connection = database.getPool().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM player WHERE last_played >= ?")) {
                statement.setTimestamp(1, timestamp);
                ResultSet rs = statement.executeQuery();
                System.out.println("Selected from db");
                while (rs.next()) {
                    list.add(rs.getString("uuid"));
                    System.out.println(rs.getString("name"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            //single player list
            String uuid = getMethods.getIdString(receiver);
            list.add(uuid);
        }
        this.list = list;
    }

    public void addItem() {
        for (String id : list) {
            Inventory inv = MapConversion.map.get(id);
//            Inventory inv = getMethods.getStashInv(id);
            for (int i = 0; i < quantity; i++) {
                inv.addItem(contents.item);
            }
            updatePlayers(inv, id);
            recordItem(true, id);
        }
    }

    private void updatePlayers(Inventory inv, String uuid) {
        String stashString = mapConversion.inventoryToString(inv);
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

    private void recordItem(boolean added, String id) {
        int playerKey = getMethods.getPlayerId(id);
        LocalDateTime date = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(date);
        String addOrRemove = null;
        if (added) {
            addOrRemove = "added";
        } else {
            addOrRemove = "removed";
        }

        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO log " +
                     "(timestamp, item_name, name, owner, player_key) VALUES (?,?,?,?,?);")) {
            statement.setTimestamp(1, timestamp);
            statement.setString(2, addOrRemove + " " + contents.itemName + " x" + quantity);
            statement.setString(3, contents.giver);
            statement.setString(4, id);
            statement.setInt(5, playerKey);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
