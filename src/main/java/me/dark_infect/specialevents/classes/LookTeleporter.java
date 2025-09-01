package me.dark_infect.specialevents.classes;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.joml.Vector4d;
import org.joml.Vector4dc;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LookTeleporter {

    private final Set<UUID> teleportingPlayers = new HashSet<>();
    private final JavaPlugin plugin;

    private int maxDistance = 100;
    private int cooldownSeconds = 20;
    private boolean requireEmptyBlock = true;
    private boolean playEffects = true;

    public LookTeleporter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public LookTeleporter(JavaPlugin plugin, int maxDistance, int cooldownSeconds,
                          boolean requireEmptyBlock, boolean playEffects) {
        this.plugin = plugin;
        this.maxDistance = maxDistance;
        this.cooldownSeconds = cooldownSeconds;
        this.requireEmptyBlock = requireEmptyBlock;
        this.playEffects = playEffects;
    }
    public void addPlayer(Player player) {
        if (teleportingPlayers.contains(player.getUniqueId())) {
            return;
        }
        teleportingPlayers.add(player.getUniqueId());
    }
    public void removePlayer(Player player) {
        teleportingPlayers.remove(player.getUniqueId());
    }
    public boolean containsPlayer(Player player) {
        return teleportingPlayers.contains(player.getUniqueId());
    }
    public void clearAllPlayers() {
        teleportingPlayers.clear();
    }

   public boolean teleportToLookLocation(Player player) {
        Location targetLocation = getTargetLocation(player);
        player.getLocation().getDirection();

        if (targetLocation == null) {
            player.sendMessage(ChatColor.RED + "Слишком далеко или не вижу блок!");
            return false;
        }
        if(isOnCooldown(player)){
            player.sendMessage(ChatColor.GREEN + "Кулдаун " + cooldownSeconds);
            return true;
        }
        teleportWithEffects(player, targetLocation);

        return true;
    }

    private void teleportWithEffects(Player player, Location target) {
        Location original = player.getLocation();


        if (playEffects) {
            playEffects(original, Particle.PORTAL, Sound.ENTITY_ENDERMAN_TELEPORT);
        }
        target.setDirection(original.getDirection());


        player.teleport(target);

        if (playEffects) {
            playEffects(target, Particle.REVERSE_PORTAL, Sound.ENTITY_ENDERMAN_TELEPORT);
        }
        setCooldown(player);
    }

    private Location getTargetLocation(Player player) {
        Block targetBlock = player.getTargetBlockExact(maxDistance);
        if (targetBlock == null || targetBlock.getType() == Material.AIR) {
            return null;
        }

        return targetBlock.getLocation().add(0.5, 1, 0.5);
    }

    private void playEffects(Location location, Particle particle, Sound sound) {
        location.getWorld().spawnParticle(particle, location, 30, 0.5, 0.5, 0.5, 0.1);
        location.getWorld().playSound(location, sound, 1.0f, 1.0f);
    }

    // Кулдаун методы
    private final Set<UUID> cooldowns = new HashSet<>();

    private boolean isOnCooldown(Player player) {
        return cooldowns.contains(player.getUniqueId());
    }

    private int getCooldownRemaining(Player player) {
        return cooldownSeconds; // Простая реализация
    }

    private void setCooldown(Player player) {
        cooldowns.add(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                cooldowns.remove(player.getUniqueId());
            }
        }.runTaskLater(plugin, cooldownSeconds * 20L);
    }

    // Геттеры и сеттеры
    public int getMaxDistance() { return maxDistance; }
    public void setMaxDistance(int maxDistance) { this.maxDistance = maxDistance; }

    public int getCooldownSeconds() { return cooldownSeconds; }
    public void setCooldownSeconds(int cooldownSeconds) { this.cooldownSeconds = cooldownSeconds; }

    public boolean isRequireEmptyBlock() { return requireEmptyBlock; }
    public void setRequireEmptyBlock(boolean requireEmptyBlock) { this.requireEmptyBlock = requireEmptyBlock; }

    public boolean isPlayEffects() { return playEffects; }
    public void setPlayEffects(boolean playEffects) { this.playEffects = playEffects; }

    public int getPlayersCount() { return teleportingPlayers.size(); }
}