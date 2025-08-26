package me.dark_infect.specialevents.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class playerUtils {
    private static final Random rand = new Random();

    public static Player getRundomPlayer(){
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (onlinePlayers.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return onlinePlayers.get(random.nextInt(onlinePlayers.size()));
    }
}
