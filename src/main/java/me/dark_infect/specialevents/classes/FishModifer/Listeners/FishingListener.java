package me.dark_infect.specialevents.classes.FishModifer.Listeners;

import me.dark_infect.specialevents.SpecialEvents;
import me.dark_infect.specialevents.classes.FishModifer.FishingAttempt;
import me.dark_infect.specialevents.classes.FishModifer.FishingSkill;
import me.dark_infect.specialevents.classes.FishModifer.RarityTier;
import me.dark_infect.specialevents.utils.Chat;
import me.dark_infect.specialevents.utils.Plugininit;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class FishingListener implements Listener {

    private final JavaPlugin plugin;
    private final Map<UUID, FishingAttempt> activeAttempts;

    public FishingListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.activeAttempts = new HashMap<>();
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        FishHook hook = event.getHook();
        PlayerFishEvent.State state = event.getState();

        switch (state) {
            case FISHING:
                handleFishingStart(player, hook);
                break;
            case BITE:
                handleFishBite(event, player, hook);
                break;
            case CAUGHT_FISH:
                handleCatch(event, player, hook);
                break;
            case FAILED_ATTEMPT:
                handleFailedAttempt(player);
                break;
        }
    }

    private void handleFishingStart(Player player, FishHook hook) {
        // Настройка времени ожидания
        int minWait = plugin.getConfig().getInt("fishing.min-wait-time", 60);
        int maxWait = plugin.getConfig().getInt("fishing.max-wait-time", 200);

        hook.setMinWaitTime(minWait);
        hook.setMaxWaitTime(maxWait);
        hook.setApplyLure(true);

        // Очистка предыдущей попытки
        activeAttempts.remove(player.getUniqueId());
    }

    private void handleFishBite(PlayerFishEvent event, Player player, FishHook hook) {
        event.setCancelled(false); // Разрешаем стандартное поведение

        // Звук поклёвки
        player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_SPLASH, 1.0f, 1.5f);
        hook.getLocation().getWorld().spawnParticle(
                Particle.FALLING_WATER, hook.getLocation(), 20, 0.5, 0.5, 0.5, 0.1);

        // Определение окна реакции
        FishingSkill skill = Plugininit.getDataManager().getSkillLevel(player);
        int baseWindow = plugin.getConfig().getInt("fishing.base-reaction-window", 20);
        int reactionWindow = baseWindow + skill.getExtraReactionTicks();

        long biteTime = System.currentTimeMillis();

        // Визуальная подсказка
        int taskId = new BukkitRunnable() {
            int elapsed = 0;

            @Override
            public void run() {
                if (elapsed >= reactionWindow || !player.isOnline()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new TextComponent("§c§l✖ Упустили!"));
                    activeAttempts.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                int remaining = reactionWindow - elapsed;
                String bar = generateProgressBar(remaining, reactionWindow);

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new TextComponent("§e§l⚡ ТЯНИ! " + bar + " §7(" + remaining + ")"));

                elapsed++;
            }
        }.runTaskTimer(plugin, 0L, 1L).getTaskId();

        // Сохранение попытки
        FishingAttempt attempt = new FishingAttempt(
                player.getUniqueId(), biteTime, reactionWindow, hook, taskId);
        activeAttempts.put(player.getUniqueId(), attempt);
    }
    private void handleCatch(PlayerFishEvent event, Player player, FishHook hook) {
        FishingAttempt attempt = activeAttempts.get(player.getUniqueId());

        // Отмена стандартного лута
        event.setCancelled(true);
        event.setExpToDrop(0);

        // Остановка таймера
        if (attempt != null) {
            Bukkit.getScheduler().cancelTask(attempt.getTaskId());
        }

        // Обновление статистики
        Plugininit.getDataManager().incrementTotalCatches(player);

        if (attempt == null || !attempt.isSuccessful()) {
            handleFailedCatch(player, attempt);
            return;
        }

        // Успешная подсечка!
        handleSuccessfulCatch(player, hook, attempt);

        activeAttempts.remove(player.getUniqueId());
    }

    private void handleSuccessfulCatch(Player player, FishHook hook, FishingAttempt attempt) {
        // Обновление статистики
        Plugininit.getDataManager().incrementSuccessfulCatches(player);
        Plugininit.getDataManager().updateStreak(player, true);

        // Проверка повышения уровня
        checkLevelUp(player);

        // Расчёт редкости
        int reactionTicks = attempt.getReactionTicks();
        boolean openWater = hook.isInOpenWater();
        boolean isNight = hook.getWorld().getTime() > 13000 && hook.getWorld().getTime() < 23000;

        RarityTier tier = Plugininit.getLootGenerator().calculateRarity(
                player, reactionTicks, openWater,
                hook.getLocation().getBlock().getBiome(), isNight);

        // Генерация лута
        ItemStack loot = Plugininit.getLootGenerator().generateLoot(
                player, tier, hook.getLocation().getBlock().getBiome());

        // Выдача награды
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(hook.getLocation(), loot);
        } else {
            player.getInventory().addItem(loot);
        }

        // Эффекты
        playSuccessEffects(player, hook, tier);

        // Сообщение
        sendSuccessMessage(player, tier, reactionTicks);

        // Счётчик легендарных
        if (tier == RarityTier.LEGENDARY) {
            Plugininit.getDataManager().incrementLegendaryCount(player);
            Bukkit.broadcastMessage(tier.getColorCode() + "§l⚡ " +
                    player.getName() + " поймал ЛЕГЕНДАРНУЮ добычу!");
        }
        String biomeName = hook.getLocation().getBlock().getBiome().name();

