package tech.secretgarden.stash;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.ArrayList;

public class DropletDatabase {
    public static ComboPooledDataSource pool;
    private static final ArrayList<String> list = Stash.dropletList;

    public static void connect() throws SQLException {

        try {
            pool = new ComboPooledDataSource();
            pool.setDriverClass("com.mysql.jdbc.Driver");
            pool.setJdbcUrl("jdbc:mysql://" + list.get(0) + ":" + list.get(1) + "/" + list.get(2) + "?useSSL=true&autoReconnect=true");
            pool.setUser(list.get(3));
            pool.setPassword(list.get(4));
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public static boolean isConnected() {
        return pool != null;
    }

    public ComboPooledDataSource getPool() {
        return pool;
    }

    public void disconnect() {
        if (isConnected()) {
            pool.close();
        }

    }
}
