package tech.secretgarden.stash.SpawnerNames;

import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Rare {
    public static String ALLAY = "Allay";
    public static String VILLAGER = "Villager";
    public static String MAGMA_CUBE = "Magma Cube";
    public static String SHULKER = "Shulker";
    public static String WITHER_SKELETON = "Wither Skeleton";
    public static String PHANTOM = "Phantom";
    public static String WARDEN = "Warden";


    public final static int SIZE = 7;

    public static String[] entityList = new String[SIZE];
    public static HashMap<String, EntityType> entityMap = new HashMap<>();

//    public void initList() throws IllegalAccessException {
//        Rare rare = new Rare();
//        Field[] fields = rare.getClass().getFields();
//
//        for (int i = 0; i < fields.length; i++) {
//            Class type = fields[i].getType();
//            Object value = fields[i].get(rare);
//            if (type.isAssignableFrom(String.class)) {
//                entityList[i] = (String) value;
//            }
//        }
//    }

    public static void initMap() {

        entityMap.put(ALLAY, EntityType.ALLAY);
        entityMap.put(VILLAGER, EntityType.VILLAGER);
        entityMap.put(MAGMA_CUBE, EntityType.MAGMA_CUBE);
        entityMap.put(SHULKER, EntityType.SHULKER);
        entityMap.put(WITHER_SKELETON, EntityType.WITHER_SKELETON);
        entityMap.put(PHANTOM, EntityType.PHANTOM);
        entityMap.put(WARDEN, EntityType.WARDEN);
    }

}
