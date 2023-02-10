package tech.secretgarden.stash.SpawnerNames;

import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Passive {

    public static String BLAZE = "Blaze";
    public static String CREEPER = "Creeper";
    public static String GHAST = "Ghast";
    public static String MAGMA_CUBE = "Magma Cube";
    public static String SHULKER = "Shulker";
    public static String SILVERFISH = "Silverfish";
    public static String SKELETON = "Skeleton";
    public static String SLIME = "Slime";
    public static String ZOMBIE = "Zombie";
    public static String ZOMBIE_VILLAGER = "Zombie Villager";
    public static String DROWNED = "Drowned";
    public static String WITHER_SKELETON = "Wither Skeleton";
    public static String WITCH = "Witch";
    public static String VINDICATOR = "Vindicator";
    public static String EVOKER = "Evoker";
    public static String PHANTOM = "Phantom";
    public static String PILLAGER = "Pillager";
    public static String RAVAGER = "Ravager";
    public static String VEX = "Vex";
    public static String PIGLIN_BRUTE = "Piglin Brute";

    public static String[] entityList = new String[20];
    public static HashMap<String, EntityType> entityMap = new HashMap<>();

    public void initList() throws IllegalAccessException {
        Hostile hostile = new Hostile();
        Field[] fields = hostile.getClass().getFields();

        for (int i = 0; i < fields.length; i++) {
            Class type = fields[i].getType();
            Object value = fields[i].get(hostile);
            if (type.isAssignableFrom(String.class)) {
                entityList[i] = (String) value;
            }
        }
    }

    public static void initMap() {
        entityMap.put(BLAZE, EntityType.BLAZE);
        entityMap.put(CREEPER, EntityType.CREEPER);
        entityMap.put(GHAST, EntityType.GHAST);
        entityMap.put(MAGMA_CUBE, EntityType.MAGMA_CUBE);
        entityMap.put(SHULKER, EntityType.SHULKER);
        entityMap.put(SILVERFISH, EntityType.SILVERFISH);
        entityMap.put(SKELETON, EntityType.SKELETON);
        entityMap.put(SLIME, EntityType.SLIME);
        entityMap.put(ZOMBIE, EntityType.ZOMBIE);
        entityMap.put(ZOMBIE_VILLAGER, EntityType.ZOMBIE_VILLAGER);
        entityMap.put(DROWNED, EntityType.DROWNED);
        entityMap.put(WITHER_SKELETON, EntityType.WITHER_SKELETON);
        entityMap.put(WITCH, EntityType.WITCH);
        entityMap.put(VINDICATOR, EntityType.VINDICATOR);
        entityMap.put(PHANTOM, EntityType.PHANTOM);
        entityMap.put(PILLAGER, EntityType.PILLAGER);
        entityMap.put(EVOKER, EntityType.EVOKER);
        entityMap.put(RAVAGER, EntityType.RAVAGER);
        entityMap.put(VEX, EntityType.VEX);
        entityMap.put(PIGLIN_BRUTE, EntityType.PIGLIN_BRUTE);
    }

}
