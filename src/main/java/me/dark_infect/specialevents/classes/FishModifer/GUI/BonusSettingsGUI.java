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

public class BonusSettingsGUI {

    private final JavaPlugin plugin;

    public BonusSettingsGUI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6§lНастройка бонусов");

        // Декоративные границы
        ItemStack border = createItem(Material.ORANGE_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
            inv.setItem(i + 45, border);
        }

        // Глобальный множитель
        double globalMult = plugin.getConfig().getDouble("bonus-chances.global-multiplier", 1.0);
        inv.setItem(4, createItem(Material.NETHER_STAR,
                "§e§lГлобальный множитель",
                Arrays.asList(
                        "§7Текущий: §e×" + String.format("%.2f", globalMult),
                        "",
                        "§7Влияет на ВСЕ бонусы сразу",
                        "§7Рекомендуется: §e0.5-1.0",
                        "",
                        "§7ЛКМ: §a+0.1",
                        "§7ПКМ: §c-0.1",
                        "§7Shift+ЛКМ: §a+0.5",
                        "§7Shift+ПКМ: §c-0.5"
                )
        ));

        // Бонусы реакции
        inv.setItem(11, createBonusItem("reaction.perfect", Material.DIAMOND,
                "§b§lИдеальная реакция", "< 0.5 секунды", 15));
        inv.setItem(12, createBonusItem("reaction.great", Material.EMERALD,
                "§a§lОтличная реакция", "< 0.7 секунды", 10));
        inv.setItem(13, createBonusItem("reaction.good", Material.GOLD_INGOT,
                "§e§lХорошая реакция", "< 1.0 секунды", 5));

        // Бонусы условий
        inv.setItem(20, createBonusItem("conditions.open-water", Material.WATER_BUCKET,
                "§9§lЧистая вода", "5×4×5 блоков", 8));
        inv.setItem(21, createBonusItem("conditions.night-time", Material.CLOCK,
                "§8§lНочное время", "13000-23000 тиков", 5));
        inv.setItem(22, createBonusItem("conditions.premium-biome", Material.GRASS_BLOCK,
                "§2§lПремиум биом", "Редкие биомы", 5));
        inv.setItem(23, createBonusItem("conditions.rain", Material.BLUE_DYE,
                "§3§lДождь", "Погодные условия", 3));

        // Бонусы навыков
        inv.setItem(29, createBonusItem("skill.apprentice", Material.IRON_INGOT,
                "§aПодмастерье", "100 уловов", 3));
        inv.setItem(30, createBonusItem("skill.expert", Material.DIAMOND,
                "§9Эксперт", "500 уловов", 8));
        inv.setItem(31, createBonusItem("skill.master", Material.NETHERITE_INGOT,
                "§5Мастер", "2000 уловов", 15));
        inv.setItem(32, createBonusItem("skill.legend", Material.NETHER_STAR,
                "§6Легенда", "5000 уловов", 25));

        // Бонусы цепочек
        inv.setItem(38, createBonusItem("streak.tier1", Material.IRON_BLOCK,
                "§7Цепочка 3+", "Малая серия", 5));
        inv.setItem(39, createBonusItem("streak.tier2", Material.GOLD_BLOCK,
                "§6Цепочка 5+", "Средняя серия", 10));
        inv.setItem(40, createBonusItem("streak.tier3", Material.DIAMOND_BLOCK,
                "§bЦепочка 10+", "Большая серия", 20));
        inv.setItem(41, createBonusItem("streak.tier4", Material.NETHERITE_BLOCK,
                "§5Цепочка 20+", "Мега серия", 35));

        // Удача моря
        inv.setItem(42, createBonusItem("luck-of-the-sea.per-level", Material.FISHING_ROD,
                "§bУдача моря", "За каждый уровень", 3));

        // Пресеты
        inv.setItem(47, createItem(Material.REDSTONE,
                "§c§lЛёгкий режим",
                Arrays.asList(
                        "§7Установить щедрые бонусы",
                        "§7для казуальной игры",
                        "",
                        "§7Множитель: §e×1.5",
                        "§eНажмите для применения"
                )
        ));

        inv.setItem(48, createItem(Material.IRON_INGOT,
                "§f§lБалансный режим",
                Arrays.asList(
                        "§7Стандартные настройки",
                        "§7для сбалансированной игры",
                        "",
                        "§7Множитель: §e×0.7",
                        "§eНажмите для применения"
                )
        ));

        inv.setItem(49, createItem(Material.OBSIDIAN,
                "§8§lСложный режим",
                Arrays.asList(
                        "§7Низкие бонусы",
                        "§7для хардкорной игры",
                        "",
                        "§7Множитель: §e×0.3",
                        "§eНажмите для применения"
                )
        ));

