package com.reogent.hammer.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HammerTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("adminmenu", "sendmessage", "give");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            List<String> completions = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
            return StringUtil.copyPartialMatches(args[1], completions, new ArrayList<>());
        }
        if (args.length == 3) {
            return Arrays.asList("hammer", "jumpitem");
        }

        // По умолчанию — пустой список
        return new ArrayList<>();
    }
}
