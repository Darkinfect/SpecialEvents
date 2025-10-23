package me.dark_infect.specialevents.classes.FishModifer.GUI;

import me.dark_infect.specialevents.classes.FishModifer.RarityTier;
import me.dark_infect.specialevents.utils.Plugininit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


public class GUIListener implements Listener {

    private final JavaPlugin plugin;

    public GUIListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    private void handleMainMenu(Player player, String itemName) {
        if (itemName.contains("Статистика выпадения")) {
            new StatisticsGUI(plugin).open(player);
        }
        else if (itemName.contains("Логи уловов")) {
            new LogsGUI(plugin).open(player, 0);
        }
        else if (itemName.contains("Настройки системы")) {
            new SettingsGUI(plugin).open(player);
        }
        else if (itemName.contains("Очистить логи")) {
            Plugininit.getFishingLogger().clearLogs();
            player.sendMessage("§a§l✓ §aЛоги успешно очищены!");
            player.closeInventory();
        }
        else if (itemName.contains("Перезагрузить конфиг")) {
            plugin.reloadConfig();
            player.sendMessage("§a§l✓ §aКонфиг перезагружен!");
        }else if (itemName.contains("Список игроков")) {
            new PlayerListGUI(plugin).open(player, 0, PlayerListGUI.SortType.TOTAL_CATCHES);
        }
        else if (itemName.contains("Тестирование")) {
            new TestingGUI(plugin).open(player);
        }
        else if (itemName.contains("Управление лутом")) {
            new LootManagementGUI(plugin).open(player);
        }
    }

    private void handleStatistics(Player player, String itemName) {
        if (itemName.contains("Назад")) {
            new AdminPanelGUI(plugin).openMainMenu(player);
        }
    }

    private void handleLogs(Player player, String itemName, String title) {
        LogsGUI logsGUI = new LogsGUI(plugin);

        if (itemName.contains("Следующая страница")) {
            int page = logsGUI.getCurrentPage() + 1;
            logsGUI.open(player, page);
        }
        else if (itemName.contains("Предыдущая страница")) {
            int page = Math.max(0, logsGUI.getCurrentPage() - 1);
            logsGUI.open(player, page);
        }
        else if (itemName.contains("Назад")) {
            new AdminPanelGUI(plugin).openMainMenu(player);
        }
    }

    private void handleSettings(Player player, String itemName, boolean isLeft, boolean isShift) {
        if (itemName.contains("Время ожидания")) {
            adjustWaitTime(isLeft, isShift);
            new SettingsGUI(plugin).open(player);
        }
        else if (itemName.contains("Окно реакции")) {
            adjustReactionWindow(isLeft);
            new SettingsGUI(plugin).open(player);
        }
        else if (itemName.contains("Режим отладки")) {
            boolean current = plugin.getConfig().getBoolean("debug", false);
            plugin.getConfig().set("debug", !current);
            new SettingsGUI(plugin).open(player);
        }
        else if (itemName.contains("Анти-АФК")) {
            boolean current = plugin.getConfig().getBoolean("fishing.anti-afk", true);
            plugin.getConfig().set("fishing.anti-afk", !current);
            new SettingsGUI(plugin).open(player);
        }
        else if (itemName.contains("Сохранить изменения")) {
            plugin.saveConfig();
            player.sendMessage("§a§l✓ §aНастройки сохранены!");
            player.closeInventory();
        }
        else if (itemName.contains("Назад")) {
            new AdminPanelGUI(plugin).openMainMenu(player);
        }else if (itemName.contains("Бонусы редкости")) {
            new BonusSettingsGUI(plugin).open(player);
        }
    }
    private void handleBonusSettings(Player player, String itemName, boolean isLeft, boolean isShift) {
        BonusSettingsGUI gui = new BonusSettingsGUI(plugin);

        if (itemName.contains("Лёгкий режим")) {
            gui.applyPreset(player, "easy");
        }
        else if (itemName.contains("Балансный режим")) {
            gui.applyPreset(player, "balanced");
        }
        else if (itemName.contains("Сложный режим")) {
            gui.applyPreset(player, "hard");
        }
        else if (itemName.contains("Глобальный множитель")) {
            adjustGlobalMultiplier(isLeft, isShift);
            gui.open(player);
        }
        else if (itemName.contains("Сохранить")) {
            plugin.saveConfig();
            player.sendMessage("§a✓ Бонусы сохранены!");
            player.closeInventory();
        }
        else if (itemName.contains("Назад")) {
            new SettingsGUI(plugin).open(player);
        }
        else {
            // Изменение конкретного бонуса
            adjustSpecificBonus(itemName, isLeft, isShift);
            gui.open(player);
        }
    }

    private void adjustGlobalMultiplier(boolean increase, boolean isShift) {
        double current = plugin.getConfig().getDouble("bonus-chances.global-multiplier", 1.0);
        double change = isShift ? 0.5 : 0.1;
        double newValue = increase ?
                Math.min(3.0, current + change) :
                Math.max(0.1, current - change);
        plugin.getConfig().set("bonus-chances.global-multiplier", newValue);
    }