        // Сохранить
        inv.setItem(50, createItem(Material.WRITABLE_BOOK,
                "§a§lСохранить",
                Arrays.asList("§7Применить изменения")
        ));

        // Назад
        inv.setItem(53, createItem(Material.ARROW,
                "§cНазад",
                Arrays.asList("§7К настройкам")
        ));

        player.openInventory(inv);
    }

    private ItemStack createBonusItem(String configKey, Material material,
                                      String name, String condition, int defaultValue) {
        String fullPath = "bonus-chances." + configKey;
        double current = plugin.getConfig().getDouble(fullPath, defaultValue);

        return createItem(material, name, Arrays.asList(
                "§7Условие: §e" + condition,
                "§7Бонус: §e+" + String.format("%.1f%%", current),
                "",
                "§7ЛКМ: §a+1%",
                "§7ПКМ: §c-1%",
                "§7Shift+ЛКМ: §a+5%",
                "§7Shift+ПКМ: §c-5%"
        ));
    }

    public void applyPreset(Player player, String preset) {
        switch (preset) {
            case "easy":
                plugin.getConfig().set("bonus-chances.global-multiplier", 1.5);
                setAllBonuses(1.5);
                player.sendMessage("§a✓ Применён лёгкий режим!");
                break;

            case "balanced":
                plugin.getConfig().set("bonus-chances.global-multiplier", 0.7);
                resetToDefaults();
                player.sendMessage("§a✓ Применён балансный режим!");
                break;

            case "hard":
                plugin.getConfig().set("bonus-chances.global-multiplier", 0.3);
                setAllBonuses(0.5);
                player.sendMessage("§a✓ Применён сложный режим!");
                break;
        }

        plugin.saveConfig();
        open(player);
    }

    private void setAllBonuses(double multiplier) {
        // Реакция
        plugin.getConfig().set("bonus-chances.reaction.perfect", (int)(15 * multiplier));
        plugin.getConfig().set("bonus-chances.reaction.great", (int)(10 * multiplier));
        plugin.getConfig().set("bonus-chances.reaction.good", (int)(5 * multiplier));

        // Условия
        plugin.getConfig().set("bonus-chances.conditions.open-water", (int)(8 * multiplier));
        plugin.getConfig().set("bonus-chances.conditions.night-time", (int)(5 * multiplier));
        plugin.getConfig().set("bonus-chances.conditions.premium-biome", (int)(5 * multiplier));
        plugin.getConfig().set("bonus-chances.conditions.rain", (int)(3 * multiplier));

        // Навыки
        plugin.getConfig().set("bonus-chances.skill.apprentice", (int)(3 * multiplier));
        plugin.getConfig().set("bonus-chances.skill.expert", (int)(8 * multiplier));
        plugin.getConfig().set("bonus-chances.skill.master", (int)(15 * multiplier));
        plugin.getConfig().set("bonus-chances.skill.legend", (int)(25 * multiplier));

        // Цепочки
        plugin.getConfig().set("bonus-chances.streak.tier1", (int)(5 * multiplier));
        plugin.getConfig().set("bonus-chances.streak.tier2", (int)(10 * multiplier));
        plugin.getConfig().set("bonus-chances.streak.tier3", (int)(20 * multiplier));
        plugin.getConfig().set("bonus-chances.streak.tier4", (int)(35 * multiplier));

        // Удача моря
        plugin.getConfig().set("bonus-chances.luck-of-the-sea.per-level", (int)(3 * multiplier));
    }

    private void resetToDefaults() {
        // Стандартные балансные значения
        plugin.getConfig().set("bonus-chances.reaction.perfect", 15);
        plugin.getConfig().set("bonus-chances.reaction.great", 10);
        plugin.getConfig().set("bonus-chances.reaction.good", 5);

        plugin.getConfig().set("bonus-chances.conditions.open-water", 8);
        plugin.getConfig().set("bonus-chances.conditions.night-time", 5);
        plugin.getConfig().set("bonus-chances.conditions.premium-biome", 5);
        plugin.getConfig().set("bonus-chances.conditions.rain", 3);

        plugin.getConfig().set("bonus-chances.skill.apprentice", 3);
        plugin.getConfig().set("bonus-chances.skill.expert", 8);
        plugin.getConfig().set("bonus-chances.skill.master", 15);
        plugin.getConfig().set("bonus-chances.skill.legend", 25);

        plugin.getConfig().set("bonus-chances.streak.tier1", 5);
        plugin.getConfig().set("bonus-chances.streak.tier2", 10);
        plugin.getConfig().set("bonus-chances.streak.tier3", 20);
        plugin.getConfig().set("bonus-chances.streak.tier4", 35);

        plugin.getConfig().set("bonus-chances.luck-of-the-sea.per-level", 3);
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
