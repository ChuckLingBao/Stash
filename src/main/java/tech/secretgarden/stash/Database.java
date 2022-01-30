package tech.secretgarden.stash;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class Database {
    /*
    private final String HOST = "";
    private final int PORT = 3306;
    private final String DATABASE = "";
    private final String USERNAME = "";
    private final String PASSWORD = "";
     */

    static ArrayList<String> list = Main.configList;
    private static Connection connection;

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:mysql://" + list.get(0) + ":" + list.get(1) + "/" + list.get(2) + "?useSSL=false",
                list.get(3),
                list.get(4));
    }

    public boolean isConnected() {
        return connection != null;
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

    }
}
