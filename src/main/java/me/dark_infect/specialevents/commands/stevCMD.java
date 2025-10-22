package me.dark_infect.specialevents.commands;

import me.dark_infect.specialevents.classes.FinalFigth;
import me.dark_infect.specialevents.classes.Items;
import me.dark_infect.specialevents.listeners.EndEvent;
import me.dark_infect.specialevents.listeners.EyeChall;
import me.dark_infect.specialevents.listeners.tpLook;
import me.dark_infect.specialevents.utils.Chat;
import me.dark_infect.specialevents.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.*;

public class stevCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;

        if(strings[0].equalsIgnoreCase("event")){
            if(strings[1].equalsIgnoreCase("setlocation")){
                new FinalFigth(player.getLocation(), 15);
                Chat.sendmessage(player,"Локация была успешно установлена");
                return true;
            }else if(strings[1].equalsIgnoreCase("start")){
                EndEvent.startInfection();
                EndEvent.startThunder();
                return true;
            }else if(strings[1].equalsIgnoreCase("inflections")){
                printLocInf(player);
                return true;
            }else if(strings[1].equalsIgnoreCase("stop")){
                EndEvent.setIsCured(true);
                Bukkit.broadcastMessage(ChatColor.RED + "Какой ужас, заражение было остановленой (Ващеeeeeeeeeeeeeeee жеесть <Xarill>)");
                return true;
            }else if(strings[1].equalsIgnoreCase("flaggrowup")){
                Chat.sendmessage(player,EndEvent.isIsCured() ? "false" : "true");
                return true;
            }else if(strings[1].equalsIgnoreCase("setinfl")){
                EndEvent.setInflections(player);
                Chat.sendmessage(player, "Семя было помещено в блок.");
                return true;
            }else if(strings[1].equalsIgnoreCase("growup")){
                EndEvent.growup();
                return true;
            }else if(strings[1].equalsIgnoreCase("spawnvex")){
                return true;
            }else if(strings[1].equalsIgnoreCase("setflag")){
                if(strings[2].equalsIgnoreCase("true")){
                    EndEvent.setTimer(20*60*5);
                    Chat.sendmessage(commandSender, "Флаг изменён");
                    return true;
                }
                return true;
            }else if(strings[1].equalsIgnoreCase("giveeye")){
                player.getInventory().addItem(Items.GetSpecialEnderEye());
                Chat.sendmessage(player,"Око было выдано.");
                return true;
            }else if(strings[1].equalsIgnoreCase("info")){
                if(Bukkit.getPlayer(strings[2]) == null){
                    Chat.sendmessage(player,"Игрок не найден");
                    return true;
                }
                EyeChall.PrintInfo(Bukkit.getPlayer(strings[2]));
            }else if(strings[1].equalsIgnoreCase("give")){
                player.getInventory().addItem(tpLook.getStick());
//                if(Bukkit.getPlayer(strings[2]) == null){
//                    Chat.sendmessage(player,"Игрок не найден");
//                    return true;
//                }
//                plugininit.getTep().addPlayer(Objects.requireNonNull(Bukkit.getPlayer(strings[2])));
            }else if(strings[1].equalsIgnoreCase("message")){
                Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " " + ChatColor.GOLD + "Выполнил испытание и получил Древнее око");
            }
            return true;
        }else if(strings[0].equalsIgnoreCase("debugchat")){
            if(Chat.getDebugChatUsers().contains(player.getUniqueId())){
                Chat.getDebugChatUsers().remove(player.getUniqueId());
                Chat.sendmessage(player,"Debug chat был деактивирован.");
                return true;
            }
            Chat.addtodebug(player);
            Chat.sendmessage(player,"Активирован debug чат");
            return true;
        }
        return false;
    }
    private static void printLocInf(Player target){
        EndEvent.getInflections().forEach(inflection -> {
            int x = inflection.getCenter().getBlockX();
            int y = inflection.getCenter().getBlockY() + 1;
            int z = inflection.getCenter().getBlockZ();


            String coordsText = x + " " + y + " " + z;


            TextComponent coords = new TextComponent( Color.LIGTH_GREEN + "[SpecialEvents] " + Color.AQUA + coordsText);

            coords.setClickEvent(new ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/tp " + x + " " + y + " " + z
            ));
            coords.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("§eТелепортироваться на " + coordsText).create()
            ));

            target.spigot().sendMessage(coords);
        });
    }

}
