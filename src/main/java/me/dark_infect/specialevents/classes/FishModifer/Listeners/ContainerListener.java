package me.dark_infect.specialevents.classes.FishModifer.Listeners;

import me.dark_infect.specialevents.utils.Plugininit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContainerListener implements Listener {

    private final Map<UUID, Long> cooldowns;

    public ContainerListener() {
        this.cooldowns = new HashMap<>();
    }

    @EventHandler
    public void onContainerUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) return;

        if (!Plugininit.getContainerManager().isLootContainer(item)) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();

        // Проверка кулдауна
        long now = System.currentTimeMillis();
        long lastUse = cooldowns.getOrDefault(player.getUniqueId(), 0L);

        if (now - lastUse < 500) {
            return; // 0.5 сек кулдаун
        }

        cooldowns.put(player.getUniqueId(), now);

        // Открытие контейнера
        Plugininit.getContainerManager().openContainer(player, item);
    }
}
