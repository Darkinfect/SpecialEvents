package me.dark_infect.specialevents.classes.FishModifer;


import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class FishingLogger {

    private final JavaPlugin plugin;
    private final File logFile;
    private final List<LogEntry> recentLogs;
    private final Map<RarityTier, Integer> rarityStats;
    private final SimpleDateFormat dateFormat;

    public FishingLogger(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logFile = new File(plugin.getDataFolder(), "fishing_logs.txt");
        this.recentLogs = new ArrayList<>();
        this.rarityStats = new HashMap<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Инициализация статистики
        for (RarityTier tier : RarityTier.values()) {
            rarityStats.put(tier, 0);
        }

        // Создание файла если не существует
        if (!logFile.exists()) {
            try {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Не удалось создать файл логов!");
            }
        }

        loadRecentLogs();
    }

    public void logCatch(Player player, RarityTier tier, String itemName,
                         int reactionTicks, double calculatedChance,
                         boolean openWater, String biome) {

        String timestamp = dateFormat.format(new Date());
        double reactionTime = reactionTicks * 0.05;

        String logMessage = String.format(
                "[%s] %s поймал %s %s (Реакция: %.2fс, Шанс: %.2f%%, Вода: %s, Биом: %s)",
                timestamp, player.getName(), tier.getDisplayName(),
                itemName, reactionTime, calculatedChance,
                openWater ? "чистая" : "мутная", biome
        );

        // Сохранение в файл
        try (FileWriter fw = new FileWriter(logFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(logMessage);
        } catch (IOException e) {
            plugin.getLogger().warning("Ошибка записи в лог: " + e.getMessage());
        }

        // Добавление в кэш
        LogEntry entry = new LogEntry(
                timestamp, player.getName(), tier, itemName,
                reactionTime, calculatedChance, openWater, biome
        );

        recentLogs.add(0, entry);
        if (recentLogs.size() > 100) {
            recentLogs.remove(recentLogs.size() - 1);
        }

        // Обновление статистики
        rarityStats.put(tier, rarityStats.get(tier) + 1);
    }

    public void logFailedAttempt(Player player, int reactionTicks, String reason) {
        String timestamp = dateFormat.format(new Date());
        double reactionTime = reactionTicks * 0.05;

        String logMessage = String.format(
                "[%s] %s промах (Реакция: %.2fс, Причина: %s)",
                timestamp, player.getName(), reactionTime, reason
        );

        try (FileWriter fw = new FileWriter(logFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(logMessage);
        } catch (IOException e) {
            plugin.getLogger().warning("Ошибка записи в лог: " + e.getMessage());
        }
    }

    private void loadRecentLogs() {
        if (!logFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            // Загружаем последние 100 строк
            int start = Math.max(0, lines.size() - 100);
            for (int i = lines.size() - 1; i >= start; i--) {
                String logLine = lines.get(i);
                LogEntry entry = parseLogEntry(logLine);
                if (entry != null) {
                    recentLogs.add(entry);
                    rarityStats.put(entry.getTier(),
                            rarityStats.get(entry.getTier()) + 1);
                }
            }

        } catch (IOException e) {
            plugin.getLogger().warning("Ошибка чтения логов: " + e.getMessage());
        }
    }

    private LogEntry parseLogEntry(String line) {
        // Парсинг строки лога (упрощённая версия)
        try {
            // Формат: [timestamp] player поймал tier item (детали...)
            if (!line.contains("поймал")) return null;

            String timestamp = line.substring(1, line.indexOf("]"));
            String[] parts = line.split(" ");
            String playerName = parts[1];

            // Определение редкости по цвету/названию
            RarityTier tier = RarityTier.COMMON;
            for (RarityTier t : RarityTier.values()) {
                if (line.contains(t.getDisplayName())) {
                    tier = t;
                    break;
                }
            }

            return new LogEntry(timestamp, playerName, tier, "Unknown",
                    0, 0, false, "Unknown");
        } catch (Exception e) {
            return null;
        }
    }

    public List<LogEntry> getRecentLogs() {
        return new ArrayList<>(recentLogs);
    }

    public Map<RarityTier, Integer> getRarityStats() {
        return new HashMap<>(rarityStats);
    }

    public int getTotalCatches() {
        return rarityStats.values().stream().mapToInt(Integer::intValue).sum();
    }

    public double getRarityPercentage(RarityTier tier) {
        int total = getTotalCatches();
        if (total == 0) return 0.0;
        return (double) rarityStats.get(tier) / total * 100;
    }

    public void clearLogs() {
        recentLogs.clear();
        rarityStats.replaceAll((k, v) -> 0);

        try {
            new FileWriter(logFile, false).close();
        } catch (IOException e) {
            plugin.getLogger().warning("Ошибка очистки логов: " + e.getMessage());
        }
    }

    // Класс для хранения записи лога
    public static class LogEntry {
        private final String timestamp;
        private final String playerName;
        private final RarityTier tier;
        private final String itemName;
        private final double reactionTime;
        private final double calculatedChance;
        private final boolean openWater;
        private final String biome;

        public LogEntry(String timestamp, String playerName, RarityTier tier,
                        String itemName, double reactionTime, double calculatedChance,
                        boolean openWater, String biome) {
            this.timestamp = timestamp;
            this.playerName = playerName;
            this.tier = tier;
            this.itemName = itemName;
            this.reactionTime = reactionTime;
            this.calculatedChance = calculatedChance;
            this.openWater = openWater;
            this.biome = biome;
        }

        public String getTimestamp() { return timestamp; }
        public String getPlayerName() { return playerName; }
        public RarityTier getTier() { return tier; }
        public String getItemName() { return itemName; }
        public double getReactionTime() { return reactionTime; }
        public double getCalculatedChance() { return calculatedChance; }
        public boolean isOpenWater() { return openWater; }
        public String getBiome() { return biome; }
    }
}

