package me.dark_infect.specialevents.classes;

import me.dark_infect.specialevents.SpecialEvents;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.*;
public class BossAbilityManager {

        private final Player boss;
        private final Map<String, Long> cooldowns = new HashMap<>();

        public BossAbilityManager(Player boss) {
            this.boss = boss;
        }

        public boolean executeAbility(String abilityName) {
            long currentTime = System.currentTimeMillis();

            // Проверка кулдауна
            if (cooldowns.containsKey(abilityName) &&
                    currentTime - cooldowns.get(abilityName) < getCooldown(abilityName)) {
                return false;
            }

            boolean success = false;
            switch (abilityName.toLowerCase()) {
                case "dash":
                    success = dashAbility();
                    break;
                case "aoecloud":
                    success = aoeCloudAbility();
                    break;
                case "aoetrap":
                    success = aoeTrapAbility();
                    break;
            }

            if (success) {
                cooldowns.put(abilityName, currentTime);
            }

            return success;
        }

        private long getCooldown(String abilityName) {
            switch (abilityName.toLowerCase()) {
                case "dash": return 3000; // 3 секунды
                case "aoecloud": return 5000; // 5 секунд
                case "aoetrap": return 8000; // 8 секунд
                default: return 1000;
            }
        }

        // 1. Механика рывка
        public boolean dashAbility() {
            Location targetLoc = boss.getEyeLocation().add(boss.getEyeLocation().getDirection().multiply(8));

            // Рывок с телепортацией
            boss.setVelocity(boss.getEyeLocation().getDirection().multiply(2).setY(0.3));

            // Визуальные эффекты
            createParticleLine(boss.getLocation(), targetLoc, Particle.CLOUD);
            boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);

            // Урон по пути (через 3 тика после рывка)
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Entity entity : boss.getNearbyEntities(1.5, 1.5, 1.5)) {
                        if (entity instanceof LivingEntity && entity != boss) {
                            LivingEntity living = (LivingEntity) entity;
                            living.damage(6.0, boss);
                            living.setVelocity(boss.getLocation().getDirection().multiply(0.5));
                        }
                    }
                }
            }.runTaskLater(Bukkit.getPluginManager().getPlugin("YourPlugin"), 3L);

            return true;
        }

        // 2. АоЕ облако
        public boolean aoeCloudAbility() {
            Location cloudLoc = boss.getLocation().add(boss.getEyeLocation().getDirection().multiply(3));


            boss.getWorld().playSound(cloudLoc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.5f);

            new BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    if (ticks++ >= 60) {
                        this.cancel();
                        return;
                    }


                    boss.getWorld().spawnParticle(Particle.DRAGON_BREATH, cloudLoc, 30, 2, 2, 2, 0.1);
                    boss.getWorld().spawnParticle(Particle.SMOKE, cloudLoc, 20, 2, 2, 2, 0.05);


                    for (Entity entity : cloudLoc.getWorld().getNearbyEntities(cloudLoc, 2.5, 2.5, 2.5)) {
                        if (entity instanceof Player && entity != boss) {
                            Player target = (Player) entity;
                            target.damage(8.0); // Смертельный урон
                            target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1));
                        }
                    }
                }
            }.runTaskTimer(SpecialEvents.getInstance(), 0L, 1L);

            return true;
        }

        // 3. АоЕ ловушки под игроками
        public boolean aoeTrapAbility() {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target != boss && target.getLocation().distance(boss.getLocation()) < 30) {
                    createTrapUnderPlayer(target);
                }
            }
            return true;
        }

        private void createTrapUnderPlayer(Player target) {
            Location trapLoc = target.getLocation().clone().add(0, 0.1, 0);

            new BukkitRunnable() {
                int warningTicks = 0;

                @Override
                public void run() {
                    if (warningTicks++ >= 20) {
                        this.cancel();
                        activateTrap(trapLoc);
                        return;
                    }

                    target.getWorld().spawnParticle(Particle.DUST, trapLoc, 10,
                            new Particle.DustOptions(Color.RED, 2.0f));
                    target.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, trapLoc, 3, 0.5, 0, 0.5, 0);
                }
            }.runTaskTimer(SpecialEvents.getInstance(), 0L, 1L);
        }

        private void activateTrap(Location trapLoc) {

            trapLoc.getWorld().spawnParticle(Particle.CRIT, trapLoc, 50, 1.5, 0, 1.5, 0.3);
            trapLoc.getWorld().playSound(trapLoc, Sound.BLOCK_ANVIL_LAND, 1.0f, 0.6f);

            new BukkitRunnable() {
                int activeTicks = 0;

                @Override
                public void run() {
                    if (activeTicks++ >= 10000) {
                        this.cancel();
                        return;
                    }

                    for (Entity entity : trapLoc.getWorld().getNearbyEntities(trapLoc, 2, 2, 2)) {
                        if (entity instanceof Player && entity != boss) {
                            Player target = (Player) entity;
                            target.damage(3.0);
                            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 1));
                        }
                    }

                    if (activeTicks % 5 == 0) {
                        trapLoc.getWorld().spawnParticle(Particle.SMOKE, trapLoc, 10, 1, 0.2, 1, 0.1);
                    }
                }
            }.runTaskTimer(SpecialEvents.getInstance(), 0L, 1L);
        }

        private void createParticleLine(Location start, Location end, Particle particle) {
            Vector direction = end.toVector().subtract(start.toVector());
            double distance = start.distance(end);
            int particles = (int) (distance * 3);

            for (int i = 0; i <= particles; i++) {
                double ratio = (double) i / particles;
                Location point = start.clone().add(direction.clone().multiply(ratio));
                start.getWorld().spawnParticle(particle, point, 1, 0, 0, 0, 0);
            }
        }
}
