package me.dark_infect.specialevents.classes.FishModifer.GUI;

import me.dark_infect.specialevents.classes.FishModifer.RarityTier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class LootManagementGUI {

    private final JavaPlugin plugin;

    public LootManagementGUI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§5§lУправление лутом");

        // Декоративные границы
        ItemStack border = createItem(Material.PURPLE_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
            inv.setItem(i + 45, border);
        }

        // Настройка редкостей
        inv.setItem(11, createRarityConfig(RarityTier.COMMON));
        inv.setItem(13, createRarityConfig(RarityTier.UNCOMMON));
        inv.setItem(15, createRarityConfig(RarityTier.RARE));
        inv.setItem(20, createRarityConfig(RarityTier.EPIC));
        inv.setItem(24, createRarityConfig(RarityTier.LEGENDARY));

        // Категории лута
        inv.setItem(29, createItem(Material.COD,
                "§b§lКатегория: Рыба",
                Arrays.asList(
                        "§7Шанс: §e" + plugin.getConfig().getDouble("loot.fish.chance", 40.0) + "%",
                        "",
                        "§7ЛКМ: §a+5%",
                        "§7ПКМ: §c-5%"
                )
        ));

        inv.setItem(31, createItem(Material.DIAMOND,
                "§9§lКатегория: Ресурсы",
                Arrays.asList(
                        "§7Шанс: §e" + plugin.getConfig().getDouble("loot.resources.chance", 30.0) + "%",
                        "",
                        "§7ЛКМ: §a+5%",
                        "§7ПКМ: §c-5%"
                )
        ));

        inv.setItem(33, createItem(Material.IRON_SWORD,
                "§e§lКатегория: Снаряжение",
                Arrays.asList(
                        "§7Шанс: §e" + plugin.getConfig().getDouble("loot.equipment.chance", 20.0) + "%",
                        "",
                        "§7ЛКМ: §a+5%",
                        "§7ПКМ: §c-5%"
                )
        ));

        inv.setItem(35, createItem(Material.GOLD_INGOT,
                "§6§lКатегория: Сокровища",
                Arrays.asList(
                        "§7Шанс: §e" + plugin.getConfig().getDouble("loot.treasure.chance", 10.0) + "%",
                        "",
                        "§7ЛКМ: §a+5%",
                        "§7ПКМ: §c-5%"
                )
        ));

        // Биомный лут
        boolean biomeEnabled = plugin.getConfig().getBoolean("loot.biome-specific", true);
        inv.setItem(38, createItem(
                biomeEnabled ? Material.GRASS_BLOCK : Material.DIRT,
                "§2§lБиомный лут",
                Arrays.asList(
                        "§7Состояние: " + (biomeEnabled ? "§aВКЛ" : "§cВЫКЛ"),
                        "",
                        "§7Уникальные предметы",
                        "§7в зависимости от биома",
                        "",
                        "§eНажмите для переключения"
                )
        ));

        // Контейнеры
        int containerChance = plugin.getConfig().getInt("loot.container-chance", 10);
        inv.setItem(40, createItem(Material.CHEST,
                "§d§lШанс контейнеров",
                Arrays.asList(
                        "§7Шанс: §e" + containerChance + "%",
                        "",
                        "§7Для редкого+ лута",
                        "",
                        "§7ЛКМ: §a+1%",
                        "§7ПКМ: §c-1%",
                        "§7Shift+ЛКМ: §a+5%",
                        "§7Shift+ПКМ: §c-5%"
                )
        ));

        // Множитель количества
        double amountMultiplier = plugin.getConfig().getDouble("loot.amount-multiplier", 1.0);
        inv.setItem(42, createItem(Material.HOPPER,
                "§a§lМножитель количества",
                Arrays.asList(
                        "§7Множитель: §ex" + amountMultiplier,
                        "",
                        "§7Влияет на количество",
                        "§7выпадающих предметов",
                        "",
                        "§7ЛКМ: §a+0.1",
                        "§7ПКМ: §c-0.1"
                )
        ));

        // Сохранить
        inv.setItem(48, createItem(Material.WRITABLE_BOOK,
                "§a§lСохранить изменения",
                Arrays.asList(
                        "§7Применить все изменения"
                )
        ));

        // Назад
        inv.setItem(49, createItem(Material.ARROW,
                "§cНазад",
                Arrays.asList("§7Вернуться в главное меню")
        ));

        player.openInventory(inv);
    }

    private ItemStack createRarityConfig(RarityTier tier) {
        double baseChance = plugin.getConfig().getDouble(
                "rarity." + tier.name().toLowerCase() + ".base-chance",
                tier.getBaseChance()
        );

        int itemCount = plugin.getConfig().getInt(
                "rarity." + tier.name().toLowerCase() + ".items-in-container",
                tier.getItemCount()
        );

        Material material;
        switch (tier) {
            case COMMON: material = Material.GRAY_CONCRETE; break;
            case UNCOMMON: material = Material.LIME_CONCRETE; break;
            case RARE: material = Material.BLUE_CONCRETE; break;
            case EPIC: material = Material.PURPLE_CONCRETE; break;
            case LEGENDARY: material = Material.GOLD_BLOCK; break;
            default: material = Material.WHITE_CONCRETE;
        }

        return createItem(material,
                tier.getFormattedName(),
                Arrays.asList(
                        "§7━━━━━━━━━━━━━━━━━━━",
                        "§7Базовый шанс: §e" + String.format("%.1f%%", baseChance),
                        "§7Предметов в контейнере: §e" + itemCount,
                        "",
                        "§7ЛКМ: §a+1% к шансу",
                        "§7ПКМ: §c-1% к шансу",
                        "§7Shift+ЛКМ: §a+1 предмет",
                        "§7Shift+ПКМ: §c-1 предмет",
                        "§7━━━━━━━━━━━━━━━━━━━"
                )
        );
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
