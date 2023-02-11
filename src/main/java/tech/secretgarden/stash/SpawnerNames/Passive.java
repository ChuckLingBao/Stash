package tech.secretgarden.stash.SpawnerNames;

import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Passive {

    public static String AXOLOTL = "Axolotl";
    public static String BEE = "Bee";
    public static String CAT = "Cat";
    public static String COD = "Cod";
    public static String DOLPHIN = "Dolphin";
    public static String DONKEY = "Donkey";
    public static String FOX = "Fox";
    public static String FROG = "Frog";
    public static String GLOW_SQUID = "Glow Squid";
    public static String GOAT = "Goat";
    public static String HORSE = "Horse";
    public static String LLAMA = "Llama";
    public static String MOOSHROOM = "Mooshroom";
    public static String MULE = "Mule";
    public static String OCELOT = "Ocelot";
    public static String PANDA = "Panda";
    public static String PARROT = "Parrot";
    public static String POLAR_BEAR = "Polar Bears";
    public static String PUFFERFISH = "Pufferfish";
    public static String RABBIT = "Rabbit";
    public static String SALMON = "Salmon";
    public static String SKELETON_HORSE = "Skeleton Horse";
    public static String SQUID = "Squid";
    public static String STRIDER = "Strider";
    public static String TADPOLE = "Tadpole";
    public static String TRADER_LLAMA = "Trader Llama";
    public static String TROPICAL_FISH = "Tropical fish";
    public static String TURTLE = "Turtle";
    public static String WANDERING_TRADER = "Wandering Trader";

    public final static int SIZE = 29;

    public static String[] entityList = new String[SIZE];
    public static HashMap<String, EntityType> entityMap = new HashMap<>();

//    public void initList() throws IllegalAccessException {
//        Passive passive = new Hostile();
//        Field[] fields = hostile.getClass().getFields();
//
//        for (int i = 0; i < fields.length; i++) {
//            Class type = fields[i].getType();
//            Object value = fields[i].get(hostile);
//            if (type.isAssignableFrom(String.class)) {
//                entityList[i] = (String) value;
//            }
//        }
//    }

    public static void initMap() {
        entityMap.put(AXOLOTL, EntityType.AXOLOTL);
        entityMap.put(BEE, EntityType.BEE);
        entityMap.put(CAT, EntityType.CAT);
        entityMap.put(COD, EntityType.COD);
        entityMap.put(DOLPHIN, EntityType.DOLPHIN);
        entityMap.put(DONKEY, EntityType.DONKEY);
        entityMap.put(FOX, EntityType.FOX);
        entityMap.put(FROG, EntityType.FROG);
        entityMap.put(GLOW_SQUID, EntityType.GLOW_SQUID);
        entityMap.put(GOAT, EntityType.GOAT);
        entityMap.put(HORSE, EntityType.HORSE);
        entityMap.put(LLAMA, EntityType.LLAMA);
        entityMap.put(MOOSHROOM, EntityType.MUSHROOM_COW);
        entityMap.put(MULE, EntityType.MULE);
        entityMap.put(OCELOT, EntityType.OCELOT);
        entityMap.put(PANDA, EntityType.PANDA);
        entityMap.put(PARROT, EntityType.PARROT);
        entityMap.put(POLAR_BEAR, EntityType.POLAR_BEAR);
        entityMap.put(PUFFERFISH, EntityType.PUFFERFISH);
        entityMap.put(RABBIT, EntityType.RABBIT);
        entityMap.put(SALMON, EntityType.SALMON);
        entityMap.put(SKELETON_HORSE, EntityType.SKELETON_HORSE);
        entityMap.put(SQUID, EntityType.SQUID);
        entityMap.put(STRIDER, EntityType.STRIDER);
        entityMap.put(TADPOLE, EntityType.TADPOLE);
        entityMap.put(TRADER_LLAMA, EntityType.TRADER_LLAMA);
        entityMap.put(TROPICAL_FISH, EntityType.TROPICAL_FISH);
        entityMap.put(TURTLE, EntityType.TURTLE);
        entityMap.put(WANDERING_TRADER, EntityType.WANDERING_TRADER);
    }

}
