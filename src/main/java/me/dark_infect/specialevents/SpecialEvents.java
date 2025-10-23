package me.dark_infect.specialevents;

import me.dark_infect.specialevents.utils.Plugininit;
import org.bukkit.plugin.java.JavaPlugin;


public final class SpecialEvents extends JavaPlugin {
    private static SpecialEvents instance;
    public static SpecialEvents getInstance(){
        return instance;
    }
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Plugininit.init();
    }

    @Override
    public void onDisable() {
        Plugininit.getDataManager().saveAllData();
        getLogger().info("Interactive Fishing отключён!");
    }

}
