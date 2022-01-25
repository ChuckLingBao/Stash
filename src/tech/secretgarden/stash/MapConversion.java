package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
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

        try {
            PreparedStatement ps = database.getConnection().prepareStatement("SELECT UUID, INV FROM PLAYERS;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String key = rs.getString("UUID");
                String value = rs.getString("INV");
                stringMap.put(key, value);
                System.out.println(key + value);
            }
            for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                String inventoryData = entry.getValue();
                Inventory inv = stringToInventory(inventoryData);
                String uuid = entry.getKey();
                map.put(uuid, inv);
                System.out.println(uuid + inv);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    //Saving HashMap
    /*
    public void saveMap() {
        for (Map.Entry<String, Inventory> entry : map.entrySet()) {
            //entry is a single entry in the map
            Inventory inventory = entry.getValue();
            inventoryToString(inventory);

            //put String value in a new HashMap
            stringMap.put(entry.getKey(), inventoryToString(inventory));

            //Time to serialize stringMap
            try {
                FileOutputStream fos = new FileOutputStream("plugins/Stash/data.ser");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(stringMap);
                oos.close();
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Loading HashMap
    public void loadMap() {
        //decode stringMap (data.ser)
        try {
            FileInputStream fis = new FileInputStream("plugins/Stash/data.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            stringMap = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        //time to turn stringMap into map
        for (Map.Entry<String, String> entry : stringMap.entrySet()) {

            String inventoryData = entry.getValue();
            stringToInventory(inventoryData);

            //put Inventory value in map.
            map.put(entry.getKey(), stringToInventory(inventoryData));
        }
    }
     */
}
