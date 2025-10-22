package me.dark_infect.specialevents.listeners;

import me.dark_infect.specialevents.classes.LookTeleporter;
import me.dark_infect.specialevents.utils.Plugininit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class tpLook implements Listener {
    private static final LookTeleporter look = Plugininit.getTep();
    private static final ItemStack stick = new ItemStack(Material.STICK);
    @EventHandler
    public void onRightClick(PlayerInteractEvent ev){
        if(ev.getAction() != Action.RIGHT_CLICK_AIR) return;
        if(ev.getPlayer().getInventory().getItemInMainHand().getItemMeta() == null) return;
        if(ev.getPlayer().getInventory().getItemInOffHand().getItemMeta() != null) return;
        if(Objects.equals(ev.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore(), stick.getItemMeta().getLore())) {
            look.teleportToLookLocation(ev.getPlayer());
        }
    }
    public static void genstick(){
        ItemMeta itemMeta = stick.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§5Энда Палка");
        itemMeta.setLore(List.of("ПАЛКА ТЭПАЛКА"));
        stick.setItemMeta(itemMeta);
    }
    public static ItemStack getStick(){
        return stick;
    }
}
