package tech.secretgarden.stash;

import io.github.thebusybiscuit.exoticgarden.ExoticGarden;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import su.nexmedia.engine.NexEngine;
import su.nightexpress.goldencrates.GoldenCrates;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {

    MapConversion mapConversion = new MapConversion();

    public static ArrayList<String> dbList = new ArrayList<>();
    public ArrayList<String> getDbList() {
        dbList.add(getConfig().getString("HOST"));
        dbList.add(getConfig().getString("PORT"));
        dbList.add(getConfig().getString("DATABASE"));
        dbList.add(getConfig().getString("USERNAME"));
        dbList.add(getConfig().getString("PASSWORD"));
        return dbList;
    }

    public static List<String> worldList = new ArrayList<>();

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        if (getConfig().getString("HOST") != null) {
            try {
                getDbList();
                Database.connect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //list of disabled worlds set in config
        worldList = getConfig().getStringList("disabled_worlds");

        System.out.println("Connected to database = " + Database.isConnected());

        System.out.println("Stash plugin has loaded");

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        getCommand("stash").setExecutor(new StashCommand());
        getCommand("stashsf").setExecutor(new StashSfCommand());
        getCommand("stashsf").setTabCompleter(new SfTabCompletion());
        getCommand("stashkey").setExecutor(new StashKeyCommand());
        getCommand("stashkey").setTabCompleter(new KeyTabCompletion());

        if (Database.isConnected()) {
            mapConversion.loadMap();
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

        if (getGcAPI() == null) {
            System.out.println("GoldenCrates was not found");
        } else {
            System.out.println("GoldenCrates was found");
        }

        if (getNeAPI() == null) {
            System.out.println("NexEngine was not found");
        } else {
            System.out.println("NexEngine was found");
        }
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
    public GoldenCrates getGcAPI() {
        Plugin gcPlugin = Bukkit.getServer().getPluginManager().getPlugin("GoldenCrates");
        if (gcPlugin instanceof GoldenCrates) {
            return (GoldenCrates) gcPlugin;
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
        Database.disconnect();

    }
}





