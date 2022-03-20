package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class MapConversion {
    Database database = new Database();

    //initialized HashMap
    static HashMap<String, Inventory> map = new HashMap<>();
    //inventoryMap -> String
    static HashMap<String, String> stringMap = new HashMap<>();

    public String inventoryToString(Inventory inventory) {
        try {
            ByteArrayOutputStream str = new ByteArrayOutputStream();
            BukkitObjectOutputStream data = new BukkitObjectOutputStream(str);

            data.writeInt(inventory.getSize());
            data.writeObject(ChatColor.DARK_PURPLE + "Stash");
            for (int i = 0; i < inventory.getSize(); i++) {
                data.writeObject(inventory.getItem(i));
            }
            data.close();
            return Base64.getEncoder().encodeToString(str.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Inventory stringToInventory(String inventoryData) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(inventoryData));
            BukkitObjectInputStream data = new BukkitObjectInputStream(stream);
            Inventory inventory = Bukkit.createInventory(null, data.readInt(), data.readObject().toString());
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) data.readObject());
            }
            data.close();
            return inventory;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadMap() {

        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS player (" +
                     "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                     "uuid VARCHAR(36), " +
                     "name VARCHAR(99), " +
                     "inv TEXT(65000), " +
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
             PreparedStatement statement = connection.prepareStatement("SELECT uuid, inv FROM player;")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String key = rs.getString("uuid");
                String value = rs.getString("inv");
                stringMap.put(key, value);
            }
            for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                String inventoryData = entry.getValue();
                Inventory inv = stringToInventory(inventoryData);
                String uuid = entry.getKey();
                map.put(uuid, inv);
            }

        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
