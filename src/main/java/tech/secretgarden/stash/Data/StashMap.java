package tech.secretgarden.stash.Data;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import tech.secretgarden.stash.Stash;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class StashMap {
    Database database = new Database();

    //initialized HashMap
    public static HashMap<String, Stash> map = new HashMap<>();
    //inventoryMap -> String
    private static HashMap<String, String> stringMap = new HashMap<>();

    public String serializeStash(Stash stash) {
        try {
            ByteArrayOutputStream str = new ByteArrayOutputStream();
            BukkitObjectOutputStream data = new BukkitObjectOutputStream(str);
            data.writeObject(stash);
            data.close();
            return Base64.getEncoder().encodeToString(str.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Stash deserializeStash(String stashData) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(stashData));
            BukkitObjectInputStream data = new BukkitObjectInputStream(stream);
            Stash stash = (Stash) data.readObject();
            data.close();
            return stash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BukkitRunnable addPlayer(String uuid, String name, Timestamp timestamp) {

        Stash stash = new Stash();
        map.put(uuid, stash);

        BukkitRunnable add = new BukkitRunnable() {
            @Override
            public void run() {

                String stashString = serializeStash(stash);

                try (Connection connection = database.getPool().getConnection();
                     PreparedStatement statement = connection.prepareStatement("INSERT INTO player (uuid, name, stash, timestamp) VALUES (?,?,?,?);")) {
                    statement.setString(1, uuid);
                    statement.setString(2, name);
                    statement.setString(3, stashString);
                    statement.setTimestamp(4, timestamp);
                    statement.executeUpdate();

                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        };
        return add;
    }

    public void loadMap() {

        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS stash_player_items (" +
                     "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                     "uuid VARCHAR(36), " +
                     "name VARCHAR(99), " +
                     "stash TEXT(65000), " +
                     "timestamp TIMESTAMP NOT NULL," +
                     "last_played TIMESTAMP NOT NULL);")) {
            statement.executeUpdate();

        } catch (Exception x) {
            x.printStackTrace();
        }
        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS log (" +
                     "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                     "name VARCHAR(255), " +
                     "item_name VARCHAR(255)," +
                     "timestamp TIMESTAMP NOT NULL, " +
                     "owner VARCHAR(255), " +
                     "player_key INT, " +
                     "FOREIGN KEY (player_key) REFERENCES player(id));")) {
            statement.executeUpdate();
        } catch (Exception x) {
            x.printStackTrace();
        }

        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT uuid, inv FROM stash_player_items;")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String key = rs.getString("uuid");
                String value = rs.getString("stash");
                stringMap.put(key, value);
            }
            for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                String stashData = entry.getValue();
                Stash stash = deserializeStash(stashData);
                String uuid = entry.getKey();
                map.put(uuid, stash);
            }

        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
