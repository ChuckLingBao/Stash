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

    public void giveSinglePlayer(String[] args, ItemStack item, Player player, String idString, String itemName) {
        String sender = player.getName();
        List<String> uuidList = new ArrayList<>();
        uuidList.add(idString);

        if (args.length == 3) {
            String number = "1";
            addItemUsingList(uuidList, number, item, sender, itemName);
        } else if (args.length == 4) {
            String number = args[3];
            addItemUsingList(uuidList, number, item, sender, itemName);
        }
    }

    public void giveByTime(Player player, String[] args, ItemStack item, String itemName) {
        String sender = player.getName();
        long timeNow = Timestamp.valueOf(LocalDateTime.now()).getTime();
        String hour = "hour";
        String day = "day";
        String month = "month";

        List<String> uuidList = new ArrayList<>();

        if (args.length == 4) {
            String timeArg = args[3];
            String number = "1";

            if ((timeArg.length() < 4) && timeArg.contains("h") || timeArg.contains("d") || timeArg.contains("m")) {
                String timeWithoutSelector = timeArg.substring(0, timeArg.length() - 1);
                int time = getRawTime(timeWithoutSelector);

                //----------------------------------------------------HOURS---------------------------------------------------------------------------------------
                if (timeArg.substring(timeArg.length() - 1).equalsIgnoreCase("h")) {
                    initializeListFromTimeSelection(timeNow, time, uuidList, hour);
                    //At this point, a list has been created with all uuid's of your selection.
                    addItemUsingList(uuidList, number, item, sender, itemName);
                }
                //----------------------------------------------------DAYS-------------------------------------------------------------------------------------
                if (timeArg.substring(timeArg.length() - 1).equalsIgnoreCase("d")) {
                    initializeListFromTimeSelection(timeNow, time, uuidList, day);
                    //At this point, a list has been created with all uuid's of your selection.
                    addItemUsingList(uuidList, number, item, sender, itemName);
                }
                //----------------------------------------------------MONTHS-------------------------------------------------------------------------------------
                if (timeArg.substring(timeArg.length() - 1).equalsIgnoreCase("m")) {
                    initializeListFromTimeSelection(timeNow, time, uuidList, month);
                    //At this point, a list has been created with all uuid's of your selection.
                    addItemUsingList(uuidList, number, item, sender, itemName);
                }

            } else {
                player.sendMessage(ChatColor.RED + "This is not a valid command");
                player.sendMessage("Usage: /stash give time <item> [amount] <time>");
            }
        } else if (args.length == 5) {
            String timeArg = args[4];
            if ((timeArg.length() < 4) && timeArg.contains("h") || timeArg.contains("d") || timeArg.contains("m")) {

                String number = args[3];
                String timeWithoutSelector = timeArg.substring(0, timeArg.length() - 1);
                int time = getRawTime(timeWithoutSelector);

                //----------------------------------------------------HOURS---------------------------------------------------------------------------------------
                if (timeArg.substring(timeArg.length() - 1).equalsIgnoreCase("h")) {
                    initializeListFromTimeSelection(timeNow, time, uuidList, hour);
                    //At this point, a uuidList has been created with all uuid's of your selection.
                    addItemUsingList(uuidList, number, item, sender, itemName);
                }
                //----------------------------------------------------DAYS-------------------------------------------------------------------------------------
                if (timeArg.substring(timeArg.length() - 1).equalsIgnoreCase("d")) {
                    initializeListFromTimeSelection(timeNow, time, uuidList, day);
                    //At this point, a uuidList has been created with all uuid's of your selection.
                    addItemUsingList(uuidList, number, item, sender, itemName);
                }
                //----------------------------------------------------MONTHS-------------------------------------------------------------------------------------
                if (timeArg.substring(timeArg.length() - 1).equalsIgnoreCase("m")) {
                    initializeListFromTimeSelection(timeNow, time, uuidList, month);
                    //At this point, a uuidList has been created with all uuid's of your selection.
                    addItemUsingList(uuidList, number, item, sender, itemName);
                }
            } else {
                player.sendMessage(ChatColor.RED + "This is not a valid command");
                player.sendMessage("Usage: stash give time <item> [amount] <time>");
            }
        }
    }

    public void giveAllPlayers(String[] args, ItemStack item, Player player, String itemName) {
        if (args[1].equals("all")) {

            String sender = player.getName();
            List<String> uuidList = new ArrayList<>();

            if (args.length == 3) {
                String number = "1";
                for (Map.Entry<String, Inventory> entry : MapConversion.map.entrySet()) {
                    uuidList.add(entry.getKey());
                }
                addItemUsingList(uuidList, number, item, sender, itemName);

            } else if (args.length == 4) {

                String number = args[3];
                for (Map.Entry<String, Inventory> entry : MapConversion.map.entrySet()) {
                    uuidList.add(entry.getKey());
                }
                addItemUsingList(uuidList, number, item, sender, itemName);
            }
        }
    }

    private int getRawTime(String timeWithoutSelector) {
        int timeInt = 0;
        try {
            timeInt = Integer.parseInt(timeWithoutSelector);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return timeInt;
    }

    private void addItemUsingList(List<String> uuidList, String number, ItemStack item, String sender, String itemName) {
        for (String idString : uuidList) {
            int playerKey = getMethods.getPlayerId(idString);
            Inventory stash = MapConversion.map.get(idString);
            UUID uuid = UUID.fromString(idString);
            String owner = Bukkit.getOfflinePlayer(uuid).getName();
            try {
                int quantity = Integer.parseInt(number);
                if (quantity <= item.getMaxStackSize()) {
                    for (int i = 0; i < quantity; i++) {
                        stash.addItem(item);
                    }
                    String stashString = mapConversion.inventoryToString(stash);
                    updatePlayers(stashString, idString);
                    recordItem(sender, itemName, number, add, owner, playerKey);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeListFromTimeSelection(long timeNow, int time, List<String> uuidList, String selection) {
        try (Connection connection = database.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT uuid, last_played FROM player")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int selectedTime = 0;
                String uuid = rs.getString("uuid");
                if (rs.getTimestamp("last_played") != null) {
                    long lastPlayed = rs.getTimestamp("last_played").getTime();
                    long diff = timeNow - lastPlayed;
                    int hours = (int) ((diff / 1000) / 3600);
                    int days = hours / 24;
                    int months = days / 30;
                    if (selection.equalsIgnoreCase("hour")) {
                        selectedTime = hours;
                    }
                    if (selection.equalsIgnoreCase("day")) {
                        selectedTime = days;
                    }
                    if (selection.equalsIgnoreCase("month")) {
                        selectedTime = months;
                    }
                    if (selectedTime < time) {
                        uuidList.add(uuid);
                    }
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
}
