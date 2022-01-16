package tech.secretgarden.stash;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import javax.annotation.Nonnull;
import java.util.*;

public class SfTabCompletion implements TabCompleter {
    private static final int MAX_SUGGESTIONS = 80;
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command cmd, String label, String[] args) {
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                return createReturnList(getSlimefunItems(), args[2]);
            }
        }
        return null;
    }
    @Nonnull
    private List<String> createReturnList(@Nonnull List<String> list, @Nonnull String string) {
        if (string.length() == 0) {
            return list;
        }

        String input = string.toLowerCase(Locale.ROOT);
        List<String> returnList = new LinkedList<>();

        for (String item : list) {
            if (item.toLowerCase(Locale.ROOT).contains(input)) {
                returnList.add(item);

                if (returnList.size() >= MAX_SUGGESTIONS) {
                    break;
                }
            } else if (item.equalsIgnoreCase(input)) {
                return Collections.emptyList();
            }
        }

        return returnList;
    }

    @Nonnull
    private List<String> getSlimefunItems() {
        List<SlimefunItem> items = Slimefun.getRegistry().getEnabledSlimefunItems();
        List<String> list = new ArrayList<>(items.size());

        for (SlimefunItem item : items) {
            list.add(item.getId());
        }

        return list;
    }
}
