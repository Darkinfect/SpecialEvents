package me.dark_infect.specialevents.classes.FishModifer.GUI;

import me.dark_infect.specialevents.classes.FishModifer.RarityTier;
import me.dark_infect.specialevents.utils.Plugininit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StatisticsGUI {

    private final JavaPlugin plugin;

    public StatisticsGUI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§b§lСтатистика выпадения");

        // Декоративные границы
        ItemStack border = createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
            inv.setItem(i + 45, border);
        }

        Map<RarityTier, Integer> stats = Plugininit.getFishingLogger().getRarityStats();
        int totalCatches = Plugininit.getFishingLogger().getTotalCatches();

        // Обычный (Серый)
        inv.setItem(11, createRarityItem(
                Material.GRAY_WOOL, RarityTier.COMMON, stats, totalCatches
        ));

        // Необычный (Зелёный)
        inv.setItem(13, createRarityItem(
                Material.LIME_WOOL, RarityTier.UNCOMMON, stats, totalCatches
        ));

        // Редкий (Синий)
        inv.setItem(15, createRarityItem(
                Material.BLUE_WOOL, RarityTier.RARE, stats, totalCatches
        ));

        // Эпический (Фиолетовый)
        inv.setItem(21, createRarityItem(
                Material.PURPLE_WOOL, RarityTier.EPIC, stats, totalCatches
        ));

        // Легендарный (Золотой)
        inv.setItem(23, createRarityItem(
                Material.YELLOW_WOOL, RarityTier.LEGENDARY, stats, totalCatches
        ));

        // Общая статистика
        inv.setItem(31, createItem(Material.ENCHANTED_BOOK,
                "§6§lОбщая статистика",
                Arrays.asList(
                        "§7━━━━━━━━━━━━━━━━━━━",
                        "§7Всего уловов: §e" + totalCatches,
                        "§7Средний уровень: §e" + calculateAverageRarity(stats, totalCatches),
                        "",
                        "§7Самый популярный: " + getMostCommonRarity(stats).getFormattedName(),
                        "§7Самый редкий: " + getLeastCommonRarity(stats).getFormattedName(),
                        "§7━━━━━━━━━━━━━━━━━━━"
                )
        ));

        // График (визуальное представление)
        displayGraph(inv, stats, totalCatches);

        // Назад
        inv.setItem(49, createItem(Material.ARROW,
                "§cНазад",
                Arrays.asList("§7Вернуться в главное меню")
        ));

        player.openInventory(inv);
    }

    private ItemStack createRarityItem(Material material, RarityTier tier,
                                       Map<RarityTier, Integer> stats, int total) {
        int count = stats.getOrDefault(tier, 0);
        double percentage = total > 0 ? (double) count / total * 100 : 0;
        double expected = tier.getBaseChance();
        double deviation = percentage - expected;

        List<String> lore = new ArrayList<>();
        lore.add("§7━━━━━━━━━━━━━━━━━━━");
        lore.add("§7Базовый шанс: §e" + String.format("%.1f%%", expected));
        lore.add("§7Фактический: §e" + String.format("%.2f%%", percentage));
        lore.add("§7Отклонение: " + (deviation >= 0 ? "§a+" : "§c") +
                String.format("%.2f%%", deviation));
        lore.add("");
        lore.add("§7Поймано: §e" + count + " §7из §e" + total);
        lore.add("§7━━━━━━━━━━━━━━━━━━━");
        lore.add("");

        // Индикатор бар
        int bars = (int) (percentage / 2); // Максимум 50 баров = 100%
        StringBuilder bar = new StringBuilder("§7[");
        for (int i = 0; i < 25; i++) {
            if (i < bars) {
                bar.append("§a█");
            } else {
                bar.append("§8█");
            }
        }
        bar.append("§7]");
        lore.add(bar.toString());

        return createItem(material, tier.getFormattedName(), lore);
    }

    private void displayGraph(Inventory inv, Map<RarityTier, Integer> stats, int total) {
        // Визуальное представление в виде столбцов
        for (RarityTier tier : RarityTier.values()) {
            int count = stats.getOrDefault(tier, 0);
            double percentage = total > 0 ? (double) count / total * 100 : 0;
            int height = (int) Math.ceil(percentage / 5); // 1 блок = 5%

            int column = 37 + tier.ordinal(); // Колонки 37-41

            for (int i = 0; i < Math.min(3, height); i++) {
                int slot = column - (i * 9);
                if (slot >= 0 && slot < 54) {
                    Material blockType = getBlockForTier(tier);
                    inv.setItem(slot, createItem(blockType,
                            tier.getColorCode() + "█",
                            Arrays.asList("§7" + String.format("%.1f%%", percentage))
                    ));
                }
            }
        }
    }

    private Material getBlockForTier(RarityTier tier) {
        switch (tier) {
            case COMMON: return Material.GRAY_CONCRETE;
            case UNCOMMON: return Material.LIME_CONCRETE;
            case RARE: return Material.BLUE_CONCRETE;
            case EPIC: return Material.PURPLE_CONCRETE;
            case LEGENDARY: return Material.YELLOW_CONCRETE;
            default: return Material.WHITE_CONCRETE;
        }
    }

    private String calculateAverageRarity(Map<RarityTier, Integer> stats, int total) {
        if (total == 0) return "§7N/A";

        double sum = 0;
        for (RarityTier tier : RarityTier.values()) {
            sum += stats.getOrDefault(tier, 0) * tier.ordinal();
        }

        double average = sum / total;
        return String.format("%.2f", average);
    }

    private RarityTier getMostCommonRarity(Map<RarityTier, Integer> stats) {
        return stats.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(RarityTier.COMMON);
    }

    private RarityTier getLeastCommonRarity(Map<RarityTier, Integer> stats) {
        return stats.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(RarityTier.LEGENDARY);
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

