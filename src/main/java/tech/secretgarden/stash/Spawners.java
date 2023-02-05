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

import java.util.UUID;

public class Spawners {

    private static ItemStack[] hostile = new ItemStack[10];
    private static ItemStack[] passive = new ItemStack[10];


    public ItemStack getSpawner() {
        NamespacedKey key = new NamespacedKey(Stash.plugin, "hostile");
        ItemStack testSpawner = new ItemStack(Material.SPAWNER, 1);

        ItemMeta meta = testSpawner.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "bat");

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "bat Spawner");

        testSpawner.setItemMeta(meta);

        return testSpawner;
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