    private void adjustSpecificBonus(String itemName, boolean isLeft, boolean isShift) {
        String configKey = getBonusConfigKey(itemName);
        if (configKey == null) return;

        double current = plugin.getConfig().getDouble(configKey, 0);
        double change = isShift ? 5 : 1;
        double newValue = isLeft ?
                Math.min(100, current + change) :
                Math.max(0, current - change);
        plugin.getConfig().set(configKey, newValue);
    }

    private String getBonusConfigKey(String itemName) {
        if (itemName.contains("Идеальная")) return "bonus-chances.reaction.perfect";
        if (itemName.contains("Отличная")) return "bonus-chances.reaction.great";
        if (itemName.contains("Хорошая")) return "bonus-chances.reaction.good";
        if (itemName.contains("Чистая вода")) return "bonus-chances.conditions.open-water";
        if (itemName.contains("Ночное")) return "bonus-chances.conditions.night-time";
        if (itemName.contains("Премиум биом")) return "bonus-chances.conditions.premium-biome";
        if (itemName.contains("Дождь")) return "bonus-chances.conditions.rain";
        if (itemName.contains("Подмастерье")) return "bonus-chances.skill.apprentice";
        if (itemName.contains("Эксперт")) return "bonus-chances.skill.expert";
        if (itemName.contains("Мастер")) return "bonus-chances.skill.master";
        if (itemName.contains("Легенда")) return "bonus-chances.skill.legend";
        if (itemName.contains("3+")) return "bonus-chances.streak.tier1";
        if (itemName.contains("5+")) return "bonus-chances.streak.tier2";
        if (itemName.contains("10+")) return "bonus-chances.streak.tier3";
        if (itemName.contains("20+")) return "bonus-chances.streak.tier4";
        if (itemName.contains("Удача моря")) return "bonus-chances.luck-of-the-sea.per-level";
        return null;
    }

    private void adjustWaitTime(boolean isLeft, boolean isShift) {
        String key = isShift ? "fishing.max-wait-time" : "fishing.min-wait-time";
        int current = plugin.getConfig().getInt(key, 60);
        int newValue = isLeft ? current + 10 : Math.max(20, current - 10);
        plugin.getConfig().set(key, newValue);
    }

    private void adjustReactionWindow(boolean isLeft) {
        int current = plugin.getConfig().getInt("fishing.base-reaction-window", 20);
        int newValue = isLeft ? current + 5 : Math.max(5, current - 5);
        plugin.getConfig().set("fishing.base-reaction-window", newValue);
    }
    private void handlePlayerList(Player player, String itemName, String title, boolean isLeft, boolean isShift) {
        PlayerListGUI gui = new PlayerListGUI(plugin);

        if (itemName.contains("Сортировка")) {
            // Переключить режим сортировки
            PlayerListGUI.SortType currentSort = extractSortType(title);
            PlayerListGUI.SortType newSort = currentSort.next();
            gui.open(player, 0, newSort);
        }
        else if (itemName.contains("Следующая")) {
            int page = extractPage(title);
            PlayerListGUI.SortType sort = extractSortType(title);
            gui.open(player, page, sort);
        }
        else if (itemName.contains("Предыдущая")) {
            int page = Math.max(0, extractPage(title) - 2);
            PlayerListGUI.SortType sort = extractSortType(title);
            gui.open(player, page, sort);
        }
        else if (itemName.contains("Назад")) {
            new AdminPanelGUI(plugin).openMainMenu(player);
        }
    }

    private void handleTesting(Player player, String itemName, boolean isLeft, boolean isShift) {
        TestingGUI testGUI = new TestingGUI(plugin);

        if (itemName.contains("Симуляция уловов")) {
            testGUI.runSimulation(player, testGUI.simulationCount);
        }
        else if (itemName.contains("Количество симуляций")) {
            testGUI.adjustSimulationCount(isLeft, isShift);
            testGUI.open(player);
        }
        else if (itemName.contains("Тест времени реакции")) {
            player.closeInventory();
            testGUI.testReactionTime(player);
        }
        else if (itemName.contains("тестовые предметы")) {
            testGUI.giveTestItems(player);
        }
        else if (itemName.contains("производительности")) {
            player.closeInventory();
            testGUI.testPerformance(player);
        }
        else if (itemName.contains("Очистить результаты")) {
            testGUI.clearResults();
            testGUI.open(player);
        }
        else if (itemName.contains("Назад")) {
            new AdminPanelGUI(plugin).openMainMenu(player);
        }
    }

