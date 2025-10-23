package me.dark_infect.specialevents.classes.FishModifer.GUI;

import me.dark_infect.specialevents.classes.FishModifer.FishingLogger;
import me.dark_infect.specialevents.classes.FishModifer.RarityTier;
import me.dark_infect.specialevents.utils.Plugininit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogsGUI {

    private final JavaPlugin plugin;
    private int currentPage = 0;
    private static final int LOGS_PER_PAGE = 28;

    public LogsGUI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, int page) {
        this.currentPage = page;

        List<FishingLogger.LogEntry> logs = Plugininit.getFishingLogger().getRecentLogs();
        int totalPages = (int) Math.ceil((double) logs.size() / LOGS_PER_PAGE);

        Inventory inv = Bukkit.createInventory(null, 54,
                "§e§lЛоги уловов §7(Стр. " + (page + 1) + "/" + totalPages + ")");

        // Декоративные границы
        ItemStack border = createItem(Material.YELLOW_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
            inv.setItem(i + 45, border);
        }

        // Отображение логов
        int start = page * LOGS_PER_PAGE;
        int end = Math.min(start + LOGS_PER_PAGE, logs.size());

        for (int i = start; i < end; i++) {
            FishingLogger.LogEntry log = logs.get(i);
            int slot = 10 + ((i - start) % 7) + ((i - start) / 7) * 9;

            if (slot >= 10 && slot < 44) {
                inv.setItem(slot, createLogItem(log, i + 1));
            }
        }

        // Навигация
        if (page > 0) {
            inv.setItem(45, createItem(Material.ARROW,
                    "§aПредыдущая страница",
                    Arrays.asList("§7Страница " + page)
            ));
        }

        if (page < totalPages - 1) {
            inv.setItem(53, createItem(Material.ARROW,
                    "§aСледующая страница",
                    Arrays.asList("§7Страница " + (page + 2))
            ));
        }

        // Фильтры
        inv.setItem(48, createItem(Material.HOPPER,
                "§b§lФильтры",
                Arrays.asList(
                        "§7Фильтровать по:",
                        "§7• Редкости",
                        "§7• Игроку",
                        "§7• Биому",
                        "",
                        "§c§oСкоро..."
                )
        ));

        // Назад
        inv.setItem(49, createItem(Material.BARRIER,
                "§cНазад",
                Arrays.asList("§7Вернуться в главное меню")
        ));

        player.openInventory(inv);
    }

    private ItemStack createLogItem(FishingLogger.LogEntry log, int number) {
        Material material = getMaterialForTier(log.getTier());

        List<String> lore = new ArrayList<>();
        lore.add("§7━━━━━━━━━━━━━━━━━━━");
        lore.add("§7Время: §e" + log.getTimestamp());
        lore.add("§7Игрок: §e" + log.getPlayerName());
        lore.add("§7Редкость: " + log.getTier().getFormattedName());
        lore.add("§7Предмет: §e" + log.getItemName());
        lore.add("");
        lore.add("§7Реакция: §e" + String.format("%.2fс", log.getReactionTime()));
        lore.add("§7Шанс: §e" + String.format("%.2f%%", log.getCalculatedChance()));
        lore.add("§7Вода: " + (log.isOpenWater() ? "§aЧистая" : "§cМутная"));
        lore.add("§7Биом: §e" + log.getBiome());
        lore.add("§7━━━━━━━━━━━━━━━━━━━");

        return createItem(material,
                "§f#" + number + " " + log.getTier().getColorCode() + log.getItemName(),
                lore);
    }

    private Material getMaterialForTier(RarityTier tier) {
        switch (tier) {
            case COMMON: return Material.PAPER;
            case UNCOMMON: return Material.LIME_DYE;
            case RARE: return Material.LAPIS_LAZULI;
            case EPIC: return Material.AMETHYST_SHARD;
            case LEGENDARY: return Material.NETHER_STAR;
            default: return Material.PAPER;
        }
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

    public int getCurrentPage() {
        return currentPage;
    }
}
