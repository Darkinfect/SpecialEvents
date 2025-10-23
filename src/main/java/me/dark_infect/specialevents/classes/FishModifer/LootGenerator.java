package me.dark_infect.specialevents.classes.FishModifer;
import me.dark_infect.specialevents.utils.Plugininit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LootGenerator {

    private final JavaPlugin plugin;

    public LootGenerator(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public RarityTier calculateRarity(Player player, int reactionTicks,
                                      boolean openWater, Biome biome, boolean isNight) {
        double bonusChance = 0;

        // Получаем множитель из конфига
        double globalMultiplier = plugin.getConfig().getDouble("bonus-chances.global-multiplier", 1.0);

        // Бонус за реакцию
        if (reactionTicks <= 10) { // < 0.5 сек
            bonusChance += plugin.getConfig().getDouble("bonus-chances.reaction.perfect", 15);
        } else if (reactionTicks <= 14) { // < 0.7 сек
            bonusChance += plugin.getConfig().getDouble("bonus-chances.reaction.great", 10);
        } else if (reactionTicks <= 20) { // < 1 сек
            bonusChance += plugin.getConfig().getDouble("bonus-chances.reaction.good", 5);
        }

        // Бонусы условий
        if (openWater) {
            bonusChance += plugin.getConfig().getDouble("bonus-chances.conditions.open-water", 8);
        }

        if (isNight) {
            bonusChance += plugin.getConfig().getDouble("bonus-chances.conditions.night-time", 5);
        }

        // Проверка биома на null перед использованием
        if (biome != null && isPremiumBiome(biome)) {
            bonusChance += plugin.getConfig().getDouble("bonus-chances.conditions.premium-biome", 5);
        }

        // Бонус за дождь
        if (player.getWorld().hasStorm()) {
            bonusChance += plugin.getConfig().getDouble("bonus-chances.conditions.rain", 3);
        }

        // Бонус навыка
        FishingSkill skill = Plugininit.getDataManager().getSkillLevel(player);
        bonusChance += getSkillBonus(skill);

        // Бонус цепочки
        int streak = Plugininit.getDataManager().getCurrentStreak(player);
        bonusChance += getStreakBonus(streak);

        // Зачарование "Удача моря"
        ItemStack rod = player.getInventory().getItemInMainHand();
        if (rod != null && rod.getType() == Material.FISHING_ROD) {
            int luckLevel = rod.getEnchantmentLevel(Enchantment.LUCK_OF_THE_SEA);
            double luckBonus = plugin.getConfig().getDouble(
                    "bonus-chances.luck-of-the-sea.per-level", 3);
            bonusChance += luckLevel * luckBonus;
        }

        // Применяем глобальный множитель
        bonusChance *= globalMultiplier;

        // Рассчёт финальной редкости
        double roll = ThreadLocalRandom.current().nextDouble(100);
        double adjustedRoll = roll - bonusChance;

        // Логирование для отладки
        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info(String.format(
                    "[DEBUG] Player: %s | Roll: %.2f | Bonus: %.2f | Adjusted: %.2f",
                    player.getName(), roll, bonusChance, adjustedRoll
            ));
        }

        if (adjustedRoll < 1) return RarityTier.LEGENDARY;
        if (adjustedRoll < 5) return RarityTier.EPIC;
        if (adjustedRoll < 15) return RarityTier.RARE;
        if (adjustedRoll < 40) return RarityTier.UNCOMMON;
        return RarityTier.COMMON;
    }

    // Новый метод для получения бонуса от навыка
    private double getSkillBonus(FishingSkill skill) {
        String configPath = "bonus-chances.skill.";

        switch (skill) {
            case NOVICE:
                return plugin.getConfig().getDouble(configPath + "novice", 0);
            case APPRENTICE:
                return plugin.getConfig().getDouble(configPath + "apprentice", 3);
            case EXPERT:
                return plugin.getConfig().getDouble(configPath + "expert", 8);
            case MASTER:
                return plugin.getConfig().getDouble(configPath + "master", 15);
            case LEGEND:
                return plugin.getConfig().getDouble(configPath + "legend", 25);
            default:
                return 0;
        }
    }

    // Новый метод для получения бонуса от цепочки
    private double getStreakBonus(int streak) {
        if (streak >= 20) {
            return plugin.getConfig().getDouble("bonus-chances.streak.tier4", 35);
        } else if (streak >= 10) {
            return plugin.getConfig().getDouble("bonus-chances.streak.tier3", 20);
        } else if (streak >= 5) {
            return plugin.getConfig().getDouble("bonus-chances.streak.tier2", 10);
        } else if (streak >= 3) {
            return plugin.getConfig().getDouble("bonus-chances.streak.tier1", 5);
        }
        return 0;
    }

    public ItemStack generateLoot(Player player, RarityTier tier, Biome biome) {
        // 10% шанс на контейнер для редких+ уровней
        if (tier.ordinal() >= RarityTier.RARE.ordinal()) {
            if (ThreadLocalRandom.current().nextDouble() < 0.1) {
                return Plugininit.getContainerManager().createLootContainer(tier);
            }
        }

        // Специальный лут биома (только если биом не null)
        if (biome != null) {
            ItemStack biomeLoot = getBiomeLoot(biome, tier);
            if (biomeLoot != null) {
                return biomeLoot;
            }
        }

        // Стандартный лут по категориям
        double categoryRoll = ThreadLocalRandom.current().nextDouble(100);

        if (categoryRoll < 40) {
            return generateFish(tier);
        } else if (categoryRoll < 70) {
            return generateResource(tier);
        } else if (categoryRoll < 90) {
            return generateEquipment(tier);
        } else {
            return generateTreasure(tier);
        }
    }
    public ItemStack generateTestLoot(Player player, RarityTier tier) {
        return generateLoot(player, tier, null);
    }
    private ItemStack generateFish(RarityTier tier) {
        Material[] commonFish = {Material.COD, Material.SALMON,
                Material.TROPICAL_FISH, Material.PUFFERFISH};

        Material fishType = commonFish[ThreadLocalRandom.current()
                .nextInt(commonFish.length)];

        int amount = tier.ordinal() + 1;
        ItemStack fish = new ItemStack(fishType, amount);

        ItemMeta meta = fish.getItemMeta();
        meta.setDisplayName(tier.getColorCode() + tier.getDisplayName() + " " +
                getItemName(fishType));
        meta.setLore(Arrays.asList(
                "§7Редкость: " + tier.getFormattedName(),
                "§7Свежевыловленная рыба!"
        ));
        fish.setItemMeta(meta);

        return fish;
    }

    private ItemStack generateResource(RarityTier tier) {
        Material resource;
        int amount;

        switch (tier) {
            case LEGENDARY:
                resource = ThreadLocalRandom.current().nextBoolean() ?
                        Material.NETHERITE_SCRAP : Material.ECHO_SHARD;
                amount = ThreadLocalRandom.current().nextInt(1, 3);
                break;
            case EPIC:
                resource = ThreadLocalRandom.current().nextBoolean() ?
                        Material.DIAMOND : Material.EMERALD;
                amount = ThreadLocalRandom.current().nextInt(2, 5);
                break;
            case RARE:
                Material[] rareRes = {Material.GOLD_INGOT, Material.REDSTONE,
                        Material.LAPIS_LAZULI};
                resource = rareRes[ThreadLocalRandom.current().nextInt(rareRes.length)];
                amount = ThreadLocalRandom.current().nextInt(4, 9);
                break;
            case UNCOMMON:
                resource = Material.IRON_INGOT;
                amount = ThreadLocalRandom.current().nextInt(3, 7);
                break;
            default:
                Material[] commonRes = {Material.COAL, Material.LEATHER, Material.KELP};
                resource = commonRes[ThreadLocalRandom.current().nextInt(commonRes.length)];
                amount = ThreadLocalRandom.current().nextInt(2, 5);
        }

        ItemStack item = new ItemStack(resource, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setLore(Arrays.asList(
                    "§7Редкость: " + tier.getFormattedName(),
                    "§7Выловлено из водных глубин"
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack generateEquipment(RarityTier tier) {
        Material[] equipmentTypes = {
                Material.IRON_SWORD, Material.IRON_HELMET,
                Material.IRON_CHESTPLATE, Material.BOW
        };

        Material type = equipmentTypes[ThreadLocalRandom.current()
                .nextInt(equipmentTypes.length)];
        ItemStack equipment = new ItemStack(type);

        // Добавление зачарований по редкости
        int enchantCount = tier.ordinal();
        Enchantment[] possibleEnchants = getEnchantmentsForItem(type);

        for (int i = 0; i < Math.min(enchantCount, possibleEnchants.length); i++) {
            Enchantment ench = possibleEnchants[ThreadLocalRandom.current()
                    .nextInt(possibleEnchants.length)];
            int level = Math.min(tier.ordinal() + 1, ench.getMaxLevel());
            equipment.addUnsafeEnchantment(ench, level);
        }

        ItemMeta meta = equipment.getItemMeta();
        meta.setDisplayName(tier.getFormattedName() + " " + getItemName(type));
        meta.setLore(Arrays.asList(
                "§7Редкость: " + tier.getFormattedName(),
                "§7Таинственное снаряжение из глубин"
        ));
        equipment.setItemMeta(meta);

        return equipment;
    }

    private ItemStack generateTreasure(RarityTier tier) {
        Material[] treasures = {
                Material.NAME_TAG, Material.SADDLE, Material.ENCHANTED_BOOK,
                Material.HEART_OF_THE_SEA, Material.NAUTILUS_SHELL
        };

        Material treasure = treasures[Math.min(tier.ordinal(), treasures.length - 1)];
        ItemStack item = new ItemStack(treasure);

        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(
                "§7Редкость: " + tier.getFormattedName(),
                "§7Редкое сокровище!"
        ));
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack getBiomeLoot(Biome biome, RarityTier tier) {
        // ВАЖНО: Проверка на null
        if (biome == null) {
            return null;
        }

        switch (biome) {
            case WARM_OCEAN:
            case LUKEWARM_OCEAN:
                if (tier == RarityTier.LEGENDARY) {
                    return new ItemStack(Material.HEART_OF_THE_SEA);
                }
                return new ItemStack(Material.NAUTILUS_SHELL, tier.ordinal() + 1);

            case FROZEN_OCEAN:
            case DEEP_FROZEN_OCEAN:
                if (tier == RarityTier.LEGENDARY) {
                    return new ItemStack(Material.BLUE_ICE,
                            ThreadLocalRandom.current().nextInt(4, 9));
                }
                return new ItemStack(Material.PACKED_ICE, tier.ordinal() + 2);

            case MUSHROOM_FIELDS:
                if (tier.ordinal() >= RarityTier.EPIC.ordinal()) {
                    return new ItemStack(Material.MYCELIUM, 16);
                }
                return new ItemStack(Material.RED_MUSHROOM_BLOCK);

            case SWAMP:
            case MANGROVE_SWAMP:
                if (tier.ordinal() >= RarityTier.RARE.ordinal()) {
                    return new ItemStack(Material.SLIME_BALL,
                            ThreadLocalRandom.current().nextInt(4, 9));
                }
                return new ItemStack(Material.LILY_PAD, tier.ordinal() + 3);

            default:
                return null;
        }
    }

    private boolean isPremiumBiome(Biome biome) {
        if (biome == null) {
            return false;
        }

        return biome == Biome.WARM_OCEAN ||
                biome == Biome.DEEP_OCEAN ||
                biome == Biome.MUSHROOM_FIELDS ||
                biome == Biome.FROZEN_OCEAN;
    }

    private Enchantment[] getEnchantmentsForItem(Material type) {
        if (type.toString().contains("SWORD")) {
            return new Enchantment[]{
                    Enchantment.SHARPNESS, Enchantment.LOOTING,
                    Enchantment.FIRE_ASPECT, Enchantment.KNOCKBACK
            };
        } else if (type.toString().contains("BOW")) {
            return new Enchantment[]{
                    Enchantment.POWER, Enchantment.PUNCH,
                    Enchantment.FLAME, Enchantment.INFINITY
            };
        } else {
            return new Enchantment[]{
                    Enchantment.PROTECTION, Enchantment.UNBREAKING,
                    Enchantment.THORNS, Enchantment.MENDING
            };
        }
    }
    public double calculateTotalChance(Player player, int reactionTicks,
                                       boolean openWater, String biomeName, boolean isNight) {
        double bonusChance = 0;
        double globalMultiplier = plugin.getConfig().getDouble("bonus-chances.global-multiplier", 1.0);

        // Бонус за реакцию
        if (reactionTicks <= 10) {
            bonusChance += plugin.getConfig().getDouble("bonus-chances.reaction.perfect", 15);
        } else if (reactionTicks <= 14) {
            bonusChance += plugin.getConfig().getDouble("bonus-chances.reaction.great", 10);
        } else if (reactionTicks <= 20) {
            bonusChance += plugin.getConfig().getDouble("bonus-chances.reaction.good", 5);
        }

        // Бонусы условий
        if (openWater) {
            bonusChance += plugin.getConfig().getDouble("bonus-chances.conditions.open-water", 8);
        }

        if (isNight) {
            bonusChance += plugin.getConfig().getDouble("bonus-chances.conditions.night-time", 5);
        }

        if (player.getWorld().hasStorm()) {
            bonusChance += plugin.getConfig().getDouble("bonus-chances.conditions.rain", 3);
        }

        // Проверка биома на null
        if (biomeName != null && !biomeName.isEmpty()) {
            try {
                Biome biome = Biome.valueOf(biomeName);
                if (isPremiumBiome(biome)) {
                    bonusChance += plugin.getConfig().getDouble(
                            "bonus-chances.conditions.premium-biome", 5);
                }
            } catch (IllegalArgumentException e) {
                // Неизвестный биом - игнорируем
            }
        }

        // Бонус навыка
        FishingSkill skill = Plugininit.getDataManager().getSkillLevel(player);
        bonusChance += getSkillBonus(skill);

        // Бонус цепочки
        int streak = Plugininit.getDataManager().getCurrentStreak(player);
        bonusChance += getStreakBonus(streak);

        // Зачарование
        ItemStack rod = player.getInventory().getItemInMainHand();
        if (rod != null && rod.getType() == Material.FISHING_ROD) {
            int luckLevel = rod.getEnchantmentLevel(Enchantment.LUCK_OF_THE_SEA);
            double luckBonus = plugin.getConfig().getDouble(
                    "bonus-chances.luck-of-the-sea.per-level", 3);
            bonusChance += luckLevel * luckBonus;
        }

        // Применяем глобальный множитель
        bonusChance *= globalMultiplier;

        return 100.0 + bonusChance;
    }
    private boolean isPremiumBiome(String biomeName) {
        try {
            Biome biome = Biome.valueOf(biomeName);
            return isPremiumBiome(biome);
        } catch (Exception e) {
            return false;
        }
    }

    private String getItemName(Material material) {
        return material.toString().toLowerCase().replace("_", " ");
    }
}