    private void handleLootManagement(Player player, String itemName, boolean isLeft, boolean isShift) {
        if (itemName.contains("Сохранить")) {
            plugin.saveConfig();
            player.sendMessage("§a✓ Настройки лута сохранены!");
            player.closeInventory();
        }
        else if (itemName.contains("Биомный лут")) {
            boolean current = plugin.getConfig().getBoolean("loot.biome-specific", true);
            plugin.getConfig().set("loot.biome-specific", !current);
            new LootManagementGUI(plugin).open(player);
        }
        else if (itemName.contains("Назад")) {
            new AdminPanelGUI(plugin).openMainMenu(player);
        }
        // Обработка изменения шансов категорий и редкостей
        else if (itemName.contains("Категория:")) {
            adjustCategoryChance(itemName, isLeft);
            new LootManagementGUI(plugin).open(player);
        }
        else if (isRarityItem(itemName)) {
            adjustRaritySettings(itemName, isLeft, isShift);
            new LootManagementGUI(plugin).open(player);
        }
    }

    private void adjustCategoryChance(String categoryName, boolean increase) {
        String configKey = null;

        if (categoryName.contains("Рыба")) configKey = "loot.fish.chance";
        else if (categoryName.contains("Ресурсы")) configKey = "loot.resources.chance";
        else if (categoryName.contains("Снаряжение")) configKey = "loot.equipment.chance";
        else if (categoryName.contains("Сокровища")) configKey = "loot.treasure.chance";

        if (configKey != null) {
            double current = plugin.getConfig().getDouble(configKey, 0);
            double newValue = increase ?
                    Math.min(100, current + 5) :
                    Math.max(0, current - 5);
            plugin.getConfig().set(configKey, newValue);
        }
    }

    private void adjustRaritySettings(String rarityName, boolean isLeft, boolean isShift) {
        RarityTier tier = getRarityFromName(rarityName);
        if (tier == null) return;

        String tierKey = "rarity." + tier.name().toLowerCase();

        if (isShift) {
            // Изменение количества предметов в контейнере
            int current = plugin.getConfig().getInt(tierKey + ".items-in-container", tier.getItemCount());
            int newValue = isLeft ? current + 1 : Math.max(1, current - 1);
            plugin.getConfig().set(tierKey + ".items-in-container", newValue);
        } else {
            // Изменение базового шанса
            double current = plugin.getConfig().getDouble(tierKey + ".base-chance", tier.getBaseChance());
            double newValue = isLeft ?
                    Math.min(100, current + 1) :
                    Math.max(0.1, current - 1);
            plugin.getConfig().set(tierKey + ".base-chance", newValue);
        }
    }

    private boolean isRarityItem(String name) {
        return name.contains("Обычный") || name.contains("Необычный") ||
                name.contains("Редкий") || name.contains("Эпический") ||
                name.contains("Легендарный");
    }

    private RarityTier getRarityFromName(String name) {
        if (name.contains("Обычный")) return RarityTier.COMMON;
        if (name.contains("Необычный")) return RarityTier.UNCOMMON;
        if (name.contains("Редкий")) return RarityTier.RARE;
        if (name.contains("Эпический")) return RarityTier.EPIC;
        if (name.contains("Легендарный")) return RarityTier.LEGENDARY;
        return null;
    }

    private int extractPage(String title) {
        try {
            String pageStr = title.substring(title.indexOf("Стр. ") + 5);
            pageStr = pageStr.substring(0, pageStr.indexOf("/"));
            return Integer.parseInt(pageStr);
        } catch (Exception e) {
            return 1;
        }
    }

    private PlayerListGUI.SortType extractSortType(String title) {
        // По умолчанию сортировка по уловам
        return PlayerListGUI.SortType.TOTAL_CATCHES;
    }

    // Обновите метод onInventoryClick
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || !clicked.hasItemMeta()) return;

        String itemName = clicked.getItemMeta().getDisplayName();
        boolean isLeft = event.isLeftClick();
        boolean isShift = event.isShiftClick();

        // Главное меню
        if (title.contains("Админ-панель рыбалки")) {
            event.setCancelled(true);
            handleMainMenu(player, itemName);
        }
        // Статистика
        else if (title.contains("Статистика выпадения")) {
            event.setCancelled(true);
            handleStatistics(player, itemName);
        }
        // Логи
        else if (title.contains("Логи уловов")) {
            event.setCancelled(true);
            handleLogs(player, itemName, title);
        }
        // Настройки
        else if (title.contains("Настройки системы")) {
            event.setCancelled(true);
            handleSettings(player, itemName, isLeft, isShift);
        }
        // Список игроков
        else if (title.contains("Игроки")) {
            event.setCancelled(true);
            handlePlayerList(player, itemName, title, isLeft, isShift);
        }
        // Тестирование
        else if (title.contains("Тестирование системы")) {
            event.setCancelled(true);
            handleTesting(player, itemName, isLeft, isShift);
        }
        // Управление лутом
        else if (title.contains("Управление лутом")) {
            event.setCancelled(true);
            handleLootManagement(player, itemName, isLeft, isShift);
        }else if (title.contains("Настройка бонусов")) {
            event.setCancelled(true);
            handleBonusSettings(player, itemName, isLeft, isShift);
        }
    }
}