// Логирование улова
        Plugininit.getFishingLogger().logCatch(
                player,
                tier,
                loot.getItemMeta() != null ? loot.getItemMeta().getDisplayName() : "Unknown",
                reactionTicks,
                Plugininit.getLootGenerator().calculateTotalChance(
                        player, reactionTicks, openWater, biomeName, isNight),
                openWater,
                biomeName
        );
    }
    private double calculateTotalChance(Player player, int reactionTicks,
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
        if (streak >= 10) bonusChance += 50;
        else if (streak >= 5) bonusChance += 20;

        // Зачарование "Удача моря"
        ItemStack rod = player.getInventory().getItemInMainHand();
        if (rod.getType() == Material.FISHING_ROD) {
            int luckLevel = rod.getEnchantmentLevel(Enchantment.LUCK_OF_THE_SEA);
            bonusChance += luckLevel * 5;
        }

        return bonusChance;
    }

    private boolean isPremiumBiome(Biome biome) {
        return biome == Biome.WARM_OCEAN ||
                biome == Biome.DEEP_OCEAN ||
                biome == Biome.MUSHROOM_FIELDS ||
                biome == Biome.FROZEN_OCEAN;
    }


    private void handleFailedCatch(Player player, FishingAttempt attempt) {
        Plugininit.getDataManager().updateStreak(player, false);

        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.8f, 1.0f);

        if (attempt != null) {
            int reactionTicks = attempt.getReactionTicks();
            int window = attempt.getReactionWindow();

            if (reactionTicks > window) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new TextComponent("§c§l✖ Слишком поздно! §7(+" +
                                (reactionTicks - window) + " тиков)"));
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new TextComponent("§c§l✖ Слишком рано!"));
            }
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent("§c§l✖ Упустили рыбу!"));
        }
    }

    private void handleFailedAttempt(Player player) {
        activeAttempts.remove(player.getUniqueId());
    }

    private void checkLevelUp(Player player) {
        int catches = Plugininit.getDataManager().getSuccessfulCatches(player);
        FishingSkill currentSkill = FishingSkill.getByProgress(catches);
        FishingSkill previousSkill = FishingSkill.getByProgress(catches - 1);

        if (currentSkill != previousSkill) {
            // Повышение уровня!
            player.sendTitle(
                    "§6§lПОВЫШЕНИЕ!",
                    currentSkill.getFormattedName() + " §eрыболов",
                    10, 70, 20);

            player.playSound(player.getLocation(),
                    Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);

            player.spawnParticle(Particle.TOTEM_OF_UNDYING,
                    player.getLocation().add(0, 1, 0), 30, 0.5, 1, 0.5);

            player.sendMessage("§7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            player.sendMessage("§6§l   ПОВЫШЕНИЕ НАВЫКА РЫБОЛОВСТВА!");
            player.sendMessage("");
            player.sendMessage("  §7Новый уровень: " + currentSkill.getFormattedName());
            player.sendMessage("  §7Бонус удачи: §e+" + currentSkill.getLuckBonus() + "%");
            player.sendMessage("  §7Время реакции: §e+" +
                    currentSkill.getExtraReactionTicks() + " тиков");
            player.sendMessage("§7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        }
    }

    private void playSuccessEffects(Player player, FishHook hook, RarityTier tier) {
        switch (tier) {
            case LEGENDARY:
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
                hook.getWorld().spawnParticle(Particle.END_ROD,
                        hook.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);
                break;
            case EPIC:
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                hook.getWorld().spawnParticle(Particle.ENCHANT,
                        hook.getLocation(), 30, 0.5, 0.5, 0.5, 1);
                break;
            case RARE:
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
                hook.getWorld().spawnParticle(Particle.HAPPY_VILLAGER,
                        hook.getLocation(), 20, 0.5, 0.5, 0.5);
                break;
            default:
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f, 1.0f);
                hook.getWorld().spawnParticle(Particle.FALLING_WATER,
                        hook.getLocation(), 15, 0.5, 0.5, 0.5);
        }
    }

    private void sendSuccessMessage(Player player, RarityTier tier, int reactionTicks) {
        int streak = Plugininit.getDataManager().getCurrentStreak(player);
        double reactionTime = reactionTicks * 0.05; // В секундах

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent(tier.getColorCode() + "§l✓ " + tier.getDisplayName().toUpperCase() +
                        " УЛОВ! §7(" + String.format("%.2f", reactionTime) + "с)"));

        if (streak >= 5) {
            player.sendMessage(tier.getColorCode() + "§l✦ " + tier.getDisplayName() +
                    " улов! §7(Цепочка: §e" + streak + "§7)");
        }
    }

    private int calculateExperience(RarityTier tier, int reactionTicks) {
        int baseExp = tier.ordinal() * 2 + 1;

        // Бонус за быструю реакцию
        if (reactionTicks <= 10) baseExp += 3;
        else if (reactionTicks <= 14) baseExp += 2;
        else if (reactionTicks <= 20) baseExp += 1;

        return ThreadLocalRandom.current().nextInt(baseExp, baseExp * 2);
    }

    private String generateProgressBar(int current, int max) {
        int bars = 10;
        int filled = (int)((double)current / max * bars);

        StringBuilder bar = new StringBuilder("§7[");
        for (int i = 0; i < bars; i++) {
            if (i < filled) {
                bar.append("§a█");
            } else {
                bar.append("§8█");
            }
        }
        bar.append("§7]");

        return bar.toString();
    }
}
