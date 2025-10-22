package me.dark_infect.specialevents.listeners;

import me.dark_infect.specialevents.SpecialEvents;
import me.dark_infect.specialevents.classes.Inflection;
import me.dark_infect.specialevents.utils.Chat;
import me.dark_infect.specialevents.utils.playerUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EndEvent implements Listener {
    private static final Set<Inflection> inflections = new HashSet<>();
    private static final Map<UUID, Long> fatiguePlayers = new HashMap<>();
    private static final World overworld = SpecialEvents.getInstance().getServer().getWorld("world");
    private static Location cureGlassLocation;
    private static boolean isCured = false;
    private static final Random random = new Random();
    private static long timer = 20 * 60 * 30;
    private static boolean isMessage = true;

    public static void setTimer(long timer) {
        EndEvent.timer = timer;
    }
    public static void setIsCured(boolean isCured) {
        EndEvent.isCured = isCured;
    }
    public static void SetLocation(Location location){
        cureGlassLocation = location;
    }
    public static void setInflections(Player player){
        Location loc = player.getLocation();
        Location location = new Location(loc.getWorld(), loc.getX(),loc.getWorld().getHighestBlockYAt(loc),loc.getZ());
        Inflection inflection = new Inflection(location);
        location.getBlock().setType(Material.CRYING_OBSIDIAN);
        inflections.add(inflection);
    }
    public static void growup(){
        inflections.forEach(Inflection::growUp);
    }
    public static void startInfection() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isCured) {
                    cancel();
                    return;
                }
                SpecialEvents.getInstance().getServer().getWorld("world").setStorm(true);
                inflections.forEach(Inflection::growUp);
            }
        }.runTaskTimer(SpecialEvents.getInstance(), 0, timer);
    }
    public static void startThunder(){
        new BukkitRunnable() {
            @Override
            public void run(){
                if(isCured){
                    cancel();
                    return;
                }
                thunderStrike(playerUtils.getRundomPlayer());
                overworld.setTime(18000);
            }
        }.runTaskTimer(SpecialEvents.getInstance(),0,20*10);
    }
    public static boolean isIsCured() {
        return isCured;
    }


    public static Set<Inflection> getInflections(){
        return inflections;
    }
    public static void thunderStrike(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        try {
            Location playerLoc = player.getLocation();
            World world = playerLoc.getWorld();

            if (world == null) {
                return;
            }

            Location strikeLocation = generateStrikeLocation(playerLoc, 100);

            if (strikeLocation == null) {
                return;
            }
            world.strikeLightning(strikeLocation);

            playThunderEffects(strikeLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Location generateStrikeLocation(Location center, int radius) {
        World world = center.getWorld();
        if (world == null) return null;

        Random random = new Random();

        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble() * radius;

        int x = (int) (center.getX() + distance * Math.cos(angle));
        int z = (int) (center.getZ() + distance * Math.sin(angle));

        int y = world.getHighestBlockYAt(x, z);

        Location strikeLoc = new Location(world, x, y, z);
        Material surfaceBlock = strikeLoc.getBlock().getType();

        if (surfaceBlock == Material.WATER || surfaceBlock == Material.LAVA) {
            return findValidStrikeLocation(world, x, z, y, 10);
        }

        return strikeLoc;
    }
    private static Location findValidStrikeLocation(World world, int startX, int startZ, int startY, int searchRadius) {
        for (int dx = -searchRadius; dx <= searchRadius; dx++) {
            for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                int x = startX + dx;
                int z = startZ + dz;
                int y = world.getHighestBlockYAt(x, z);

                Location testLoc = new Location(world, x, y, z);
                Material blockType = testLoc.getBlock().getType();

                if (blockType != Material.WATER && blockType != Material.LAVA) {
                    return testLoc;
                }
            }
        }
        return new Location(world, startX, startY, startZ);
    }

    private static void playThunderEffects(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        world.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10.0f, 0.8f);
        world.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 5.0f, 1.0f);

        world.spawnParticle(Particle.FLASH, location, 1);
        world.spawnParticle(Particle.ELECTRIC_SPARK, location, 50, 0.5, 1.0, 0.5, 0.2);

        Block block = location.getBlock();
        if (block.getType().isFlammable()) {
            block.setType(Material.FIRE);
            Bukkit.getScheduler().runTaskLater(SpecialEvents.getInstance(), () -> {
                if (block.getType() == Material.FIRE) {
                    block.setType(Material.AIR);
                }
            }, 60L);
        }
    }
    public static void thunderStrike(Player player, int radius) {
        if (player == null || !player.isOnline()) {
            return;
        }

        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();

        if (world == null) {
            return;
        }

        Location strikeLocation = generateStrikeLocation(playerLoc, radius);

        if (strikeLocation != null) {
            world.strikeLightning(strikeLocation);
            playThunderEffects(strikeLocation);
        }
    }

    //    private static void spreadInfection() {
//        if(inflections.size() < 15) {
//            World world = Bukkit.getWorld("world");
//            int x = random.nextInt(2250) - random.nextInt(2250);
//            int z = random.nextInt(2250) - random.nextInt(2250);
//            int y = world.getHighestBlockYAt(x, z);
//            Location centerInflect = new Location(world, x, y, z);
//            Inflection inflection = new Inflection(centerInflect);
//            centerInflect.getBlock().setType(Material.CRYING_OBSIDIAN);
//            inflections.add(inflection);
//            Chat.debugmessage("Проросло новое семя");
//        }else{
//            if(isMessage){
//                isMessage = false;
//                Bukkit.broadcastMessage(ChatColor.RED + "[WARNING] Максимальное количество семян было достигнуто!!!!!!!!!");
//            }
//            return;
//        }
//    }
/*    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (isCured) return;

        for (Inflection inflection: inflections) {
            if(player.getLocation().distance(inflection.getCenter()) <= inflection.getRadius()){
                if(player.hasPermission("se.immune")) return;
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.MINING_FATIGUE,
                        20 * 10, // 10 секунд
                        2,       // Уровень III
                        true
                ));
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.NAUSEA,
                        20*10,
                        2,
                        true
                ));
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.HUNGER,
                        20*10,
                        2,
                        true
                ));
                fatiguePlayers.put(player.getUniqueId(), System.currentTimeMillis());
                break;
            }
        }
    }*/
}
