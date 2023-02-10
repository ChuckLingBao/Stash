package tech.secretgarden.stash.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.secretgarden.stash.Data.DropletDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerifyCommand implements CommandExecutor {

    DropletDatabase dropletDatabase = new DropletDatabase();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Incorrect command usage!");
                player.sendMessage(ChatColor.RED + "/verify <SG id> <token>");
                return false;
            }

            String appID = args[0];

            try (Connection connection = dropletDatabase.getPool().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT gamertag FROM mc_accounts WHERE app_id = '" + appID + "'")) {
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

            try (Connection connection = dropletDatabase.getPool().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT verified FROM mc_accounts WHERE app_id = '" + appID + "'")) {
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

            String tokenResult = getToken(appID);

            if (token.equals(tokenResult)) {
                try (Connection connection = dropletDatabase.getPool().getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE mc_accounts SET verified = 'true' WHERE app_id = '" + appID + "'")) {
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try (Connection connection = dropletDatabase.getPool().getConnection();
                     PreparedStatement statement = connection.prepareStatement("SELECT verified FROM mc_accounts WHERE app_id = '" + appID + "'")) {
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
    private String getToken(String appID) {
        String result = null;
        try (Connection connection = dropletDatabase.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT token FROM mc_accounts WHERE app_id = ?")) {
            statement.setString(1, appID);
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
