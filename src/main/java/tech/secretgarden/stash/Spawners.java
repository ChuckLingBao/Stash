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
import tech.secretgarden.stash.SpawnerNames.Hostile;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Spawners {

    private static ItemStack[] hostile = new ItemStack[10];
    private static ItemStack[] passive = new ItemStack[10];


    public ItemStack getSpawner(String type) {

        int randNum;
        String entity;

        NamespacedKey key;
        // get type of spawner
        if (type.equalsIgnoreCase("hostile")) {

            key = new NamespacedKey(Stash.plugin, "hostile");
            // get random entity
            randNum = ThreadLocalRandom.current().nextInt(0, 18);
            entity = Hostile.entityList[randNum];

        } else if (type.equalsIgnoreCase("passive")) {
            key = new NamespacedKey(Stash.plugin, "passive");
            randNum = ThreadLocalRandom.current().nextInt(0, 18);
            entity = Hostile.entityList[randNum];  // TODO - Change to passive.
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
