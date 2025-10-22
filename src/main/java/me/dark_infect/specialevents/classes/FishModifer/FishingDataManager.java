package me.dark_infect.specialevents.classes.FishModifer;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FishingDataManager {

    private final JavaPlugin plugin;
    private final Map<UUID, Integer> streakCache;
    private final Map<UUID, Long> lastCatchTime;

    private final NamespacedKey totalCatchesKey;
    private final NamespacedKey successfulCatchesKey;
    private final NamespacedKey bestStreakKey;
    private final NamespacedKey legendaryCountKey;

    public FishingDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.streakCache = new HashMap<>();
        this.lastCatchTime = new HashMap<>();

        this.totalCatchesKey = new NamespacedKey(plugin, "total_catches");
        this.successfulCatchesKey = new NamespacedKey(plugin, "successful_catches");
        this.bestStreakKey = new NamespacedKey(plugin, "best_streak");
        this.legendaryCountKey = new NamespacedKey(plugin, "legendary_count");
    }

    public int getTotalCatches(Player player) {
        return player.getPersistentDataContainer()
                .getOrDefault(totalCatchesKey, PersistentDataType.INTEGER, 0);
    }

    public int getSuccessfulCatches(Player player) {
        return player.getPersistentDataContainer()
                .getOrDefault(successfulCatchesKey, PersistentDataType.INTEGER, 0);
    }

    public int getBestStreak(Player player) {
        return player.getPersistentDataContainer()
                .getOrDefault(bestStreakKey, PersistentDataType.INTEGER, 0);
    }

    public int getLegendaryCount(Player player) {
        return player.getPersistentDataContainer()
                .getOrDefault(legendaryCountKey, PersistentDataType.INTEGER, 0);
    }

    public void incrementTotalCatches(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        int current = getTotalCatches(player);
        data.set(totalCatchesKey, PersistentDataType.INTEGER, current + 1);
    }

    public void incrementSuccessfulCatches(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        int current = getSuccessfulCatches(player);
        data.set(successfulCatchesKey, PersistentDataType.INTEGER, current + 1);
    }

    public void incrementLegendaryCount(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        int current = getLegendaryCount(player);
        data.set(legendaryCountKey, PersistentDataType.INTEGER, current + 1);
    }

    public FishingSkill getSkillLevel(Player player) {
        int catches = getSuccessfulCatches(player);
        return FishingSkill.getByProgress(catches);
    }

    public int getCurrentStreak(Player player) {
        return streakCache.getOrDefault(player.getUniqueId(), 0);
    }

    public void updateStreak(Player player, boolean success) {
        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long lastTime = lastCatchTime.getOrDefault(uuid, 0L);

        if (currentTime - lastTime > 10000) {
            streakCache.put(uuid, 0);
        }

        if (success) {
            int currentStreak = streakCache.getOrDefault(uuid, 0) + 1;
            streakCache.put(uuid, currentStreak);
            lastCatchTime.put(uuid, currentTime);

            if (currentStreak > getBestStreak(player)) {
                player.getPersistentDataContainer()
                        .set(bestStreakKey, PersistentDataType.INTEGER, currentStreak);
            }
        } else {
            streakCache.put(uuid, 0);
        }
    }

    public double getSuccessRate(Player player) {
        int total = getTotalCatches(player);
        if (total == 0) return 0.0;
        return (double) getSuccessfulCatches(player) / total * 100;
    }

    public void saveAllData() {
        streakCache.clear();
        lastCatchTime.clear();
    }
}
