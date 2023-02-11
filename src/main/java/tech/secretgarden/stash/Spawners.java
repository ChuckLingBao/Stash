package tech.secretgarden.stash;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import tech.secretgarden.stash.SpawnerNames.Hostile;
import tech.secretgarden.stash.SpawnerNames.Passive;
import tech.secretgarden.stash.SpawnerNames.Rare;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Spawners {

    Stash plugin = Stash.plugin;



//
//    public NamespacedKey HOSTILE_KEY = new NamespacedKey(plugin, "hostile");
//    public NamespacedKey PASSIVE_KEY = new NamespacedKey(plugin, "passive");
//    public NamespacedKey RARE_KEY = new NamespacedKey(plugin, "rare");


    public ItemStack getSpawner(String type) {

        String entity;

        NamespacedKey key;
        // get type of spawner
        if (type.equalsIgnoreCase("hostile")) {

            key = plugin.getKey("hostile");
            entity = getHostileEntity();

        } else if (type.equalsIgnoreCase("passive")) {

            key = plugin.getKey("passive");
            entity = getPassiveEntity();

        } else if (type.equalsIgnoreCase("rare")) {

            key = plugin.getKey("rare");
            entity = getRareEntity();

        } else if (type.equalsIgnoreCase("both")) {

            // pick hostile or passive spawners
            int picker = ThreadLocalRandom.current().nextInt(0, 2);
            if (picker == 0) {
                key = plugin.getKey("hostile");
                entity = getHostileEntity();
            } else {
                key = plugin.getKey("passive");
                entity = getPassiveEntity();
            }

        } else {
            return null;
        }

        // create spawner
        ItemStack spawner = new ItemStack(Material.SPAWNER, 1);
        ItemMeta meta = spawner.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, entity);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + entity + " Spawner");

        spawner.setItemMeta(meta);

        return spawner;
    }

    private String getHostileEntity() {
        int randNum = ThreadLocalRandom.current().nextInt(0, Hostile.SIZE);
        return Hostile.entityList[randNum];
    }

    private String getPassiveEntity() {
        int randNum = ThreadLocalRandom.current().nextInt(0, Passive.SIZE);
        return Passive.entityList[randNum];
    }

    private String getRareEntity() {
        int randNum = ThreadLocalRandom.current().nextInt(0, Rare.SIZE);
        return Rare.entityList[randNum];
    }


//    public static void initSpawners() {
//        // init hostile arr
//
//        for (int i = 0; i < hostile.length; i++) {
//
//        }
//
//        ItemStack testSpawner = new ItemStack(Material.SPAWNER, 1);
//        BlockStateMeta bsm = (BlockStateMeta) testSpawner.getItemMeta();
//        CreatureSpawner cs = (CreatureSpawner) bsm.getBlockState();
//        hostile[0] = new ItemStack(Material.SPAWNER)
//    }
}
