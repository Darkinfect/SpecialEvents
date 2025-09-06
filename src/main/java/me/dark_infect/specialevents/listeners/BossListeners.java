package me.dark_infect.specialevents.listeners;

import me.dark_infect.specialevents.commands.BossCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import javax.swing.*;

public class BossListeners implements Listener {
    private static final int score = 0;
    @EventHandler
    public void OnHitBoss(EntityDamageByEntityEvent event){
        if(BossCommand.isBoss((Player) event.getEntity())){
            if(score == 10){

            }
        }
    }
}
