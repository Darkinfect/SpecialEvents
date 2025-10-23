package me.dark_infect.specialevents.classes.FishModifer.GUI;


import me.dark_infect.specialevents.utils.Plugininit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class AdminPanelGUI {

    private final JavaPlugin plugin;

    public AdminPanelGUI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6§l⚙ Админ-панель рыбалки");

        // Декоративные границы
        ItemStack border = createItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
            inv.setItem(i + 45, border);
        }
        for (int i = 0; i < 54; i += 9) {
            inv.setItem(i, border);
            inv.setItem(i + 8, border);
        }

        // Статистика выпадения
        inv.setItem(11, createItem(Material.CARROT_ON_A_STICK,
                "§b§lСтатистика выпадения",
                Arrays.asList(
                        "§7Просмотр процентов выпадения",
                        "§7каждой редкости",
                        "",
                        "§eНажмите для просмотра"
                )
        ));

        // Логи уловов
        inv.setItem(13, createItem(Material.BOOK,
                "§e§lЛоги уловов",
                Arrays.asList(
                        "§7Последние 100 уловов",
                        "§7с подробной информацией",
                        "",
                        "§eНажмите для просмотра"
                )
        ));

        // Управление игроками
        inv.setItem(15, createItem(Material.PLAYER_HEAD,
                "§a§lСписок игроков",
                Arrays.asList(
                        "§7Статистика всех игроков",
                        "§7и их достижения",
                        "",
                        "§eНажмите для просмотра"
                )
        ));

        // Настройки системы
        inv.setItem(20, createItem(Material.COMPARATOR,
                "§d§lНастройки системы",
                Arrays.asList(
                        "§7Изменение параметров",
                        "§7рыбалки",
                        "",
                        "§eНажмите для настройки"
                )
        ));

        // Тестирование
        inv.setItem(22, createItem(Material.COMMAND_BLOCK,
                "§6§lТестирование",
                Arrays.asList(
                        "§7Симуляция уловов",
                        "§7и проверка шансов",
                        "",
                        "§eНажмите для теста"
                )
        ));

        // Управление лутом
        inv.setItem(24, createItem(Material.CHEST,
                "§5§lУправление лутом",
                Arrays.asList(
                        "§7Настройка наград",
                        "§7по редкостям",
                        "",
                        "§eНажмите для изменения"
                )
        ));

        // Очистка логов
        inv.setItem(31, createItem(Material.TNT,
                "§c§lОчистить логи",
                Arrays.asList(
                        "§7Удалить все записи",
                        "§7из файла логов",
                        "",
                        "§c§lОСТОРОЖНО!"
                )
        ));

        // Перезагрузка конфига
        inv.setItem(40, createItem(Material.REDSTONE,
                "§a§lПерезагрузить конфиг",
                Arrays.asList(
                        "§7Применить изменения",
                        "§7без перезапуска",
                        "",
                        "§eНажмите для перезагрузки"
                )
        ));

        // Информация
        int totalCatches = Plugininit.getFishingLogger().getTotalCatches();
        inv.setItem(49, createItem(Material.KNOWLEDGE_BOOK,
                "§6§lИнформация",
                Arrays.asList(
                        "§7Версия: §e1.0.0",
                        "§7Всего уловов: §e" + totalCatches,
                        "§7Активных игроков: §e" + Bukkit.getOnlinePlayers().size(),
                        "",
                        "§7Разработано для Spigot 1.21.4"
                )
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
