package me.dark_infect.specialevents.classes.FishModifer;
import me.dark_infect.specialevents.utils.Plugininit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

        // Бонус за реакцию
        if (reactionTicks <= 10) bonusChance += 30;      // < 0.5 сек
        else if (reactionTicks <= 14) bonusChance += 20; // < 0.7 сек
        else if (reactionTicks <= 20) bonusChance += 10; // < 1 сек

        // Бонусы условий
        if (openWater) bonusChance += 15;
        if (isNight) bonusChance += 10;
        if (isPremiumBiome(biome)) bonusChance += 10;

        // Бонус навыка
        FishingSkill skill = Plugininit.getDataManager().getSkillLevel(player);
        bonusChance += skill.getLuckBonus();

        // Бонус цепочки
        int streak = Plugininit.getDataManager().getCurrentStreak(player);
        if (streak >= 10) bonusChance += 50; // Гарантия редкого
        else if (streak >= 5) bonusChance += 20;

        // Зачарование "Удача моря"
        ItemStack rod = player.getInventory().getItemInMainHand();
        if (rod.getType() == Material.FISHING_ROD) {
            int luckLevel = rod.getEnchantmentLevel(Enchantment.LUCK_OF_THE_SEA);
            bonusChance += luckLevel * 5;
        }

        // Рассчёт финальной редкости
        double roll = ThreadLocalRandom.current().nextDouble(100);
        double adjustedRoll = roll - bonusChance;

        if (adjustedRoll < 1) return RarityTier.LEGENDARY;
        if (adjustedRoll < 5) return RarityTier.EPIC;
        if (adjustedRoll < 15) return RarityTier.RARE;
        if (adjustedRoll < 40) return RarityTier.UNCOMMON;
        return RarityTier.COMMON;
    }

    public ItemStack generateLoot(Player player, RarityTier tier, Biome biome) {
        // 10% шанс на контейнер для редких+ уровней
        if (tier.ordinal() >= RarityTier.RARE.ordinal()) {
            if (ThreadLocalRandom.current().nextDouble() < 0.1) {
                return Plugininit.getContainerManager().createLootContainer(tier);
            }
        }

        // Специальный лут биома
        ItemStack biomeLoot = getBiomeLoot(biome, tier);
        if (biomeLoot != null) {
            return biomeLoot;
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
        meta.setLore(Arrays.asList(
                "§7Редкость: " + tier.getFormattedName(),
                "§7Выловлено из водных глубин"
        ));
        ItemStack fish = new ItemStack(Material.TROPICAL_FISH);
        fish.setItemMeta(meta);

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
        }

        return null;
    }

    private boolean isPremiumBiome(Biome biome) {
        return biome == Biome.WARM_OCEAN || biome == Biome.DEEP_OCEAN ||
                biome == Biome.MUSHROOM_FIELDS || biome == Biome.FROZEN_OCEAN;
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

    private String getItemName(Material material) {
        return material.toString().toLowerCase().replace("_", " ");
    }
}