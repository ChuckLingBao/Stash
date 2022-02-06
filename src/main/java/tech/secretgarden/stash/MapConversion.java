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
import java.sql.SQLException;
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
             PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Players (" +
                     "ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                     "UUID VARCHAR(36), " +
                     "Name VARCHAR(99), " +
                     "Inv TEXT(65000), " +
                     "Timestamp TIMESTAMP NOT NULL);")) {
            statement.executeUpdate();

        } catch (Exception x) {
            x.printStackTrace();
        }
        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Log (" +
                     "ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                     "Name VARCHAR(255), " +
                     "ItemName VARCHAR(255)," +
                     "Timestamp TIMESTAMP NOT NULL, " +
                     "Owner VARCHAR(255), " +
                     "PlayerKey INT, " +
                     "FOREIGN KEY (PlayerKey) REFERENCES Players(ID));")) {
            statement.executeUpdate();
        } catch (Exception x) {
            x.printStackTrace();
        }

        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT UUID, Inv FROM Players;")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String key = rs.getString("UUID");
                String value = rs.getString("Inv");
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
