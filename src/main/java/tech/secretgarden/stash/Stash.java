package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class Stash {

    StashPlugin plugin = StashPlugin.plugin;
    NamespacedKey key = plugin.getKey("stash_page");

    ArrayList<ItemStack> items;
    HashMap<Integer, Inventory> inventoryMap = new HashMap<>();
    Inventory inventory;
    String ownerUUID;
    int INVENTORY_SIZE = 54;
    int ITEM_SLOTS = 45;

    public Stash(String uuid) {
        this.items = new ArrayList<>();
        this.ownerUUID = uuid;
    }

    public Stash(ArrayList<ItemStack> items, String uuid) {
        this.items = items;
        this.ownerUUID = uuid;
    }

    public ArrayList<ItemStack> getList() {
        return items;
    }

    public Inventory createInventory() {
        return createInventory(false);
    }

    public Inventory createInventory(boolean otherStash) {

        String name = Bukkit.getPlayer(ownerUUID).getName();

        if (otherStash) {
            this.inventory = Bukkit.createInventory(null, INVENTORY_SIZE, ChatColor.DARK_PURPLE + name + " Stash");
        } else {
            this.inventory = Bukkit.createInventory(null, INVENTORY_SIZE, ChatColor.DARK_PURPLE + "Stash");
        }

        return this.inventory;
    }

    public void populateInventoryPage(int pageNum) {
        this.inventory.clear();
        int startIndex = ITEM_SLOTS * pageNum;
        int endIndex = startIndex + ITEM_SLOTS;
        if (endIndex >= items.size()) { endIndex = items.size(); }

        // add previous button
        if (pageNum > 0) {
            ItemStack prevButton = navButton(pageNum, false);
            this.inventory.setItem(ITEM_SLOTS, prevButton);
        }

        // add next button
        if ((pageNum + 1) * INVENTORY_SIZE <= items.size()) {
            ItemStack nextButton = navButton(pageNum, true);
            this.inventory.setItem(INVENTORY_SIZE - 1, nextButton);
        }

        // add items
        for (int i = startIndex; i < endIndex; i++) {
            this.inventory.addItem(this.items.get(i));
        }
    }

    public void add(ItemStack item) {
        int itemAmount = item.getAmount();
        for (int i = 0; i < items.size(); i++) {
            ItemStack currItem = items.get(i);
            int currAmount = currItem.getAmount();
            int currAmountAvailable = currItem.getMaxStackSize() - currAmount;
            if (currItem.isSimilar(item) && currAmountAvailable > 0) {

                // check if we can add to curr index
                if (itemAmount <= currAmountAvailable) {
                    // add all items to this index and break
                    currItem.setAmount(currAmount + itemAmount);
                    itemAmount = 0;
                    break;
                } else {
                    // top-off stack size and continue
                    currItem.setAmount(currItem.getMaxStackSize());
                    itemAmount -= currAmountAvailable;
                }
            }
        }

        // could not add all items to an existing index
        if (itemAmount > 0) {
            item.setAmount(itemAmount);
            items.add(item);
        }
    }

    public void remove(int index) {

    }

    public void remove(int index, int count) {

    }

    private ItemStack navButton(int pageNum, boolean increasing) {

        int newPageNum;
        ItemStack button = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = button.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, pageNum);

        // pageNum starts at 0
        if (increasing) {
            newPageNum = pageNum + 2;
            meta.setDisplayName("Next");
        }
        else {
            newPageNum = pageNum;
            meta.setDisplayName("Back");
        }

        lore.add("Go to page " + newPageNum);
        meta.setLore(lore);
        return button;
    }

    public int getPage() {
        if (this.inventory.getItem(ITEM_SLOTS).getType() != Material.AIR) {

            ItemStack navButton = this.inventory.getItem(ITEM_SLOTS);
            ItemMeta meta = navButton.getItemMeta();
            return meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);

        } else if (this.inventory.getItem(INVENTORY_SIZE - 1).getType() != Material.AIR) {

            ItemStack navButton = this.inventory.getItem(INVENTORY_SIZE - 1);
            ItemMeta meta = navButton.getItemMeta();
            return meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);

        } else {
            return 0;
        }
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }
}
