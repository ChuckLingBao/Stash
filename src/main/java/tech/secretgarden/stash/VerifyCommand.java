package tech.secretgarden.stash;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerifyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length != 2) {
                return false;
            }
            Player player = (Player) sender;
            String idString = args[0];

            //check if id string can be parsed
            int id;
            try {
                id = Integer.parseInt(idString);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Incorrect command usage!");
                player.sendMessage(ChatColor.RED + "/verify <number> <token>");
                return false;
            }

            try (Connection connection = DropletDatabase.pool.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT gamertag FROM mc_account WHERE user_key = '" + id + "'")) {
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    String gamertag = rs.getString("gamertag");
                    if (!gamertag.equals(player.getName())) {
                        player.sendMessage(ChatColor.RED + "Error! You are trying to verify someone else's account!");
                        return false;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try (Connection connection = DropletDatabase.pool.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT verified FROM mc_account WHERE user_key = '" + id + "'")) {
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    String verification = rs.getString("verified");
                    if (verification.equalsIgnoreCase("true")) {
                        player.sendMessage(ChatColor.GREEN + "This account has already been verified!");
                        return false;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            String token = args[1];

            String tokenResult = getToken(id);

            if (token.equals(tokenResult)) {
                try (Connection connection = DropletDatabase.pool.getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE mc_account SET verified = 'true' WHERE user_key = '" + id + "'")) {
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try (Connection connection = DropletDatabase.pool.getConnection();
                     PreparedStatement statement = connection.prepareStatement("SELECT verified FROM mc_account WHERE user_key = '" + id + "'")) {
                    ResultSet rs = statement.executeQuery();
                    while (rs.next()) {
                        String verification = rs.getString("verified");
                        if (verification.equalsIgnoreCase("true")) {
                            player.sendMessage(ChatColor.GREEN + "You successfully linked your accounts!");
                        } else {
                            player.sendMessage(ChatColor.RED + "verification request failed! Please get a new command.");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(ChatColor.RED + "Invalid verification link!");
            }
        }
        return false;
    }
    private String getToken(int id) {
        String result = null;
        try (Connection connection = DropletDatabase.pool.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT token FROM mc_account WHERE user_key = ?")) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                result = rs.getString("token");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
