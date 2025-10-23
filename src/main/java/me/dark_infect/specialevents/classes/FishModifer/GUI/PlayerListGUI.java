package me.dark_infect.specialevents.classes.FishModifer.GUI;


import me.dark_infect.specialevents.classes.FishModifer.FishingSkill;
import me.dark_infect.specialevents.utils.Plugininit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;


public class PlayerListGUI {

    private final JavaPlugin plugin;
    private int currentPage = 0;
    private static final int PLAYERS_PER_PAGE = 28;
    private SortType sortType = SortType.TOTAL_CATCHES;

    public PlayerListGUI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, int page, SortType sort) {
        this.currentPage = page;
        this.sortType = sort;

        // Получаем всех игроков с данными
        List<PlayerData> playerDataList = getAllPlayersData();

        // Сортировка
        sortPlayerData(playerDataList, sort);

        int totalPages = (int) Math.ceil((double) playerDataList.size() / PLAYERS_PER_PAGE);

        Inventory inv = Bukkit.createInventory(null, 54,
                "§a§lИгроки §7(Стр. " + (page + 1) + "/" + Math.max(1, totalPages) + ")");

        // Декоративные границы
        ItemStack border = createItem(Material.GREEN_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
            inv.setItem(i + 45, border);
        }

        // Отображение игроков
        int start = page * PLAYERS_PER_PAGE;
        int end = Math.min(start + PLAYERS_PER_PAGE, playerDataList.size());

        for (int i = start; i < end; i++) {
            PlayerData data = playerDataList.get(i);
            int slot = 10 + ((i - start) % 7) + ((i - start) / 7) * 9;

            if (slot >= 10 && slot < 44) {
                inv.setItem(slot, createPlayerHead(data, i + 1));
            }
        }

        // Сортировка
        inv.setItem(46, createItem(Material.COMPARATOR,
                "§e§lСортировка: " + sort.getDisplayName(),
                Arrays.asList(
                        "§7ЛКМ: Следующий режим",
                        "§7Текущий: " + sort.getDisplayName(),
                        "",
                        "§7Доступные режимы:",
                        "§7• По уловам",
                        "§7• По успешности",
                        "§7• По легендарным",
                        "§7• По навыку"
                )
        ));

        // Поиск
        inv.setItem(47, createItem(Material.SPYGLASS,
                "§b§lПоиск игрока",
                Arrays.asList(
                        "§7Введите имя в чат",
                        "§c§oСкоро..."
                )
        ));

        // Топ-3 игрока
        if (playerDataList.size() >= 3) {
            inv.setItem(4, createTopPlayerItem(playerDataList.get(0), 1));
            if (playerDataList.size() >= 2) {
                inv.setItem(3, createTopPlayerItem(playerDataList.get(1), 2));
            }
            if (playerDataList.size() >= 3) {
                inv.setItem(5, createTopPlayerItem(playerDataList.get(2), 3));
            }
        }

        // Навигация
        if (page > 0) {
            inv.setItem(48, createItem(Material.ARROW,
                    "§aПредыдущая страница",
                    Arrays.asList("§7Страница " + page)
            ));
        }

        if (page < totalPages - 1) {
            inv.setItem(50, createItem(Material.ARROW,
                    "§aСледующая страница",
                    Arrays.asList("§7Страница " + (page + 2))
            ));
        }

        // Назад
        inv.setItem(49, createItem(Material.BARRIER,
                "§cНазад",
                Arrays.asList("§7Вернуться в главное меню")
        ));

        player.openInventory(inv);
    }

    private List<PlayerData> getAllPlayersData() {
        List<PlayerData> dataList = new ArrayList<>();

        // Проходим по всем игрокам, которые когда-либо играли
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            Player player = (Player) offlinePlayer;
            int totalCatches = Plugininit.getDataManager().getTotalCatches(player);

            // Пропускаем игроков без статистики
            if (totalCatches == 0) continue;

            int successfulCatches = Plugininit.getDataManager().getSuccessfulCatches(player);
            int legendaryCount = Plugininit.getDataManager().getLegendaryCount(player);
            int bestStreak = Plugininit.getDataManager().getBestStreak(player);
            double successRate = Plugininit.getDataManager().getSuccessRate(player);
            FishingSkill skill = Plugininit.getDataManager().getSkillLevel(player);

            PlayerData data = new PlayerData(
                    offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown",
                    offlinePlayer.getUniqueId(),
                    totalCatches,
                    successfulCatches,
                    legendaryCount,
                    bestStreak,
                    successRate,
                    skill,
                    offlinePlayer.isOnline()
            );

            dataList.add(data);
        }

        return dataList;
    }

    private void sortPlayerData(List<PlayerData> dataList, SortType sortType) {
        switch (sortType) {
            case TOTAL_CATCHES:
                dataList.sort((a, b) -> Integer.compare(b.totalCatches, a.totalCatches));
                break;
            case SUCCESS_RATE:
                dataList.sort((a, b) -> Double.compare(b.successRate, a.successRate));
                break;
            case LEGENDARY_COUNT:
                dataList.sort((a, b) -> Integer.compare(b.legendaryCount, a.legendaryCount));
                break;
            case SKILL_LEVEL:
                dataList.sort((a, b) -> Integer.compare(b.skill.ordinal(), a.skill.ordinal()));
                break;
        }
    }

    private ItemStack createPlayerHead(PlayerData data, int rank) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (meta != null) {
            // Устанавливаем владельца головы
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(data.uuid);
            meta.setOwningPlayer(offlinePlayer);

            String statusColor = data.isOnline ? "§a" : "§7";
            meta.setDisplayName(statusColor + "§l" + data.playerName);

            List<String> lore = new ArrayList<>();
            lore.add("§7━━━━━━━━━━━━━━━━━━━");
            lore.add("§7Место: §e#" + rank);
            lore.add("§7Статус: " + (data.isOnline ? "§aОнлайн" : "§7Оффлайн"));
            lore.add("");
            lore.add("§7Навык: " + data.skill.getFormattedName());
            lore.add("§7Всего уловов: §e" + data.totalCatches);
            lore.add("§7Успешных: §a" + data.successfulCatches);
            lore.add("§7Успешность: §e" + String.format("%.1f%%", data.successRate));
            lore.add("");
            lore.add("§7Легендарных: §6§l" + data.legendaryCount);
            lore.add("§7Лучшая серия: §d" + data.bestStreak);
            lore.add("§7━━━━━━━━━━━━━━━━━━━");
            lore.add("");
            lore.add("§eНажмите для подробностей");

            meta.setLore(lore);
            skull.setItemMeta(meta);
        }

        return skull;
    }

    private ItemStack createTopPlayerItem(PlayerData data, int place) {
        Material material;
        String prefix;

        switch (place) {
            case 1:
                material = Material.GOLD_BLOCK;
                prefix = "§6§l🥇 ";
                break;
            case 2:
                material = Material.IRON_BLOCK;
                prefix = "§7§l🥈 ";
                break;
            case 3:
                material = Material.COPPER_BLOCK;
                prefix = "§c§l🥉 ";
                break;
            default:
                material = Material.STONE;
                prefix = "§f";
        }

        return createItem(material,
                prefix + data.playerName,
                Arrays.asList(
                        "§7" + sortType.getDisplayName() + ": §e" + sortType.getValue(data),
                        "§7Навык: " + data.skill.getFormattedName()
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

    public int getCurrentPage() {
        return currentPage;
    }

    public SortType getSortType() {
        return sortType;
    }

    // Внутренний класс для хранения данных игрока
    public static class PlayerData {
        String playerName;
        UUID uuid;
        int totalCatches;
        int successfulCatches;
        int legendaryCount;
        int bestStreak;
        double successRate;
        FishingSkill skill;
        boolean isOnline;

        public PlayerData(String playerName, UUID uuid, int totalCatches,
                          int successfulCatches, int legendaryCount, int bestStreak,
                          double successRate, FishingSkill skill, boolean isOnline) {
            this.playerName = playerName;
            this.uuid = uuid;
            this.totalCatches = totalCatches;
            this.successfulCatches = successfulCatches;
            this.legendaryCount = legendaryCount;
            this.bestStreak = bestStreak;
            this.successRate = successRate;
            this.skill = skill;
            this.isOnline = isOnline;
        }
    }

    // Enum для типов сортировки
    public enum SortType {
        TOTAL_CATCHES("По уловам"),
        SUCCESS_RATE("По успешности"),
        LEGENDARY_COUNT("По легендарным"),
        SKILL_LEVEL("По навыку");

        private final String displayName;

        SortType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getValue(PlayerData data) {
            switch (this) {
                case TOTAL_CATCHES: return String.valueOf(data.totalCatches);
                case SUCCESS_RATE: return String.format("%.1f%%", data.successRate);
                case LEGENDARY_COUNT: return String.valueOf(data.legendaryCount);
                case SKILL_LEVEL: return data.skill.getDisplayName();
                default: return "N/A";
            }
        }

        public SortType next() {
            int nextIndex = (this.ordinal() + 1) % values().length;
            return values()[nextIndex];
        }
    }
}

