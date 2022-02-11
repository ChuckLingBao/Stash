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

    public String getName(UUID id) {
        if (Bukkit.getPlayer(id) != null) {
            String owner = Bukkit.getPlayer(id).getName();
            return owner;
        } else {
            String owner = Bukkit.getOfflinePlayer(id).getName();
            return owner;
        }
    }

    public String getKey(Inventory stash) {
        for (Map.Entry<String, Inventory> entry : MapConversion.map.entrySet()) {
            if (stash.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public int getForeignKey(String uuid) {
        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT ID FROM player WHERE uuid = '" + uuid + "'")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public boolean getWorld(String world) {
        int x = 0;
        for (String entry : Main.worldList) {
            if (entry.equalsIgnoreCase(world)) {
                x = x + 1;
            }
        }
        return x == 0;
    }
}
