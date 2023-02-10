package tech.secretgarden.stash;

import com.google.common.collect.ImmutableList;
import io.github.thebusybiscuit.exoticgarden.ExoticGarden;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import su.nexmedia.engine.NexEngine;
import su.nightexpress.excellentcrates.ExcellentCrates;
import tech.secretgarden.stash.Commands.*;
import tech.secretgarden.stash.Data.Database;
import tech.secretgarden.stash.Data.DropletDatabase;
import tech.secretgarden.stash.Data.MapConversion;
import tech.secretgarden.stash.SpawnerNames.Hostile;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Stash extends JavaPlugin {

    public static Stash plugin;

    MapConversion mapConversion = new MapConversion();
    Database database = new Database();
    DropletDatabase dropletDatabase = new DropletDatabase();
    Hostile hostile = new Hostile();

    public static ArrayList<String> dbList = new ArrayList<>();
    public ArrayList<String> getDbList() {
        dbList.add(getConfig().getString("HOST"));
        dbList.add(getConfig().getString("PORT"));
        dbList.add(getConfig().getString("DATABASE"));
        dbList.add(getConfig().getString("USERNAME"));
        dbList.add(getConfig().getString("PASSWORD"));
        return dbList;
    }

    public static ArrayList<String> dropletList = new ArrayList<>();
    public ArrayList<String> getDropletList() {
        dropletList.add(getConfig().getString("DROPLET_HOST"));
        dropletList.add(getConfig().getString("DROPLET_PORT"));
        dropletList.add(getConfig().getString("DROPLET_DATABASE"));
        dropletList.add(getConfig().getString("DROPLET_USERNAME"));
        dropletList.add(getConfig().getString("DROPLET_PASSWORD"));
        return dropletList;
    }

    public static List<String> worldList = new ArrayList<>();

    @Override
    public void onEnable() {
        plugin = this;
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        if (getConfig().getString("HOST") != null && getConfig().getString("DROPLET_HOST") != null) {
            try {
                getDbList();
                getDropletList();
                database.connect();
                dropletDatabase.connect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //list of disabled worlds set in config
        worldList = getConfig().getStringList("disabled_worlds");

        System.out.println("Connected to database = " + database.isConnected());

        System.out.println("Stash plugin has loaded");

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        getCommand("stash").setExecutor(new StashCommand());
        getCommand("stashsf").setExecutor(new StashSfCommand(this));
        getCommand("stashsf").setTabCompleter(new SfTabCompletion(this));
        getCommand("stashkey").setExecutor(new StashKeyCommand(this));
        getCommand("stashkey").setTabCompleter(new KeyTabCompletion(this));
        getCommand("verify").setExecutor(new VerifyCommand());
        getCommand("randomspawner").setExecutor(new RandomSpawner());

        Hostile.initMap();
        try {
            hostile.initList();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (database.isConnected()) {
            mapConversion.loadMap();
        } else {
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (getSfAPI() == null) {
            System.out.println("sf4 not found");
        } else {
            System.out.println("sf4 was found");
        }

        if (getEgAPI() == null) {
            System.out.println("ExoticGardens was not found");
        } else {
            System.out.println("ExoticGardens was found");
        }

        if (getEcAPI() == null) {
            System.out.println("GoldenCrates was not found");
        } else {
            System.out.println("GoldenCrates was found");
        }

        if (getNeAPI() == null) {
            System.out.println("NexEngine was not found");
        } else {
            System.out.println("NexEngine was found");
        }
        updateLastPlayedPlayers.runTaskTimerAsynchronously(this, 20, 20 * 60);
    }

    //slimefun API
    public Slimefun getSfAPI() {
        Plugin sfPlugin = Bukkit.getServer().getPluginManager().getPlugin("Slimefun");
        if (sfPlugin instanceof Slimefun) {
            return (Slimefun) sfPlugin;
        } else {
            return null;
        }
    }

    //exotic gardens API
    public ExoticGarden getEgAPI() {
        Plugin egPlugin = Bukkit.getServer().getPluginManager().getPlugin("ExoticGarden");
        if (egPlugin instanceof ExoticGarden) {
            return (ExoticGarden) egPlugin;
        } else {
            return null;
        }
    }

    //golden crates API
    public ExcellentCrates getEcAPI() {
        Plugin ecPlugin = Bukkit.getServer().getPluginManager().getPlugin("ExcellentCrates");
        if (ecPlugin instanceof ExcellentCrates) {
            return (ExcellentCrates) ecPlugin;
        } else {
            return null;
        }
    }

    //nex engine API
    public NexEngine getNeAPI() {
        Plugin nePlugin = Bukkit.getServer().getPluginManager().getPlugin("NexEngine");
        if (nePlugin instanceof NexEngine) {
            return (NexEngine) nePlugin;
        } else {
            return null;
        }
    }


    @Override
    public void onDisable() {
        System.out.println("Stash has unloaded");
        database.disconnect();

    }
    BukkitRunnable updateLastPlayedPlayers = new BukkitRunnable() {
        @Override
        public void run() {
            ImmutableList<Player> onlinePlayerList = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
            for (Player player : onlinePlayerList) {
                String uuid = player.getUniqueId().toString();
                try (Connection connection = database.getPool().getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE player SET last_played = ? WHERE uuid = '" + uuid + "'")) {
                    statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            getLogger().info("updated players");
        }
    };
}





