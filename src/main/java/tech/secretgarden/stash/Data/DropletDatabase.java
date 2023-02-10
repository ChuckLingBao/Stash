package tech.secretgarden.stash.Data;

import com.zaxxer.hikari.HikariDataSource;
import tech.secretgarden.stash.Stash;

import java.sql.SQLException;
import java.util.ArrayList;

public class DropletDatabase {
    public static HikariDataSource pool;
    private static final ArrayList<String> list = Stash.dropletList;

    public void connect() throws SQLException {

        pool = new HikariDataSource();
        pool.setDriverClassName("com.mysql.jdbc.Driver");
        pool.setJdbcUrl("jdbc:mysql://" + list.get(0) + ":" + list.get(1) + "/" + list.get(2) + "?useSSL=true&autoReconnect=true");
        pool.setUsername(list.get(3));
        pool.setPassword(list.get(4));
    }

    public static boolean isConnected() {
        return pool != null;
    }

    public HikariDataSource getPool() {
        return pool;
    }

    public void disconnect() {
        if (isConnected()) {
            pool.close();
        }

    }
}