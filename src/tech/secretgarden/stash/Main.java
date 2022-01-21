package tech.secretgarden.stash;

import io.github.thebusybiscuit.exoticgarden.ExoticGarden;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import su.nexmedia.engine.NexEngine;
import su.nightexpress.goldencrates.GoldenCrates;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public class Main extends JavaPlugin {

    MapConversion mapConversion = new MapConversion();

    @Override
    public void onEnable() {
        System.out.println("Stash plugin has loaded");

        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
        getCommand("stash").setExecutor(new StashCommand(this));
        getCommand("stashsf").setExecutor(new StashSfCommand(this));
        getCommand("stashsf").setTabCompleter(new SfTabCompletion());
        getCommand("stashkey").setExecutor(new StashKeyCommand(this));
        getCommand("stashkey").setTabCompleter(new KeyTabCompletion());



        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //decodes stringMap, then converts it back to map.
        mapConversion.loadMap();

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

    //GIVE METHODS
    public void configAddAll(String itemName, Integer integer) {
        Set<String> configKeys = getConfig().getKeys(false);
        LocalDateTime date = LocalDateTime.now();
        String dateStr = date.format(DateTimeFormatter.ofPattern("EEEE MMMM dd yyyy hh,mm,ss a"));
        for (String p : configKeys) {
            getConfig().set(p + ".Added Items." + "- " + "x" + integer + " " + itemName, dateStr);
            saveConfig();
        }
    }
    public void configAdd(String[] args, String itemName, Integer integer) {
        LocalDateTime date = LocalDateTime.now();
        String dateStr = date.format(DateTimeFormatter.ofPattern("EEEE MMMM dd yyyy hh,mm,ss a"));
        String p = args[1];
        getConfig().set(p + ".Added Items." + "- " + "x" + integer + " " + itemName, dateStr);
        saveConfig();
    }
    public void parseIntegersAll(String[] args, ItemStack item, Player player) {
        try {
            Integer.parseInt(args[3]);
            int integer = Integer.parseInt(args[3]);
            String itemName = item.getItemMeta().getDisplayName();
            if (integer <= item.getMaxStackSize()) {
                for (Map.Entry entry : MapConversion.map.entrySet()) {
                    Inventory stashInv = (Inventory) entry.getValue();
                    for (int i = 0; i < integer; i++) {
                        stashInv.addItem(item);
                    }
                }
                configAddAll(itemName, integer);
            }
        } catch (NumberFormatException nfe) {
            player.sendMessage("This argument must be an integer");
        }
    }
    public void parseIntegersSingle(String[] args, Inventory singleStash, ItemStack item, Player player) {
        try {
            Integer.parseInt(args[3]);
            String itemName = item.getItemMeta().getDisplayName();
            int integer = Integer.parseInt(args[3]);
            if (integer <= item.getMaxStackSize()) {
                for (int i = 0; i < integer; i++) {
                    singleStash.addItem(item);
                }
                configAdd(args, itemName, integer);
            }
        } catch (NumberFormatException nfe) {
            player.sendMessage("This argument must be an integer");
        }
    }

    @Override
    public void onDisable() {
        System.out.println("Stash has unloaded");

        //turns map -> stringMap, then serializes it.
        mapConversion.saveMap();

    }
}





