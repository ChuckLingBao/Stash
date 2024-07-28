package tech.secretgarden.stash.Commands;

import org.bukkit.inventory.ItemStack;
import tech.secretgarden.stash.Data.Database;
import tech.secretgarden.stash.Data.StashAPI;
import tech.secretgarden.stash.Data.StashMap;
import tech.secretgarden.stash.Stash;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class ReceiverList {

    Database database = new Database();
    StashAPI stashAPI = new StashAPI();
    StashMap stashMap = new StashMap();

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
            for (Map.Entry<String, Stash> entry : StashMap.map.entrySet()) {
                list.add(entry.getKey());
            }
        } else if (receiver.equalsIgnoreCase("time")) {
            //find database timestamps after the one created in contents.
            System.out.println("time receiver list creation started");
            try (Connection connection = database.getPool().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM stashes WHERE last_played >= ?")) {
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
            String uuid = stashAPI.getIdString(receiver);
            list.add(uuid);
        }
        this.list = list;
    }

    public void addItem() {
        for (String id : list) {
            Stash stash = StashMap.map.get(id);
//            Inventory inv = getMethods.getStashInv(id);
            for (int i = 0; i < quantity; i++) {
                stash.add(contents.item);
            }
            updatePlayers(stash, id);
            recordItem(true, id);
        }
    }

    private void updatePlayers(Stash stash, String uuid) {
        String stashString = stashMap.serializeStash(stash);
        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE stashes " +
                     "SET stash = ? " +
                     "WHERE uuid = ?;")) {
            statement.setString(1, stashString);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void recordItem(boolean added, String id) {
        int playerKey = stashAPI.getPlayerId(id);
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
