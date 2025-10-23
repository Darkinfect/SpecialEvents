package me.dark_infect.specialevents.classes.FishModifer.GUI;

import me.dark_infect.specialevents.classes.FishModifer.RarityTier;
import me.dark_infect.specialevents.utils.Plugininit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TestingGUI {

    private final JavaPlugin plugin;
    private Map<RarityTier, Integer> simulationResults;
    int simulationCount = 100;

    public TestingGUI(JavaPlugin plugin) {
        this.plugin = plugin;
        this.simulationResults = new HashMap<>();
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6§lТестирование системы");

        // Декоративные границы
        ItemStack border = createItem(Material.ORANGE_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
            inv.setItem(i + 45, border);
        }

        // Симуляция уловов
        inv.setItem(11, createItem(Material.FISHING_ROD,
                "§b§lСимуляция уловов",
                Arrays.asList(
                        "§7Текущее количество: §e" + simulationCount,
                        "",
                        "§7Запустить симуляцию " + simulationCount + " уловов",
                        "§7и посмотреть распределение",
                        "",
                        "§eНажмите для запуска"
                )
        ));

        // Изменение количества симуляций
        inv.setItem(13, createItem(Material.REPEATER,
                "§e§lКоличество симуляций",
                Arrays.asList(
                        "§7Текущее: §e" + simulationCount,
                        "",
                        "§7ЛКМ: §a+100",
                        "§7ПКМ: §c-100",
                        "§7Shift+ЛКМ: §a+1000",
                        "§7Shift+ПКМ: §c-1000"
                )
        ));

        // Тест конкретной редкости
        inv.setItem(15, createItem(Material.DIAMOND,
                "§9§lТест шанса редкости",
                Arrays.asList(
                        "§7Рассчитать реальный шанс",
                        "§7получения каждой редкости",
                        "§7с текущими настройками",
                        "",
                        "§eНажмите для расчёта"
                )
        ));

        // Результаты последней симуляции
        if (!simulationResults.isEmpty()) {
            displaySimulationResults(inv);
        }

        // Тест производительности
        inv.setItem(29, createItem(Material.CLOCK,
                "§d§lТест производительности",
                Arrays.asList(
                        "§7Проверить скорость",
                        "§7генерации лута",
                        "",
                        "§eНажмите для теста"
                )
        ));

        // Выдать тестовые предметы
        inv.setItem(31, createItem(Material.CHEST,
                "§a§lПолучить тестовые предметы",
                Arrays.asList(
                        "§7Выдать по 1 предмету",
                        "§7каждой редкости",
                        "",
                        "§eНажмите для получения"
                )
        ));

        // Симуляция реакции
        inv.setItem(33, createItem(Material.COMPARATOR,
                "§c§lТест времени реакции",
                Arrays.asList(
                        "§7Проверить влияние",
                        "§7времени реакции на редкость",
                        "",
                        "§eНажмите для теста"
                )
        ));

        // Очистить результаты
        inv.setItem(40, createItem(Material.BUCKET,
                "§7§lОчистить результаты",
                Arrays.asList(
                        "§7Удалить результаты",
                        "§7последней симуляции"
                )
        ));

        // Назад
        inv.setItem(49, createItem(Material.ARROW,
                "§cНазад",
                Arrays.asList("§7Вернуться в главное меню")
        ));

        player.openInventory(inv);
    }

    public void runSimulation(Player player, int count) {
        player.sendMessage("§e⏳ Запуск симуляции " + count + " уловов...");

        simulationResults.clear();
        for (RarityTier tier : RarityTier.values()) {
            simulationResults.put(tier, 0);
        }

        // Симуляция
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < count; i++) {
                // Случайные условия
                int reactionTicks = ThreadLocalRandom.current().nextInt(5, 30);
                boolean openWater = ThreadLocalRandom.current().nextBoolean();
                boolean isNight = ThreadLocalRandom.current().nextBoolean();

                // Передаём null для биома, если не хотим его учитывать в симуляции
                RarityTier tier = Plugininit.getLootGenerator().calculateRarity(
                        player, reactionTicks, openWater, null, isNight);

                simulationResults.put(tier, simulationResults.get(tier) + 1);
            }

            long duration = System.currentTimeMillis() - startTime;

            player.sendMessage("§a✓ Симуляция завершена за " + duration + "мс");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

        } catch (Exception e) {
            player.sendMessage("§c✖ Ошибка во время симуляции: " + e.getMessage());
            plugin.getLogger().severe("Simulation failed: " + e.getMessage());
            e.printStackTrace();
        }

        open(player);
    }

    private void displaySimulationResults(Inventory inv) {
        int total = simulationResults.values().stream().mapToInt(Integer::intValue).sum();

        int slot = 20;
        for (RarityTier tier : RarityTier.values()) {
            int count = simulationResults.getOrDefault(tier, 0);
            double percentage = total > 0 ? (double) count / total * 100 : 0;
            double expected = tier.getBaseChance();
            double deviation = percentage - expected;

            Material material;
            switch (tier) {
                case COMMON: material = Material.GRAY_WOOL; break;
                case UNCOMMON: material = Material.LIME_WOOL; break;
                case RARE: material = Material.BLUE_WOOL; break;
                case EPIC: material = Material.PURPLE_WOOL; break;
                case LEGENDARY: material = Material.YELLOW_WOOL; break;
                default: material = Material.WHITE_WOOL;
            }

            inv.setItem(slot, createItem(material,
                    tier.getFormattedName(),
                    Arrays.asList(
                            "§7━━━━━━━━━━━━━━━━━━━",
                            "§7Ожидалось: §e" + String.format("%.1f%%", expected),
                            "§7Получено: §e" + String.format("%.2f%%", percentage),
                            "§7Отклонение: " + (deviation >= 0 ? "§a+" : "§c") +
                                    String.format("%.2f%%", deviation),
                            "",
                            "§7Количество: §e" + count + " §7из §e" + total,
                            "§7━━━━━━━━━━━━━━━━━━━"
                    )
            ));

            slot++;
        }
    }

    public void testReactionTime(Player player) {
        player.sendMessage("§e⏳ Тестирование влияния времени реакции...");
        player.sendMessage("");

        int[] reactionTimes = {5, 10, 15, 20, 25, 30}; // В тиках

        for (int ticks : reactionTimes) {
            Map<RarityTier, Integer> results = new HashMap<>();
            for (RarityTier tier : RarityTier.values()) {
                results.put(tier, 0);
            }

            // 1000 симуляций для каждого времени
            for (int i = 0; i < 1000; i++) {
                RarityTier tier = Plugininit.getLootGenerator().calculateRarity(
                        player, ticks, true, null, false);
                results.put(tier, results.get(tier) + 1);
            }

            double legendaryChance = (double) results.get(RarityTier.LEGENDARY) / 10.0;
            double epicChance = (double) results.get(RarityTier.EPIC) / 10.0;

            player.sendMessage(String.format(
                    "§7Реакция §e%.2fс§7: Легендарный §6%.1f%%§7, Эпический §5%.1f%%",
                    ticks * 0.05, legendaryChance, epicChance
            ));
        }

        player.sendMessage("");
        player.sendMessage("§a✓ Тест завершён!");
    }

    public void giveTestItems(Player player) {
        for (RarityTier tier : RarityTier.values()) {
            ItemStack item = Plugininit.getLootGenerator().generateLoot(player, tier, null);
            player.getInventory().addItem(item);
        }

        player.sendMessage("§a✓ Получены тестовые предметы всех редкостей!");
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
    }

    public void testPerformance(Player player) {
        player.sendMessage("§e⏳ Тестирование производительности...");

        int iterations = 10000;
        long startTime = System.nanoTime();

        try {
            for (int i = 0; i < iterations; i++) {
                // Передаём null для биома, так как это тест производительности
                ItemStack item = Plugininit.getLootGenerator().generateLoot(
                        player, RarityTier.RARE, null);

                // Проверка что предмет создан
                if (item == null) {
                    plugin.getLogger().warning("Generated null item during performance test!");
                }
            }

            long duration = (System.nanoTime() - startTime) / 1000000; // В миллисекундах
            double avgTime = (double) duration / iterations;

            player.sendMessage("§a✓ Результаты теста:");
            player.sendMessage("§7Всего генераций: §e" + iterations);
            player.sendMessage("§7Общее время: §e" + duration + "мс");
            player.sendMessage("§7Среднее время: §e" + String.format("%.4f", avgTime) + "мс");
            player.sendMessage("§7Генераций в секунду: §e" + (int)(1000.0 / avgTime));

        } catch (Exception e) {
            player.sendMessage("§c✖ Ошибка во время теста: " + e.getMessage());
            plugin.getLogger().severe("Performance test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void adjustSimulationCount(boolean increase, boolean shift) {
        int change = shift ? 1000 : 100;
        if (increase) {
            simulationCount = Math.min(10000, simulationCount + change);
        } else {
            simulationCount = Math.max(100, simulationCount - change);
        }
    }

    public void clearResults() {
        simulationResults.clear();
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }

        return item;
    }
}

