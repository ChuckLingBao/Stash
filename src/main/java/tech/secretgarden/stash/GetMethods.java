package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class GetMethods {

    private final Database database = new Database();
    private final MapConversion mapConversion = new MapConversion();

    public String getName(UUID id) {
        if (Bukkit.getPlayer(id) != null) {
            return Bukkit.getPlayer(id).getName();
        } else {
            return Bukkit.getOfflinePlayer(id).getName();
        }
    }

    public String getMapKey(Inventory stash) {
        for (Map.Entry<String, Inventory> entry : MapConversion.map.entrySet()) {
            if (stash.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public int getPlayerId(String uuid) {
        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id FROM player WHERE uuid = '" + uuid + "'")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public Inventory getStashInv(String uuid) {
        try (Connection connection = database.getPool().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT inv FROM player where uuid = '" + uuid + "'")) {
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                String invString = rs.getString("inv");
                return mapConversion.stringToInventory(invString);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public boolean getWorld(String world) {
        int x = 0;
        for (String entry : Stash.worldList) {
            if (entry.equalsIgnoreCase(world)) {
                x = x + 1;
            }
        }
        return x == 0;
    }
}
