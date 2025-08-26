package me.dark_infect.specialevents.utils;

import org.bukkit.Color;
import me.dark_infect.specialevents.SpecialEvents;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VFXEffect {
    private static final JavaPlugin plugin = SpecialEvents.getInstance();
    public enum EffectType {
        CIRCLE,
        SPHERE,
        SPIRAL,
        BEAM,
        EXPLOSION,
        RINGS
    }

    public static void createEffect(Location center, EffectType type, Color color,
                                    double radius, int durationTicks, Player... viewers) {
        if (center == null || center.getWorld() == null) return;

        World world = center.getWorld();
        List<Player> targetViewers = getViewers(viewers, world);

        switch (type) {
            case CIRCLE:
                createCircleEffect(center, color, radius, durationTicks, targetViewers);
                break;
            case SPHERE:
                createSphereEffect(center, color, radius, durationTicks, targetViewers);
                break;
            case SPIRAL:
                createSpiralEffect(center, color, radius, durationTicks, targetViewers);
                break;
            case BEAM:
                createBeamEffect(center, color, radius, durationTicks, targetViewers);
                break;
            case EXPLOSION:
                createExplosionEffect(center, color, radius, durationTicks, targetViewers);
                break;
            case RINGS:
                createRingsEffect(center, color, radius, durationTicks, targetViewers);
                break;
        }
    }

    private static List<Player> getViewers(Player[] specificViewers, World world) {
        List<Player> viewers = new ArrayList<>();

        if (specificViewers.length > 0) {
            for (Player viewer : specificViewers) {
                if (viewer != null && viewer.isOnline()) {
                    viewers.add(viewer);
                }
            }
        } else {
            viewers.addAll(world.getPlayers());
        }

        return viewers;
    }


    private static void createCircleEffect(Location center, Color color, double radius,
                                           int durationTicks, List<Player> viewers) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= durationTicks) {
                    this.cancel();
                    return;
                }

                int points = 30;
                for (int i = 0; i < points; i++) {
                    double angle = 2 * Math.PI * i / points;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);

                    Location particleLoc = center.clone().add(x, 0, z);
                    spawnParticle(particleLoc, color, viewers);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void createSphereEffect(Location center, Color color, double radius,
                                           int durationTicks, List<Player> viewers) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= durationTicks) {
                    this.cancel();
                    return;
                }

                int points = 20;
                for (int i = 0; i < points; i++) {
                    double phi = Math.PI * i / points;
                    for (int j = 0; j < points; j++) {
                        double theta = 2 * Math.PI * j / points;

                        double x = radius * Math.sin(phi) * Math.cos(theta);
                        double y = radius * Math.cos(phi);
                        double z = radius * Math.sin(phi) * Math.sin(theta);

                        Location particleLoc = center.clone().add(x, y, z);
                        spawnParticle(particleLoc, color, viewers);
                    }
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    private static void createSpiralEffect(Location center, Color color, double radius,
                                           int durationTicks, List<Player> viewers) {
        new BukkitRunnable() {
            int ticks = 0;
            double height = 0;

            @Override
            public void run() {
                if (ticks >= durationTicks) {
                    this.cancel();
                    return;
                }

                int points = 50;
                for (int i = 0; i < points; i++) {
                    double angle = 2 * Math.PI * i / points;
                    double currentRadius = radius * (1 - (double) ticks / durationTicks);
                    double currentHeight = height * (double) i / points;

                    double x = currentRadius * Math.cos(angle);
                    double z = currentRadius * Math.sin(angle);

                    Location particleLoc = center.clone().add(x, currentHeight, z);
                    spawnParticle(particleLoc, color, viewers);
                }

                height += 0.1;
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void createBeamEffect(Location center, Color color, double radius,
                                         int durationTicks, List<Player> viewers) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= durationTicks) {
                    this.cancel();
                    return;
                }

                int circlePoints = 15;
                for (int i = 0; i < circlePoints; i++) {
                    double angle = 2 * Math.PI * i / circlePoints;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);

                    Location baseLoc = center.clone().add(x, 0, z);
                    spawnParticle(baseLoc, color, viewers);
                }

                int heightPoints = 20;
                for (int i = 0; i < heightPoints; i++) {
                    double y = i * 0.5;
                    Location beamLoc = center.clone().add(0, y, 0);
                    spawnParticle(beamLoc, color, viewers);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private static void createExplosionEffect(Location center, Color color, double radius,
                                              int durationTicks, List<Player> viewers) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= durationTicks) {
                    this.cancel();
                    return;
                }

                double currentRadius = radius * ((double) ticks / durationTicks);

                int points = 25;
                for (int i = 0; i < points; i++) {
                    double angle = 2 * Math.PI * i / points;
                    double x = currentRadius * Math.cos(angle);
                    double z = currentRadius * Math.sin(angle);

                    for (int j = -2; j <= 2; j++) {
                        Location ringLoc = center.clone().add(x, j * 0.5, z);
                        spawnParticle(ringLoc, color, viewers);
                    }
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void createRingsEffect(Location center, Color color, double radius,
                                          int durationTicks, List<Player> viewers) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= durationTicks) {
                    this.cancel();
                    return;
                }

                int rings = 5;
                for (int ring = 1; ring <= rings; ring++) {
                    double ringRadius = radius * ring / rings;
                    double offset = Math.sin(ticks * 0.1) * 0.5;

                    int points = 20;
                    for (int i = 0; i < points; i++) {
                        double angle = 2 * Math.PI * i / points;
                        double x = ringRadius * Math.cos(angle);
                        double z = ringRadius * Math.sin(angle);

                        Location ringLoc = center.clone().add(x, offset, z);
                        spawnParticle(ringLoc, color, viewers);
                    }
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void spawnParticle(Location location,Color color, List<Player> viewers) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 4.0f);

        for (Player viewer : viewers) {
            if (viewer.getWorld().equals(location.getWorld())) {
                double distanceSquared = viewer.getLocation().distanceSquared(location);
                if (distanceSquared <= 256 * 256) {
                    // ПРАВИЛЬНЫЙ способ для Dust particles
                    viewer.spawnParticle(
                            Particle.DUST,
                            location,
                            1, // count
                            0, // offsetX
                            0, // offsetY
                            0, // offsetZ
                            0, // extra
                            dustOptions // data
                    );
                }
            }
        }
    }

    public static void playEffectSound(Location location, Sound sound, float volume, float pitch) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }

    public static void createQuickFlash(Location center, Color color, List<Player> viewers) {
        for (int i = 0; i < 10; i++) {
            double offsetX = (Math.random() - 0.5) * 2;
            double offsetY = (Math.random() - 0.5) * 2;
            double offsetZ = (Math.random() - 0.5) * 2;

            Location flashLoc = center.clone().add(offsetX, offsetY, offsetZ);
            spawnParticle(flashLoc, color, viewers);
        }
    }
}
