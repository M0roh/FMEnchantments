package org.fmenchants.TabCompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.fmenchants.FMEnchants;

import java.util.ArrayList;
import java.util.List;

public class FMEnchantsTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1)
            completions.add("give");
        else if (args.length == 2)
            completions.addAll(FMEnchants.getCustomEnchants().keySet());

        return completions;
    }
}
