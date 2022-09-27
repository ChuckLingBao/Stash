package tech.secretgarden.stash;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class StashCmdContents {
    String giver;
    ItemStack item;
    String itemName;
    String[] args;
    Player player = null;
    String receiver;
    private int quantity = 0;
    String idString = null;
    Timestamp timestamp = null;
    String error = null;

    public StashCmdContents(String giver, ItemStack item, String itemName, String[] args) {
        this.giver = giver;
        this.item = item;
        this.itemName = itemName;
        this.args = args;
        this.receiver = args[1];
        if (args.length > 3) {
            this.quantity = initQuantity(args[3]);
        } else {
            this.quantity = 1;
        }
        if (args[1].equalsIgnoreCase("time")) {
            setTimestamp();
        }
    }

    public String getReceiver() {
        return this.receiver;
    }

    public Player getPlayer() {
        return this.player;
    }

    private int initQuantity(String arg) {
        try {
            int num = Integer.parseInt(arg);
            if (num <= item.getMaxStackSize()) {
                return num;
            } else {
                //trying to give more than one stack at a time.
                return 0;
            }
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    private void setTimestamp() {
        String timeArg = args[args.length-1];
        char c = timeArg.charAt(timeArg.length()-1);
        String timeWithoutSelector = timeArg.substring(0, timeArg.length() - 1);
        long num = parseInt(timeWithoutSelector);
        long now = Timestamp.valueOf(LocalDateTime.now()).getTime();

        if (c == 'h' || c =='H') {
            num = TimeUnit.HOURS.toMillis(num);
        }
        if (c == 'd' || c =='D') {
            num = TimeUnit.DAYS.toMillis(num);
        }
        if (c == 'm' || c =='M') {
            num = TimeUnit.DAYS.toMillis(num) * 30;
        }
        this.timestamp = new Timestamp(now - num);
        System.out.println(this.timestamp);
    }

    private long parseInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            this.error = "Incorrect time usage";
            return 0;
        }
    }
}
