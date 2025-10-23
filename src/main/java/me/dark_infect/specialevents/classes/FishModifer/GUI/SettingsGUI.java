package me.dark_infect.specialevents.classes.FishModifer.GUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.Arrays;
import java.util.List;

public class SettingsGUI {

    private final JavaPlugin plugin;

    public SettingsGUI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§d§lНастройки системы");

        // Декоративные границы
        ItemStack border = createItem(Material.PURPLE_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
            inv.setItem(i + 45, border);
        }
        inv.setItem(17, createItem(Material.EXPERIENCE_BOTTLE,
                "§6§lНастройка бонусов",
                Arrays.asList(
                        "§7Детальная настройка всех",
                        "§7бонусов к шансам",
                        "",
                        "§7Текущий множитель: §e×" +
                                plugin.getConfig().getDouble("bonus-chances.global-multiplier", 1.0),
                        "",
                        "§eНажмите для настройки"
                )
        ));

        // Время ожидания
        int minWait = plugin.getConfig().getInt("fishing.min-wait-time", 60);
        int maxWait = plugin.getConfig().getInt("fishing.max-wait-time", 200);
        inv.setItem(11, createItem(Material.CLOCK,
                "§b§lВремя ожидания",
                Arrays.asList(
                        "§7Минимальное: §e" + minWait + " §7тиков",
                        "§7Максимальное: §e" + maxWait + " §7тиков",
                        "",
                        "§7ЛКМ: §a+10 тиков (мин)",
                        "§7ПКМ: §c-10 тиков (мин)",
                        "§7Shift+ЛКМ: §a+10 тиков (макс)",
                        "§7Shift+ПКМ: §c-10 тиков (макс)"
                )
        ));

        // Окно реакции
        int reactionWindow = plugin.getConfig().getInt("fishing.base-reaction-window", 20);
        inv.setItem(13, createItem(Material.COMPASS,
                "§e§lОкно реакции",
                Arrays.asList(
                        "§7Текущее: §e" + reactionWindow + " §7тиков §7(" +
                                (reactionWindow * 0.05) + "с)",
                        "",
                        "§7ЛКМ: §a+5 тиков",
                        "§7ПКМ: §c-5 тиков",
                        "",
                        "§7Влияет на сложность подсечки"
                )
        ));

        // Бонусы редкости
        inv.setItem(15, createItem(Material.DIAMOND,
                "§9§lБонусы редкости",
                Arrays.asList(
                        "§7Быстрая реакция: §e+" +
                                plugin.getConfig().getInt("fishing.reaction-bonus.perfect", 30) + "%",
                        "§7Открытая вода: §e+" +
                                plugin.getConfig().getInt("fishing.condition-bonus.open-water", 15) + "%",
                        "§7Премиум биом: §e+" +
                                plugin.getConfig().getInt("fishing.condition-bonus.premium-biome", 10) + "%",
                        "",
                        "§eНажмите для изменения"
                )
        ));

        // Логирование
        boolean debugMode = plugin.getConfig().getBoolean("debug", false);
        inv.setItem(20, createItem(
                debugMode ? Material.LIME_DYE : Material.GRAY_DYE,
                "§a§lРежим отладки",
                Arrays.asList(
                        "§7Состояние: " + (debugMode ? "§aВКЛ" : "§cВЫКЛ"),
                        "",
                        "§7Показывает подробную",
                        "§7информацию в консоли",
                        "",
                        "§eНажмите для переключения"
                )
        ));

        // АнтиАФК
        boolean antiAfk = plugin.getConfig().getBoolean("fishing.anti-afk", true);
        inv.setItem(22, createItem(
                antiAfk ? Material.TRIPWIRE_HOOK : Material.STRING,
                "§c§lАнти-АФК",
                Arrays.asList(
                        "§7Состояние: " + (antiAfk ? "§aВКЛ" : "§cВЫКЛ"),
                        "",
                        "§7Требует активной реакции",
                        "§7от игрока",
                        "",
                        "§eНажмите для переключения"
                )
        ));

        // Биомные награды
        boolean biomeRewards = plugin.getConfig().getBoolean("fishing.biome-rewards", true);
        inv.setItem(24, createItem(
                biomeRewards ? Material.GRASS_BLOCK : Material.DIRT,
                "§2§lБиомные награды",
                Arrays.asList(
                        "§7Состояние: " + (biomeRewards ? "§aВКЛ" : "§cВЫКЛ"),
                        "",
                        "§7Уникальный лут по биомам",
                        "",
                        "§eНажмите для переключения"
                )
        ));

        // Контейнеры
        boolean containersEnabled = plugin.getConfig().getBoolean("containers.enabled", true);
        int containerChance = plugin.getConfig().getInt("containers.chance", 10);
        inv.setItem(29, createItem(
                containersEnabled ? Material.CHEST : Material.BARREL,
                "§5§lСистема контейнеров",
                Arrays.asList(
                        "§7Состояние: " + (containersEnabled ? "§aВКЛ" : "§cВЫКЛ"),
                        "§7Шанс: §e" + containerChance + "%",
                        "",
                        "§7ЛКМ: §aВКЛ/ВЫКЛ",
                        "§7ПКМ: §eИзменить шанс"
                )
        ));

        // Множители опыта
        int expMultiplier = plugin.getConfig().getInt("experience.base-multiplier", 2);
        inv.setItem(31, createItem(Material.EXPERIENCE_BOTTLE,
                "§6§lМножитель опыта",
                Arrays.asList(
                        "§7Текущий: §ex" + expMultiplier,
                        "",
                        "§7ЛКМ: §a+0.5",
                        "§7ПКМ: §c-0.5",
                        "",
                        "§7Влияет на получаемый опыт"
                )
        ));

        // Сброс статистики
        inv.setItem(38, createItem(Material.TNT,
                "§c§lСбросить статистику",
                Arrays.asList(
                        "§7Удалить всю статистику",
                        "§7игроков",
                        "",
                        "§c§l§nОСТОРОЖНО!",
                        "§c§oНеобратимое действие!"
                )
        ));

        // Сохранить изменения
        inv.setItem(40, createItem(Material.WRITABLE_BOOK,
                "§a§lСохранить изменения",
                Arrays.asList(
                        "§7Применить все изменения",
                        "§7и перезагрузить конфиг",
                        "",
                        "§eНажмите для сохранения"
                )
        ));

        // Назад
        inv.setItem(49, createItem(Material.ARROW,
                "§cНазад",
                Arrays.asList("§7Вернуться в главное меню")
        ));

        player.openInventory(inv);
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

