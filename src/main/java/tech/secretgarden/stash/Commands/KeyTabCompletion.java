package tech.secretgarden.stash.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import tech.secretgarden.stash.StashPlugin;

import java.util.ArrayList;
import java.util.List;

public class KeyTabCompletion implements TabCompleter {

    private final StashPlugin plugin;
    public KeyTabCompletion(StashPlugin instance) {
        this.plugin = instance;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (plugin.getEcAPI() != null && plugin.getNeAPI() != null) {
            if (args.length == 3 && args[0].equals("give")) {
                return createList();
            }
        }
        return null;
    }

    private List<String> createList() {
        List<String> list = new ArrayList<>();
        list.add("vip");
        list.add("mvp");
        list.add("diamond");
        list.add("seasonal");
        return list;
    }
}
