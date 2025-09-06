package me.dark_infect.specialevents.classes;


import org.bukkit.Color;
import me.dark_infect.specialevents.utils.VFXEffect;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Vex;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class Inflection {
    private final Location center;
    private final Set<Location> inflectedblocks = new CopyOnWriteArraySet<>();;
    private final Random random = new Random();
    private boolean isGrownUp = false;
    private int maxsize = 10;
    private int rec = 10;
    private static final World world = Bukkit.getWorld("world");
    private int radius = 10;
    private final Set<Material> immuneBlocks = new HashSet<>(Arrays.asList(
            Material.CRYING_OBSIDIAN,
            Material.AIR,
            Material.CHEST,
            Material.BEDROCK,
            Material.BARRIER,
            Material.END_PORTAL_FRAME,
            Material.SHULKER_BOX,
            Material.BARREL
    ));
    public Inflection(Location center){
        this.center = center;
        inflectedblocks.add(center);
    }
    public void growUp(){
//        if(inflectedblocks.size() > 210 || isGrownUp){
//            isGrownUp = true;
//            if(!inflectedblocks.isEmpty()){
//                inflectedblocks.clear();
//            }
//
//        }
        if(rec == 10){
            spawnVexis();
            spawnVexis();
            spawnVexis();
            rec = 0;
        }
        for (Location source: inflectedblocks) {
            int dx = random.nextInt(3) - 1;
            int dz = random.nextInt(3) - 1;
            int dy = random.nextInt(3)-1;

            Location newInfection = source.clone().add(dx, dy, dz);
            Block block = newInfection.getBlock();
            World world = block.getWorld();

            if (hasAirNearby(block) && !immuneBlocks.contains(block.getType()) && isCanGrewUp(block)) {
                this.infectBlock(newInfection);
            }
        }
        rec++;
        upgraderadius(inflectedblocks.size());
    }
    private boolean hasAirNearby(Block block) {
        return block.getRelative(1, 0, 0).getType() == Material.AIR ||
                block.getRelative(-1, 0, 0).getType() == Material.AIR ||
                block.getRelative(0, 1, 0).getType() == Material.AIR ||
                block.getRelative(0, -1, 0).getType() == Material.AIR ||
                block.getRelative(0, 0, 1).getType() == Material.AIR ||
                block.getRelative(0, 0, -1).getType() == Material.AIR;
    }
    private void infectBlock(Location loc) {
        Block block = loc.getBlock();
        block.setType(Material.CRYING_OBSIDIAN);
        inflectedblocks.add(loc);

        loc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 10);
        loc.getWorld().playSound(loc, Sound.BLOCK_GLASS_BREAK, 0.7f, 0.5f);
    }
    public int getRadius() {
        return radius;
    }

    private void upgraderadius(int count){
        if(count > maxsize){
            maxsize *=2;
            radius+=3;
            return;
        }
        return;
    }
    public void spawnVexis() {
        if (world == null || center == null) return;

        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            double offsetX = random.nextDouble() * 10 - 5;
            double offsetY = 0;
            double offsetZ = random.nextDouble() * 10 - 5;

            Location spawnLoc = center.clone().add(offsetX, offsetY, offsetZ);
            Vex vex = world.spawn(spawnLoc, Vex.class);
            vex.setCharging(true);
            vex.setSilent(false);
            vex.setAI(true);
            vex.setRemoveWhenFarAway(true);
        }
    }
    public void spawnParticles(){
        VFXEffect.createEffect(center, VFXEffect.EffectType.EXPLOSION, Color.BLUE,6,2000);
    }
    public Set<Location> getInflectedblocks() {
        return inflectedblocks;
    }
    public Location getCenter() {
        return center;
    }
    private boolean isCanGrewUp(Block block){
        return immuneBlocks.contains(block.getRelative(1,0,0).getType())  ||
                immuneBlocks.contains(block.getRelative(-1,0,0).getType())  ||
                immuneBlocks.contains(block.getRelative(0,1,0).getType()) ||
                immuneBlocks.contains(block.getRelative(0,-1,0).getType()) ||
                immuneBlocks.contains(block.getRelative(0,0,1).getType()) ||
                immuneBlocks.contains(block.getRelative(0,0,-1).getType());
    }
}
