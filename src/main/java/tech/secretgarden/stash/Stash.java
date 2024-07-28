package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Stash {

    ArrayList<ItemStack> items;
    Inventory stash;

    public Stash() {
        this.items = new ArrayList<>();
        initStash();
    }

    public Stash(ArrayList<ItemStack> items) {
        this.items = items;
        initStash();
    }

    // populate stash inventory
    private void initStash() {
    }

    public Inventory getStash(int pageNum) {
        this.stash = Bukkit.createInventory(null, 54, ChatColor.LIGHT_PURPLE + "Stash");
        int startIndex = 45 * pageNum;

        // add previous button
        if (pageNum > 0) {
            ItemStack prevButton = navButton(pageNum, false);
            this.stash.setItem(45, prevButton);
        }

        for (int i = startIndex; i < startIndex + 45; i++) {
            if (i >= items.size()) {
                break;
            }
            this.stash.addItem(this.items.get(i));
        }
        return stash;
    }

    private ItemStack navButton(int pageNum, boolean increasing) {

        int newPageNum;
        ItemStack button = new ItemStack(Material.NETHER_STAR);

        ItemMeta meta = button.getItemMeta();
        meta.setDisplayName(direction);
        ArrayList<String> lore = new ArrayList<>();

        if (increasing) { newPageNum = pageNum + 1; }
        else { newPageNum = pageNum - 1; }

        lore.add("Go to page " + newPageNum);
        meta.setLore(lore);
        return button;
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }
}
