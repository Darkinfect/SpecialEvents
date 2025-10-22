package me.dark_infect.specialevents.classes.FishModifer;

import me.dark_infect.specialevents.SpecialEvents;
import me.dark_infect.specialevents.utils.Plugininit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ContainerManager {
    private final NamespacedKey lootTierKey;
    private final NamespacedKey isLootContainerKey;

    public ContainerManager() {
        this.lootTierKey = new NamespacedKey(SpecialEvents.getInstance(), "loot_tier");
        this.isLootContainerKey = new NamespacedKey(SpecialEvents.getInstance(), "is_loot_container");
    }

    public ItemStack createLootContainer(RarityTier tier) {
        Material containerMaterial;
        String displayName;
        int customModelData;

        switch (tier) {
            case LEGENDARY:
                containerMaterial = Material.CHEST;
                displayName = "§6§l✦ Сокровищница Посейдона";
                customModelData = 3;
                break;
            case EPIC:
                containerMaterial = Material.BARREL;
                displayName = "§5§l✦ Королевский сундук";
                customModelData = 2;
                break;
            default:
                containerMaterial = Material.BUNDLE;
                displayName = tier.getColorCode() + tier.getDisplayName() + " мешочек";
                customModelData = 1;
        }

        ItemStack container = new ItemStack(containerMaterial);
        ItemMeta meta = container.getItemMeta();

        meta.setDisplayName(displayName);
        meta.setCustomModelData(customModelData);
        meta.setLore(Arrays.asList(
                "§7",
                "§7ПКМ чтобы открыть",
                "§7Содержит §e" + tier.getItemCount() + " §7предметов",
                "§7Редкость: " + tier.getFormattedName(),
                "§7"
        ));

        // Сохранение данных в PDC
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(lootTierKey, PersistentDataType.STRING, tier.name());
        data.set(isLootContainerKey, PersistentDataType.BYTE, (byte) 1);

        container.setItemMeta(meta);
        return container;
    }

    public boolean isLootContainer(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        return data.has(isLootContainerKey, PersistentDataType.BYTE);
    }

    public void openContainer(Player player, ItemStack container) {
        PersistentDataContainer data = container.getItemMeta().getPersistentDataContainer();
        String tierName = data.get(lootTierKey, PersistentDataType.STRING);

        if (tierName == null) return;

        RarityTier tier = RarityTier.valueOf(tierName);
        List<ItemStack> loot = generateContainerLoot(player, tier);

        // Эффекты открытия
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.2f);
        player.spawnParticle(Particle.ENCHANT,
                player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5);

        if (tier == RarityTier.LEGENDARY) {
            player.spawnParticle(Particle.END_ROD,
                    player.getLocation().add(0, 1, 0), 50, 0.5, 0.5, 0.5);
            player.playSound(player.getLocation(),
                    Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        }

        // Выдача предметов
        int given = 0;
        for (ItemStack reward : loot) {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), reward);
            } else {
                player.getInventory().addItem(reward);
            }
            given++;
        }

        // Сообщение
        player.sendMessage("");
        player.sendMessage(tier.getColorCode() + "§l✦ Открыт " +
                tier.getDisplayName() + " контейнер!");
        player.sendMessage("§7Получено предметов: §e" + given);
        player.sendMessage("");

        // Удаление контейнера
        container.setAmount(container.getAmount() - 1);
    }

    private List<ItemStack> generateContainerLoot(Player player, RarityTier tier) {
        List<ItemStack> loot = new ArrayList<>();
        int itemCount = tier.getItemCount();

        LootGenerator generator = Plugininit.getLootGenerator();

        for (int i = 0; i < itemCount; i++) {
            // Определяем подтип лута
            double roll = ThreadLocalRandom.current().nextDouble(100);

            RarityTier itemTier = tier;
            // Шанс на более редкий лут внутри контейнера
            if (roll < 5 && tier.ordinal() < RarityTier.LEGENDARY.ordinal()) {
                itemTier = RarityTier.values()[tier.ordinal() + 1];
            }

            ItemStack item = generator.generateLoot(player, itemTier, null);
            if (item != null) {
                loot.add(item);
            }
        }

        return loot;
    }

    public NamespacedKey getLootTierKey() {
        return lootTierKey;
    }

    public NamespacedKey getIsLootContainerKey() {
        return isLootContainerKey;
    }
}
