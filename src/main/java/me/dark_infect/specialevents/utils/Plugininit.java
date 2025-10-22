package me.dark_infect.specialevents.utils;


import me.dark_infect.specialevents.SpecialEvents;
import me.dark_infect.specialevents.classes.FishModifer.ContainerManager;
import me.dark_infect.specialevents.classes.FishModifer.FishingDataManager;
import me.dark_infect.specialevents.classes.FishModifer.LootGenerator;
import me.dark_infect.specialevents.classes.stevCompleter;
import me.dark_infect.specialevents.commands.BossCommand;
import me.dark_infect.specialevents.commands.stevCMD;
import me.dark_infect.specialevents.listeners.EndEvent;
import me.dark_infect.specialevents.listeners.EyeChall;
import me.dark_infect.specialevents.classes.*;
import me.dark_infect.specialevents.listeners.tpLook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

import static java.lang.System.err;

public class Plugininit {
    private static SpecialEvents plugin = SpecialEvents.getInstance();
    private static PluginManager pluginManager = plugin.getInstance().getServer().getPluginManager();
    private static LookTeleporter tep = new LookTeleporter(plugin,100,3,true,true);
    private static InvulnerabilityManager instance;
    private static FishingDataManager dataManager;
    private static LootGenerator lootGenerator;
    private static ContainerManager containerManager;

    public static InvulnerabilityManager getInstanceInvManager(){
        return instance;
    }

    public static LookTeleporter getTep() {
        return tep;
    }

    public static void init(){
        boolean start = true;
        try{
            instance = new InvulnerabilityManager(plugin);
            dataManager = new FishingDataManager(plugin);
            lootGenerator = new LootGenerator(plugin);
            containerManager = new ContainerManager();
            Checker();
            Items.GenerateItems();
            EyeChall.CheckerF();
            addToDebug();
            registerCommand();
            registerEvent();
        }catch (Exception e){
            e.printStackTrace(err);
        }
    }
    private static void registerCommand(){
        Objects.requireNonNull(plugin.getCommand("stev")).setExecutor(new stevCMD());
        Objects.requireNonNull(plugin.getCommand("stev")).setTabCompleter(new stevCompleter());
        Objects.requireNonNull(plugin.getCommand("boss")).setExecutor(new BossCommand());
        Objects.requireNonNull(plugin.getCommand("boss")).setTabCompleter(new BossCommand());
    }
    private static void registerEvent(){
        pluginManager.registerEvents(new EndEvent(),plugin);
        pluginManager.registerEvents(new EyeChall(), plugin);
        pluginManager.registerEvents(new tpLook(),plugin);
    }
    public static void Checker(){
        new BukkitRunnable(){
            @Override
            public void run(){
                if(FinalFigth.getInstance() == null) return;
                if(FinalFigth.getInstance().CheckEgg()){
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin,0L,20L);
    }
    private static void addToDebug(){
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(player.hasPermission("se.debug")) Chat.addtodebug(player);
        });
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(player.hasPermission("se.debug")) Chat.addtodebug(player);
        });
    }
    public static FishingDataManager getDataManager() {
        return dataManager;
    }

    public static LootGenerator getLootGenerator() {
        return lootGenerator;
    }

    public static ContainerManager getContainerManager() {
        return containerManager;
    }
}
