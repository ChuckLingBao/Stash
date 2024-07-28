package tech.secretgarden.stash.Data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import tech.secretgarden.stash.Stash;
import tech.secretgarden.stash.StashPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class StashAPI {

    private final Database database = new Database();
    private final StashMap stashMap = new StashMap();

    public String getName(UUID id) {
        if (Bukkit.getPlayer(id) != null) {
            return Bukkit.getPlayer(id).getName();
        } else {
            return Bukkit.getOfflinePlayer(id).getName();
        }
    }

//    public String getMapKey(ArrayList<ItemStack> stash) {
//        for (Map.Entry<String, ArrayList<ItemStack>> entry : StashMap.map.entrySet()) {
//            if (stash.equals(entry.getValue())) {
//                return entry.getKey();
//            }
//        }
//        return null;
//    }

    public String getStashViewOwner(InventoryView view, Player viewer) {
        String title = view.getTitle();
        if (title.contains(ChatColor.DARK_PURPLE + "Stash")) {
            if (title.length() > 5) {
                String name = title.substring(0, title.length() - 6);
                return getIdString(name);
            } else {
                return viewer.getUniqueId().toString();
            }
        }
        return "";
    }

    public int getPlayerId(String uuid) {
        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id FROM stashes WHERE uuid = '" + uuid + "'")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public Stash getStash(String uuid) {
        try (Connection connection = database.getPool().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT stash FROM stashes where uuid = '" + uuid + "'")) {
            ResultSet rs = statement.executeQuery();
            String stashString = rs.getString("stash");
            return stashMap.deserializeStash(stashString);

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

//    public Inventory getStashInv(String uuid, int pageNum) {
//        ArrayList<ItemStack> stashItems = getStashArray(uuid);
//    }

    public boolean getWorld(String world) {
        int x = 0;
        for (String entry : StashPlugin.worldList) {
            if (entry.equalsIgnoreCase(world)) {
                x = x + 1;
            }
        }
        return x == 0;
    }

    public String getIdString(String player) {

        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT uuid FROM ranks WHERE gamertag = '" + player + "'")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String uuid = rs.getString("uuid");
                System.out.println(uuid);
                return uuid;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
