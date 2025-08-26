package me.dark_infect.specialevents.utils;


import me.dark_infect.specialevents.SpecialEvents;
import me.dark_infect.specialevents.classes.stevCompleter;
import me.dark_infect.specialevents.commands.stevCMD;
import me.dark_infect.specialevents.listeners.EndEvent;
import org.bukkit.plugin.PluginManager;

public class plugininit {
    private static SpecialEvents plugin = SpecialEvents.getInstance();
    private static PluginManager pluginManager = plugin.getInstance().getServer().getPluginManager();


    public static boolean init(){
        boolean start = true;
        try{
            registerCommand();
            registerEvent();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return start;
    }
    private static void registerCommand(){
        plugin.getCommand("stev").setExecutor(new stevCMD());
        plugin.getCommand("stev").setTabCompleter(new stevCompleter());
    }
    private static void registerEvent(){
        pluginManager.registerEvents(new EndEvent(),plugin);
    }

}
