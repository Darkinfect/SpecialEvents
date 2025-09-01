package me.dark_infect.specialevents.classes;

import me.dark_infect.specialevents.SpecialEvents;
import me.dark_infect.specialevents.utils.VFXEffect;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.A;

import java.util.*;


// 5 blocks
// fireball в точку от Сенти и там Яд проверять раз 3 секнуды на выход
public class FinalFigth {
    private static FinalFigth instance;
    private Location center;
    private int radius;
    private static final List<String> storyParts = new ArrayList<>();
    private static Warden warden;
    private List<WitherSkeleton> witherSkeletons = new ArrayList<>();

    public static FinalFigth getInstance() {
        return instance;
    }
    private void initializeStory() {
        storyParts.add("§6§lЛЕГЕНДА О КРОНОСЕ§r\n§e<Dark_infect>В эпоху, когда мир был ещё юн, а боги ходили среди смертных...");
        storyParts.add("§7жил Кронос — могущественный титан, повелитель времени и хранитель равновесия.");
        storyParts.add("§7Он был одним из тех, кто даровал людям знания о земледелии, ремёслах и звёздах.");
        storyParts.add("§7Люди почитали его как мудрого покровителя, и он любил их как своих детей.");

        storyParts.add("§cНо случилось несчастье. Тёмная энергия отравила душу Кроноса...");
        storyParts.add("§7Его божественная сущность стала медленно разъедаться тьмой.");
        storyParts.add("§7Чтобы не навредить тем, кого любил, он добровольно отказался от бессмертия.");

        storyParts.add("§7Долгие годы Кронос жил среди людей, скрывая свою истинную природу.");
        storyParts.add("§7Люди звали его Старым Кроном, и он помогал им последними крупицами силы.");

        storyParts.add("§4Но тьма внутри него росла... В одну из лунных ночей воспоминания хлынули обратно.");
        storyParts.add("§7Он вспомнил о Святилище Вечности — месте, где хранилась его истинная сила.");

        storyParts.add("§8Ведомый тёмным зовом, Кронос отправился в забытые руины древнего храма...");
        storyParts.add("§4Когда он прикоснулся к Источнику Времени, произошло необратимое.");

        storyParts.add("§cТьма слилась с его силой, создавая чудовищное заражение!");
        storyParts.add("§7Заражение начало расползаться по миру, превращая всё живое в каменные изваяния.");

        storyParts.add("§4Теперь Кронос, некогда защитник, стал величайшей угрозой!");
        storyParts.add("§7Его тело пульсирует в сердце храма, распространяя заражение по земле.");

        storyParts.add("§c§lЕсли его не остановить — заражение поглотит весь мир!");
        storyParts.add("§6Только смелые воины могут проникнуть в Святилище Вечности...");
        storyParts.add("§6...и совершить последнее милосердие — освободить душу Кроноса от тьмы.");

        storyParts.add("§7§o\"Не убивайте того, кого когда-то любили — освободите того, кто уже мёртв внутри.\"");
        storyParts.add("§8§o— последние слова Кроноса перед тем, как тьма поглотила его разум.");

        storyParts.add("§4§lВремя истекает. Заражение расползается. Судьба мира в ваших руках...............");
    }
    public FinalFigth(Location center, int radius) {
        instance = this;
        this.center = center;
        this.radius = radius;
        initializeStory();
    }
    public boolean CheckEgg(){
        if(center.getBlock().getType().equals(Material.DRAGON_EGG)){
            bossFightScene();
            return true;
        }
        return false;
    }
    private void bossFightScene(){
        new BukkitRunnable(){
            @Override
            public void run(){
                createBlueFireCircle(center.add(0,10,0), 10,300);
                cancel();
            }
        }.runTaskTimer(SpecialEvents.getInstance(),0L,20*15);
    }
    public void setCenter(Location center) {
        this.center = center;
    }
    public void createBlueFireCircle(Location center, double radius, int durationTicks) {
        new BukkitRunnable() {
            double currentRadius = 0;
            int otherticks = -1000;
            int ticks = 0;
            boolean flag = true;
            @Override
            public void run() {
                if (ticks++ <= durationTicks) {
                    currentRadius = radius * (ticks / (double) durationTicks); //                       10 * (1/300)= small ->

                        // Создаем круг из частиц
                        for (int i = 0; i < 360; i += 5) { // 72 точки в круге
                            double angle = Math.toRadians(i);
                            double x = center.getX() + currentRadius * Math.cos(angle);
                            double z = center.getZ() + currentRadius * Math.sin(angle);

                            Location particleLoc = new Location(center.getWorld(), x, center.getY(), z);

                            // Синий огонь (SOUL_FIRE_FLAME) + дополнительные эффекты
                            center.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc, 1, 0, 0, 0, 0);

                            // Дополнительные эффекты для красоты
                            if (i % 15 == 0) {
                                center.getWorld().spawnParticle(Particle.SOUL, particleLoc, 2, 0.1, 0.1, 0.1, 0.05);
                                center.getWorld().spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0.1, 0, 0.01);
                            }
                        }

                }else {
                    if(flag){
                        collectEnderParticlesToPoint(center,15,durationTicks,120);
                        flag = false;
                    }
                    if(otherticks>=durationTicks){
                        // Финальный эффект при завершении
                        if (ticks == durationTicks) {
                            center.getWorld().playSound(center, Sound.ITEM_FIRECHARGE_USE, 1.0f, 0.8f);
                            center.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, center, 50, radius, 1, radius, 0.1);
                        }
                        this.cancel();
                        return;
                    }
                    for (int i = 0; i < 360; i += 5) { // 72 точки в круге
                        double angle = Math.toRadians(i);
                        double x = center.getX() + currentRadius * Math.cos(angle);
                        double z = center.getZ() + currentRadius * Math.sin(angle);

                        Location particleLoc = new Location(center.getWorld(), x, center.getY(), z);

                        // Синий огонь (SOUL_FIRE_FLAME) + дополнительные эффекты
                        center.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc, 1, 0, 0, 0, 0);
                    }
                    otherticks++;
                }
            }
        }.runTaskTimer(SpecialEvents.getInstance(), 0L, 1L);
    }
    public void collectEnderParticlesToPoint(Location targetPoint, double radius, int durationTicks, int particleCount) {
        new BukkitRunnable() {
            int ticks = 0;
            final Random random = new Random();
            final Map<Integer, Location> particlePaths = new HashMap<>();

            @Override
            public void run() {
                if (ticks++ >= durationTicks) {
                    // Финальный эффект в конце
                    createFinalEffect(targetPoint);
                    this.cancel();
                    return;
                }

                // Создаем новые частицы на каждом тике
                if (ticks % 5 == 0) {
                    createNewParticles(targetPoint, radius, particleCount);
                }

                // Двигаем все существующие частицы к цели
                moveParticlesToTarget(targetPoint);

                // Эффект притяжения в целевой точке
                if (ticks % 10 == 0) {
                    targetPoint.getWorld().spawnParticle(Particle.REVERSE_PORTAL, targetPoint, 3, 0.2, 0.2, 0.2, 0.1);
                    targetPoint.getWorld().playSound(targetPoint, Sound.BLOCK_PORTAL_AMBIENT, 0.3f, 2.0f);
                }
            }

            private void createNewParticles(Location target, double radius, int count) {
                for (int i = 0; i < count; i++) {
                    // Случайная точка в сфере радиуса
                    double x = target.getX() + (random.nextDouble() * 2 - 1) * radius;
                    double y = target.getY() + (random.nextDouble() * 2 - 1) * radius;
                    double z = target.getZ() + (random.nextDouble() * 2 - 1) * radius;

                    Location startLoc = new Location(target.getWorld(), x, y, z);
                    particlePaths.put(particlePaths.size(), startLoc.clone());

                    // Эффект появления частицы
                    target.getWorld().spawnParticle(Particle.PORTAL, startLoc, 2, 0.1, 0.1, 0.1, 0.05);
                }
            }

            private void moveParticlesToTarget(Location target) {
                Iterator<Map.Entry<Integer, Location>> iterator = particlePaths.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<Integer, Location> entry = iterator.next();
                    Location currentLoc = entry.getValue();

                    // Вычисляем направление к цели
                    Vector direction = target.toVector().subtract(currentLoc.toVector()).normalize();
                    double distance = currentLoc.distance(target);

                    // Скорость движения (быстрее к концу)
                    double speed = 0.3 + (0.7 * (ticks / (double) durationTicks));

                    // Двигаем частицу
                    Location newLoc = currentLoc.clone().add(direction.multiply(speed));
                    entry.setValue(newLoc);

                    // Эффект движения частицы
                    target.getWorld().spawnParticle(Particle.END_ROD, newLoc, 1, 0, 0, 0, 0);
                    target.getWorld().spawnParticle(Particle.PORTAL, newLoc, 1, 0.05, 0.05, 0.05, 0.01);

                    // Если частица достигла цели
                    if (distance < 0.5) {
                        target.getWorld().spawnParticle(Particle.FLASH, target, 1, 0, 0, 0, 0);
                        target.getWorld().playSound(target, Sound.ENTITY_ENDER_EYE_DEATH, 0.2f, 1.8f);
                        iterator.remove();
                    }
                }
            }

            private void createFinalEffect(Location target) {
                // Большой финальный взрыв частиц
                target.getWorld().spawnParticle(Particle.DRAGON_BREATH, target, 100, 1.0, 1.0, 1.0, 0.2);
                target.getWorld().spawnParticle(Particle.END_ROD, target, 50, 0.5, 0.5, 0.5, 0.1);
                target.getWorld().spawnParticle(Particle.REVERSE_PORTAL, target, 30, 0.3, 0.3, 0.3, 0.05);

                // Звуковые эффекты
                target.getWorld().playSound(target, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8f, 1.5f);
                target.getWorld().playSound(target, Sound.ITEM_TRIDENT_THUNDER, 0.6f, 2.0f);

                createDiagonalRaysToGround(target,12,1000);
                // Очищаем оставшиеся частицы
                particlePaths.clear();
            }

        }.runTaskTimer(SpecialEvents.getInstance(), 0L, 1L);
    }

    public Location getCenter() {
        return center;
    }

    public void createDiagonalRaysToGround(Location startPoint, double rayLength, int durationTicks) {
        new BukkitRunnable() {
            int ticks = 0;
            final Location[] rayEnds = new Location[4];
            final Location[] currentPoints = new Location[4];

            @Override
            public void run() {
                if (ticks++ >= durationTicks) {
                    createFinalEffect(startPoint, rayEnds);
                    this.cancel();
                    return;
                }

                // Инициализация при первом запуске
                if (ticks == 1) {
                    initializeRays(startPoint, rayLength);
                }

                // Двигаем лучи к их конечным точкам
                moveRaysToTarget();

                // Отрисовка лучей
                drawRays();

                // Звуковые эффекты
                if (ticks % 5 == 0) {
                    startPoint.getWorld().playSound(startPoint, Sound.BLOCK_BEACON_AMBIENT, 0.3f, 1.8f);
                }
            }

            private void initializeRays(Location center, double length) {
                World world = center.getWorld();
                double x = center.getX();
                double y = center.getY();
                double z = center.getZ();

                // 4 диагональных направления
                Vector[] directions = {
                        new Vector(1, -1, 1),    // SE
                        new Vector(1, -1, -1),   // SW
                        new Vector(-1, -1, 1),   // NE
                        new Vector(-1, -1, -1)   // NW
                };

                for (int i = 0; i < 4; i++) {
                    Vector dir = directions[i].normalize().multiply(length);
                    Location endPoint = center.clone().add(dir);

                    // Находим реальный блок снизу для этой конечной точки
                    rayEnds[i] = findGroundBlock(endPoint);
                    currentPoints[i] = center.clone();
                }
            }

            private Location findGroundBlock(Location location) {
                World world = location.getWorld();
                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();

                // Ищем первый непрозрачный блок снизу
                for (int checkY = y; checkY > world.getMinHeight(); checkY--) {
                    Block block = world.getBlockAt(x, checkY, z);
                    if (block.getType().isSolid() && !block.getType().isTransparent()) {
                        return new Location(world, x + 0.5, checkY + 1.1, z + 0.5);
                    }
                }

                // Если не нашли блок, возвращаем оригинальную точку на земле
                return new Location(world, x + 0.5, world.getMinHeight() + 1, z + 0.5);
            }

            private void moveRaysToTarget() {
                double progress = ticks / (double) durationTicks;

                for (int i = 0; i < 4; i++) {
                    if (rayEnds[i] != null) {
                        // Интерполяция между начальной и конечной точкой
                        Location newPoint = startPoint.clone().add(
                                (rayEnds[i].getX() - startPoint.getX()) * progress,
                                (rayEnds[i].getY() - startPoint.getY()) * progress,
                                (rayEnds[i].getZ() - startPoint.getZ()) * progress
                        );
                        currentPoints[i] = newPoint;
                    }
                }
            }

            private void drawRays() {
                for (int i = 0; i < 4; i++) {
                    if (currentPoints[i] != null && rayEnds[i] != null) {
                        drawSingleRay(startPoint, currentPoints[i], i);
                    }
                }
            }

            private void drawSingleRay(Location start, Location end, int rayIndex) {
                World world = start.getWorld();
                Vector direction = end.toVector().subtract(start.toVector());
                double distance = start.distance(end);
                int particles = (int) (distance * 5); // Плотность частиц

                // Цвета для разных лучей
                Particle particleType;
                switch (rayIndex) {
                    case 0: particleType = Particle.ELECTRIC_SPARK; break; // Красный
                    case 1: particleType = Particle.ELECTRIC_SPARK; break; // Синий
                    case 2: particleType = Particle.ELECTRIC_SPARK; break; // Желтый
                    case 3: particleType = Particle.ELECTRIC_SPARK; break; // Зеленый
                    default: particleType = Particle.CRIT;
                }

                // Рисуем луч частицами
                for (int j = 0; j <= particles; j++) {
                    double ratio = (double) j / particles;
                    Location particleLoc = start.clone().add(direction.clone().multiply(ratio));

                    // Разные эффекты для разных частиц
                    switch (particleType) {
                        case DUST:
                            world.spawnParticle(particleType, particleLoc, 1,
                                    new Particle.DustOptions(Color.fromRGB(255, 50, 50), 1.0f));
                            break;
                        case DUST_COLOR_TRANSITION:
                            world.spawnParticle(particleType, particleLoc, 1,
                                    new Particle.DustTransition(
                                            Color.fromRGB(50, 50, 255),
                                            Color.fromRGB(100, 100, 255),
                                            1.0f));
                            break;
                        default:
                            world.spawnParticle(particleType, particleLoc, 1, 0, 0, 0, 0);
                    }
                }

                // Эффект на конце луча
                world.spawnParticle(Particle.FLAME, end, 3, 0.1, 0.1, 0.1, 0.05);
                world.spawnParticle(Particle.SMOKE, end, 2, 0.05, 0.05, 0.05, 0.02);
            }

            private void createFinalEffect(Location center, Location[] ends) {
                World world = center.getWorld();

                // Эффект в конечных точках
                for (Location end : ends) {
                    if (end != null) {

                        world.spawnParticle(Particle.EXPLOSION_EMITTER, end, 5, 0.3, 0.3, 0.3, 0.1);
                        world.spawnParticle(Particle.FLASH, end, 1, 0, 0, 0, 0);
                        world.playSound(end, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.5f, 1.5f);
                        WitherSkeleton witherskeleton = world.spawn(end, WitherSkeleton.class);
                        witherskeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,Integer.MAX_VALUE,2));
                        witherskeleton.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,Integer.MAX_VALUE,2));
                        witherskeleton.setPersistent(true);
                        witherskeleton.setRemoveWhenFarAway(false);
                        witherskeleton.getEquipment().setItemInMainHand(Items.getSword());
                        witherskeleton.getEquipment().setBoots(Items.getBoots());
                        witherskeleton.getEquipment().setChestplate(Items.getChesplayte());
                        witherskeleton.getEquipment().setLeggings(Items.getLeggins());
                        witherskeleton.getEquipment().setHelmet(Items.getHelmet());
                        witherSkeletons.add(witherskeleton);
                    }
                }
                warden = center.getWorld().spawn(center, Warden.class);
                warden.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,Integer.MAX_VALUE,3));
                warden.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,Integer.MAX_VALUE,1));
                warden.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,Integer.MAX_VALUE,3));

                // Центральный эффект
                world.spawnParticle(Particle.DRAGON_BREATH, center, 20, 0.5, 0.5, 0.5, 0.2);
                world.playSound(center, Sound.ITEM_TRIDENT_THUNDER, 0.7f, 1.8f);
            }
        }.runTaskTimer(SpecialEvents.getInstance(), 0L, 1L);
    }

    public static List<String> getStoryParts() {
        return storyParts;
    }

    public void createBeamBetweenPoints(Location start, Location end, Particle particle, int durationTicks, double particleDensity) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks++ >= durationTicks) {
                    createBeamEndEffect(end);
                    this.cancel();
                    return;
                }

                // Рисуем луч между точками
                drawBeam(start, end, particle, particleDensity);

                // Эффекты на концах луча
                if (ticks % 5 == 0) {
                    createBeamEndEffect(start);
                    createBeamEndEffect(end);
                }

                // Звуковые эффекты
                if (ticks % 20 == 0) {
                    playBeamSound(start);
                    playBeamSound(end);
                }
            }

        }.runTaskTimer(SpecialEvents.getInstance(), 0L, 1L);
    }

    private void drawBeam(Location start, Location end, Particle particle, double density) {
        World world = start.getWorld();
        Vector direction = end.toVector().subtract(start.toVector());
        double distance = start.distance(end);
        int particles = (int) (distance * density);

        for (int i = 0; i <= particles; i++) {
            double ratio = (double) i / particles;
            Location point = start.clone().add(direction.clone().multiply(ratio));

            // Основные частицы луча
            world.spawnParticle(particle, point, 1, 0, 0, 0, 0);

            // Дополнительные эффекты для красоты
            if (i % 10 == 0) {
                world.spawnParticle(Particle.ELECTRIC_SPARK, point, 1, 0.1, 0.1, 0.1, 0.05);
            }
        }
    }

    private void createBeamEndEffect(Location location) {
        World world = location.getWorld();

        // Эффекты на концах луча
        world.spawnParticle(Particle.FLAME, location, 5, 0.2, 0.2, 0.2, 0.05);
        world.spawnParticle(Particle.SMOKE, location, 3, 0.1, 0.1, 0.1, 0.02);
        world.spawnParticle(Particle.END_ROD, location, 2, 0.15, 0.15, 0.15, 0.01);
    }

    private void playBeamSound(Location location) {
        location.getWorld().playSound(location, Sound.BLOCK_BEACON_AMBIENT, 0.4f, 1.8f);
        location.getWorld().playSound(location, Sound.ENTITY_ENDER_EYE_LAUNCH, 0.3f, 2.0f);
    }

    public void createBeamBetweenPoints(Location start, Location end) {
        createBeamBetweenPoints(start, end, Particle.END_ROD, 100, 3.0);
    }

    public void createBeamBetweenPoints(Location start, Location end, int durationTicks) {
        createBeamBetweenPoints(start, end, Particle.END_ROD, durationTicks, 3.0);
    }

    public void createBeamBetweenPoints(Location start, Location end, Particle particle) {
        createBeamBetweenPoints(start, end, particle, 100, 3.0);
    }
    public static Warden getWarden(){
        return warden;
    }
    public static void AddEffects(Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE,1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE,1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE,1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE,1));
    }
}
