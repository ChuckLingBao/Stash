package tech.secretgarden.stash;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class Database {
    private final String HOST = "localhost";
    private final int PORT = 3306;
    private final String DATABASE = "stash_plugin";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    //private static HikariDataSource hikari;

    static ArrayList<String> list = Main.configList;
    //private static HikariConfig config = new HikariConfig();
    private static Connection connection;

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:mysql://" + list.get(0) + ":" + list.get(1) + "/" + list.get(2) + "?useSSL=false",
                list.get(3),
                list.get(4));
        /*
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", list.get(0));
        hikari.addDataSourceProperty("port", list.get(1));
        hikari.addDataSourceProperty("databaseName", list.get(2));
        hikari.addDataSourceProperty("user", list.get(3));
        hikari.addDataSourceProperty("password", list.get(4));
        return hikari;

        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        hikari.setJdbcUrl(list.get(0));
        hikari.addDataSourceProperty("port", list.get(1));
        hikari.addDataSourceProperty("databaseName", list.get(2));
        hikari.addDataSourceProperty("user", list.get(3));
        hikari.addDataSourceProperty("password", list.get(4));


        config.setJdbcUrl(
                "jdbc:mysql://" +
                        list.get(0) +
                        ":" +
                        list.get(1) +
                        "/" +
                        list.get(2)
        );
        config.setUsername(list.get(3));
        config.setPassword(list.get(4));
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        hikari = new HikariDataSource( config );

         */
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
