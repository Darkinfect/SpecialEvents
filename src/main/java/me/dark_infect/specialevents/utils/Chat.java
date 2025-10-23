package me.dark_infect.specialevents.utils;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class Chat {
    private static final List<UUID> DebugChatUsers = new ArrayList<>();
    private static boolean isOneOnline = false;
    private Chat(){
        throw new IllegalStateException("Utility class");
    }

    public static void sendmessage(CommandSender sender, String message){
        if(sender instanceof ConsoleCommandSender){
            message = message.replace(Color.DARK_AQUA, ChatColor.DARK_AQUA.toString());
        }
        sender.sendMessage(Color.LIGTH_GREEN + "[SpecialEvents] " + Color.AQUA + message);
    }
    public static void sendmessage(CommandSender sender, TextComponent message){
        if(sender instanceof ConsoleCommandSender){
           return;
        }
        TextComponent mess = new TextComponent(Color.LIGTH_GREEN + "[SpecialEvents] " + Color.AQUA + message);
        sender.spigot().sendMessage(mess);
    }
    public static void debugmessage(String message){
        DebugChatUsers.forEach(uuid->{
            if(Bukkit.getPlayer(uuid) != null)
                isOneOnline = true;
        });
        if(DebugChatUsers.isEmpty() || !isOneOnline){
            return;
        }
        DebugChatUsers.forEach(sender->{
            Player target = Bukkit.getPlayer(sender);
            sendmessage(target,message);
        });
        isOneOnline = false;
    }
    public static void addtodebug(Player player){
        DebugChatUsers.add(player.getUniqueId());
    }
    public static void sendPluginMessage(Player player,String message){
        player.sendMessage(Color.GOLD + message);
    }

    public static void console(String string) {
        if (string.startsWith("-") || string.startsWith("[")) {
            Bukkit.getLogger().log(Level.INFO, string);
        }
        else {
            Bukkit.getLogger().log(Level.INFO, "[MD] " + string);
        }
    }
    public static void sendGlobalMessage( String string) {
        Server server = Bukkit.getServer();
        server.getConsoleSender().sendMessage(Color.GOLD + string);
        for (Player player : server.getOnlinePlayers()) {
            if (player.isOp() ) {
                sendmessage(player,   Color.WHITE + " - " + string);
            }
        }

    }
    public static String buildmessage( String string){
        String message;
        message = Color.LIGTH_GREEN + "[MD] " + Color.AQUA + string;
        return message;
    }
    public static List<UUID> getDebugChatUsers(){
        return DebugChatUsers;
    }
}
