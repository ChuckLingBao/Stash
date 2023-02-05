package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;

public class InsertData {

    private final Database database = new Database();
    private final MapConversion mapConversion = new MapConversion();


    public BukkitRunnable addPlayer(String uuid, String name, Timestamp timestamp) {

        Inventory inv = Bukkit.createInventory(null, 18, ChatColor.DARK_PURPLE + "Stash");
        MapConversion.map.put(uuid, inv);

        BukkitRunnable add = new BukkitRunnable() {
            @Override
            public void run() {

                System.out.println("created inv");
                String stash = mapConversion.inventoryToString(inv);

                try (Connection connection = database.getPool().getConnection();
                     PreparedStatement statement = connection.prepareStatement("INSERT INTO player (uuid, name, inv, timestamp) VALUES (?,?,?,?);")) {
                    statement.setString(1, uuid);
                    statement.setString(2, name);
                    statement.setString(3, stash);
                    statement.setTimestamp(4, timestamp);
                    statement.executeUpdate();

                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        };

        return add;

    }
}
