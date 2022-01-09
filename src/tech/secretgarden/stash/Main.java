package tech.secretgarden.stash;

import io.github.thebusybiscuit.exoticgarden.ExoticGarden;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    MapConversion mapConversion = new MapConversion();

    @Override
    public void onEnable() {
        System.out.println("Stash plugin has loaded");

        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
        getCommand("stash").setExecutor(new StashCommand(this));
        getCommand("stashsf").setExecutor(new StashSfCommand(this));
        getCommand("stashsf").setTabCompleter(new SfTabCompletion());

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        FileConfiguration config = getConfig();

        //decodes stringMap, then converts it back to map.
        mapConversion.loadMap();

        if (getSfAPI() == null) {
            System.out.println("sf4 not found, plugin disabled");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            System.out.println("sf4 was found");
        }

        if (getEgAPI() == null) {
            System.out.println("eg was not found, plugin disabled");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            System.out.println("eg was found");
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

    @Override
    public void onDisable() {
        System.out.println("Stash has unloaded");

        //turns map -> stringMap, then serializes it.
        mapConversion.saveMap();

    }
}





