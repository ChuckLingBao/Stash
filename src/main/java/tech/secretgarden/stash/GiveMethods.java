package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GiveMethods {
    private final Database database = new Database();
    private final MapConversion mapConversion = new MapConversion();
    private final GetMethods getMethods = new GetMethods();

    String add = "added";

    public void giveSinglePlayer(String[] args, Inventory singleStash, ItemStack item, Player player, String idString, String itemName) {
        UUID uuid = UUID.fromString(idString);
        String owner = getMethods.getName(uuid);
        String sender = player.getName();
        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id FROM player WHERE uuid = '" + idString + "'")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int playerKey = rs.getInt("id");
                if (args.length == 3) {
                    String number = "1";
                    singleStash.addItem(item);
                    String stash = mapConversion.inventoryToString(singleStash);
                    updatePlayers(stash, idString);

                    recordItem(sender, itemName, number, add, owner, playerKey);
                } else if (args.length == 4) {
                    try {
                        String number = args[3];

                        int integer = Integer.parseInt(number);
                        if (integer <= item.getMaxStackSize()) {
                            for (int i = 0; i < integer; i++) {
                                singleStash.addItem(item);
                            }
                            String stashString = mapConversion.inventoryToString(singleStash);
                            updatePlayers(stashString, idString);
                            recordItem(sender, itemName, number, add, owner, playerKey);
                        }
                    } catch (NumberFormatException nfe) {
                        player.sendMessage("This argument must be an integer");
                    }
                }
            }
        } catch (SQLException x) {
            x.printStackTrace();
        }

    }

    public void giveByTime(Player player, String args[]) {
        String name = player.getDisplayName();
        long timeNow = Timestamp.valueOf(LocalDateTime.now()).getTime();

        List<String> uuidList = new ArrayList<>();

        if (args.length == 4) {
            String timeArg = args[3];

            if ((timeArg.length() < 3) && timeArg.contains("h") || timeArg.contains("d") || timeArg.contains("m")) {

            } else {
                player.sendMessage(ChatColor.RED + "This is not a valid command");
                player.sendMessage("Usage: stash give time <item> [amount] <time>");
            }
        } else if (args.length == 5) {
            String timeArg = args[4];
            if ((timeArg.length() < 3) && timeArg.contains("h") || timeArg.contains("d") || timeArg.contains("m")) {

                String timeWithoutSelector = timeArg.substring(0, timeArg.length() - 2);
                int time = getTime(timeWithoutSelector);


                //----------------------------------------------------HOURS---------------------------------------------------------------------------------------
                if (timeArg.substring(timeArg.length() - 1).equalsIgnoreCase("h")) {

                    getHoursFromLastPlayed(timeNow, time, uuidList);
                    for (String uuid : uuidList) {
                        
                    }


                }
                //----------------------------------------------------DAYS-------------------------------------------------------------------------------------
                if (timeArg.substring(timeArg.length() - 1).equalsIgnoreCase("d")) {

                    getDaysFromLastPlayed(timeNow, time, uuidList);
                }
                //----------------------------------------------------MONTHS-------------------------------------------------------------------------------------
                if (timeArg.substring(timeArg.length() - 1).equalsIgnoreCase("m")) {

                    getMonthsFromLastPlayed(timeNow, time, uuidList);
                }


            } else {
                player.sendMessage(ChatColor.RED + "This is not a valid command");
                player.sendMessage("Usage: stash give time <item> [amount] <time>");
            }
        }
    }

    public void giveAllPlayers(String[] args, ItemStack item, Player player, String itemName) {
        if (args[1].equals("all")) {

            String name = player.getDisplayName();

            if (args.length == 3) {
                String number = "1";
                for (Map.Entry<String, Inventory> entry : MapConversion.map.entrySet()) {
                    Inventory stashInv = entry.getValue();
                    String idString = entry.getKey();
                    stashInv.addItem(item);
                    String stashString = mapConversion.inventoryToString(stashInv);
                    String uuidString = entry.getKey();
                    UUID uuid = UUID.fromString(uuidString);
                    String owner = Bukkit.getOfflinePlayer(uuid).getName();
                    try (Connection connection = database.getPool().getConnection();
                         PreparedStatement statement = connection.prepareStatement("SELECT id FROM player WHERE uuid = '" + idString + "'")) {
                        ResultSet rs = statement.executeQuery();
                        while (rs.next()) {
                            int playerKey = rs.getInt("id");
                            updatePlayers(stashString, idString);
                            recordItem(name, itemName, number, add, owner, playerKey);
                        }
                    } catch (SQLException x) {
                        x.printStackTrace();
                    }
                }
            } else if (args.length == 4) {
                try {
                    String number = args[3];
                    int integer = Integer.parseInt(number);
                    if (integer <= item.getMaxStackSize()) {
                        for (Map.Entry<String, Inventory> entry : MapConversion.map.entrySet()) {
                            Inventory stashInv = entry.getValue();
                            String uuidString = entry.getKey();
                            UUID uuid = UUID.fromString(uuidString);
                            String owner = Bukkit.getOfflinePlayer(uuid).getName();
                            try (Connection connection = database.getPool().getConnection();
                                 PreparedStatement statement = connection.prepareStatement("SELECT id FROM player WHERE uuid = '" + uuidString + "'")) {
                                ResultSet rs = statement.executeQuery();
                                while (rs.next()) {
                                    int playerKey = rs.getInt("id");
                                    for (int i = 0; i < integer; i++) {
                                        stashInv.addItem(item);
                                    }
                                    String stash = mapConversion.inventoryToString(stashInv);
                                    updatePlayers(stash, uuidString);
                                    recordItem(name, itemName, number, add, owner, playerKey);
                                }
                            } catch (SQLException x) {
                                x.printStackTrace();
                            }

                        }
                    }
                } catch (NumberFormatException nfe) {
                    player.sendMessage("This argument must be an integer");
                }
            }
        }


    }

    private int getTime(String timeWithoutSelector) {
        int timeInt = 0;
        try {
            timeInt = Integer.parseInt(timeWithoutSelector);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return timeInt;
    }

    private void getHoursFromLastPlayed(long timeNow, int time, List<String> uuidList) {
        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT uuid, last_played FROM player")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String uuid = rs.getString("uuid");
                long lastPlayed = rs.getTimestamp("last_played").getTime();
                long diff = timeNow - lastPlayed;
                int hours = (int) ((diff / 1000) / 3600);

                if (hours < time) {
                    uuidList.add(uuid);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getDaysFromLastPlayed(long timeNow, int time, List<String> uuidList) {
        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT uuid, last_played FROM player")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String uuid = rs.getString("uuid");
                long lastPlayed = rs.getTimestamp("last_played").getTime();
                long diff = timeNow - lastPlayed;
                int hours = (int) ((diff / 1000) / 3600);
                int days = hours / 24;

                if (days < time) {
                    uuidList.add(uuid);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getMonthsFromLastPlayed(long timeNow, int time, List<String> uuidList) {
        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT uuid, last_played FROM player")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String uuid = rs.getString("uuid");
                long lastPlayed = rs.getTimestamp("last_played").getTime();
                long diff = timeNow - lastPlayed;
                int hours = (int) ((diff / 1000) / 3600);
                int days = hours / 24;
                int months = days / 30;

                if (months < time) {
                    uuidList.add(uuid);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayers(String stashString, String uuid) {

        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE player " +
                     "SET inv = ? " +
                     "WHERE uuid = ?;")) {
            statement.setString(1, stashString);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void recordItem(String name, String itemName, String number, String addOrRemove, String owner, int playerKey) {
        LocalDateTime date = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(date);

        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO log " +
                     "(timestamp, item_name, name, owner, player_key) VALUES (?,?,?,?,?);")) {
            statement.setTimestamp(1, timestamp);
            statement.setString(2, addOrRemove + " " + itemName + " x" + number);
            statement.setString(3, name);
            statement.setString(4, owner);
            statement.setInt(5, playerKey);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAllPlayers() {
        for (Map.Entry<String, Inventory> entry : MapConversion.map.entrySet()) {
            String uuid = entry.getKey();
            Inventory inv = entry.getValue();
            String stashString = mapConversion.inventoryToString(inv);
            updatePlayers(stashString, uuid);
        }
    }
}
