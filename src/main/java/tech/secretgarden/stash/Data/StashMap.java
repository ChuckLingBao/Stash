package tech.secretgarden.stash.Data;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
import java.time.LocalDateTime;
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
//            ByteArrayOutputStream str = new ByteArrayOutputStream();
//            BukkitObjectOutputStream data = new BukkitObjectOutputStream(str);
//            data.writeObject(stash);
//            data.close();
//            return Base64.getEncoder().encodeToString(str.toByteArray());
            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.writeValueAsString(stash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Stash deserializeStash(String stashData) {
        try {
//            ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(stashData));
//            BukkitObjectInputStream data = new BukkitObjectInputStream(stream);
//            Stash stash = (Stash) data.readObject();
//            data.close();
//            return stash;
            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.readValue(stashData, Stash.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BukkitRunnable addPlayer(String uuid, String name, Timestamp timestamp) {

        Stash stash = new Stash(uuid);
        map.put(uuid, stash);

        BukkitRunnable add = new BukkitRunnable() {
            @Override
            public void run() {

                String stashString = serializeStash(stash);

                try (Connection connection = database.getPool().getConnection();
                     PreparedStatement statement = connection.prepareStatement("INSERT INTO stashes (uuid, name, stash, timestamp, last_played) VALUES (?,?,?,?,?);")) {
                    statement.setString(1, uuid);
                    statement.setString(2, name);
                    statement.setString(3, stashString);
                    statement.setTimestamp(4, timestamp);
                    statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
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
             PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS stashes (" +
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
                     "FOREIGN KEY (player_key) REFERENCES stashes(id));")) {
            statement.executeUpdate();
        } catch (Exception x) {
            x.printStackTrace();
        }

        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT uuid, stash FROM stashes;")) {
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
