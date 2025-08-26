package me.dark_infect.specialevents.classes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class stevCompleter implements TabCompleter {
    private final List<String> FirstArgs = List.of(
            "event",
            "debugchat"
    );
    private final List<String> SecondArgs = List.of(
            "inflections",
            "setlocation",
            "start",
            "stop",
            "setflag",
            "setinfl",
            "growup"
    );
    private final List<String> ThirdArgs = List.of(
            "true"
    );
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 1){
            return FirstArgs;
        }else if(strings.length == 2 && strings[0].equalsIgnoreCase("event")){
            return SecondArgs;
        }else if(strings.length == 3 && strings[1].equalsIgnoreCase("setflag")){
            return ThirdArgs;
        }
        return null;
    }
}
