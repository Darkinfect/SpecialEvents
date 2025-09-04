package me.dark_infect.specialevents.listeners;

import me.dark_infect.specialevents.SpecialEvents;
import me.dark_infect.specialevents.classes.FinalFigth;
import me.dark_infect.specialevents.classes.InvulnerabilityManager;
import me.dark_infect.specialevents.classes.Items;
import me.dark_infect.specialevents.utils.Chat;
import me.dark_infect.specialevents.utils.plugininit;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.lang.module.FindException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static me.dark_infect.specialevents.classes.Items.PortalActivation;

public class EyeChall implements Listener {
    private static boolean canMove = true;
    private static int countKilling = 0;
    private static int countKillChast = 0;
    private static final Player player = Bukkit.getPlayer("Dark_infect");
    private static final World overworld = SpecialEvents.getInstance().getServer().getWorld("world");
    private static final List<String> targetplayer = List.of(
            "KAKSAD",
            "Bleebi",
            "Harsoff_",
            "_firesta",
            "Xariil",
            "Mihalich"
    );
    private static final HashMap<Player, Boolean> progress = new HashMap<>();

    public static void CheckerF(){
        new BukkitRunnable(){
            @Override
            public void run(){
                List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();
                players.forEach(player -> {
                    //ChallengeOne(player);
                    //ChallengeTwo(player);
                    ChallengeThird(player);
                    //ChallengeFourth(player);
                    //ChallengeFives(player);
                    //ChallengeSix(player);
                });
            }
        }.runTaskTimer(SpecialEvents.getInstance(),0L,30*20);
    }
    @EventHandler
    public void PlayerInterract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (Objects.requireNonNull(event.getClickedBlock()).getType() != Material.END_PORTAL_FRAME) return;
        if(mainHand.getType() != Material.ENDER_EYE && offHand.getType() != Material.ENDER_EYE){
            return;
        }
        if (PortalActivation(mainHand,Items.GetLore())||PortalActivation(offHand,Items.GetLore())){
            return;
        }
        Chat.sendPluginMessage(player,"< Вместилище никак не реагирует. Странно...\n" +
                "< В чём дело? Неужели оно не предназначено для этого? Пока что...\n" +
                "< Мне очень повезло найти эту структуру, но, видимо, время ещё не пришло.");
        event.setCancelled(true);
    }



    public static void ChallengeOne(Player player){
        if (progress.containsKey(player)) return;
        if(player.getName().equals(targetplayer.get(0))){
            int diamondsMined = player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE);
            if(diamondsMined > 520){
                player.getInventory().addItem(Items.GetSpecialEnderEye());
                progress.put(player, Boolean.TRUE);
                Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " " + ChatColor.GOLD + "Выполнил испытание и получил Древнее око");
                return;
            }
        }
        return;
    }
    // поймать 100 рыбёшек
    public static void ChallengeTwo(Player player){
        if (progress.containsKey(player)) return;
        if(player.getName().equals(targetplayer.get(1))) {
            int curevill = player.getStatistic(Statistic.FISH_CAUGHT);
            Chat.debugmessage("Поймано рыб" + curevill);
            if(curevill >= 100){
                player.getInventory().addItem(Items.GetSpecialEnderEye());
                progress.put(player, Boolean.TRUE);
                Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " " + ChatColor.GOLD + "Выполнил испытание и получил Древнее око");
            }
        }
    }
    // 37 голов пиглина
    public static void ChallengeThird(Player player){
        if (progress.containsKey(player)) return;
        if(player.getName().equals(targetplayer.get(2))) {
           if(player.getInventory().contains(Material.PIGLIN_HEAD,37)){
               player.getInventory().addItem(Items.GetSpecialEnderEye());
               progress.put(player, Boolean.TRUE);
               Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " " + ChatColor.GOLD + "Выполнил испытание и получил Древнее око");
           }
        }
    }
    // убить 5 гастов в обычном мире
    public static void ChallengeFourth(Player player){
        if (progress.containsKey(player)) return;
        if(player.getName().equals(targetplayer.get(3))) {
            Chat.debugmessage("Убито гастов " + countKillChast);
            if(countKillChast >= 5){
                player.getInventory().addItem(Items.GetSpecialEnderEye());
                progress.put(player, Boolean.TRUE);
                Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " " + ChatColor.GOLD + "Выполнил испытание и получил Древнее око");
            }
        }
    }
    // 200 хряков из ада
    public static void ChallengeFives(Player player){
        if (progress.containsKey(player)) return;
        if(player.getName().equals(targetplayer.get(4))) {
            Chat.debugmessage("Убито Хряков" + countKilling);
            if(countKilling > 200){
                player.getInventory().addItem(Items.GetSpecialEnderEye());
                progress.put(player, Boolean.TRUE);
                Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " " + ChatColor.GOLD + "Выполнил испытание и получил Древнее око");
            }
        }
    }
    //выполнить достижение арбалетчик
    public static void ChallengeSix(Player player){
        if (progress.containsKey(player)) return;
        if(player.getName().equals(targetplayer.get(5))) {
            if(hasArbalisticAdvancement(player)){
                player.getInventory().addItem(Items.GetSpecialEnderEye());
                progress.put(player, Boolean.TRUE);
                Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " " + ChatColor.GOLD + "Выполнил испытание и получил Древнее око");
            };
        }
    }
    @EventHandler
    public void KillHoglins(EntityDeathEvent event){
        if(event.getDamageSource().getCausingEntity() == null) return;
        Chat.debugmessage(event.getEntity().getType().toString() + " \n " + event.getEntity().getWorld() + " \n " + event.getDamageSource().getCausingEntity().getName() + " \n " + event.getEntity().getWorld().equals(overworld));
        if(event.getEntity().getType().equals(EntityType.HOGLIN) && event.getDamageSource().getCausingEntity().getName().equals(targetplayer.get(4))){
            countKilling++;
            return;
        }
        if(event.getEntity().getType().equals(EntityType.GHAST) && event.getEntity().getWorld().equals(overworld) && event.getDamageSource().getCausingEntity().getName().equals(targetplayer.get(3))){
            countKillChast++;
            return;
        }
    }
    public static void PrintInfo(Player player){
        if(progress.containsKey(player)){
            Chat.debugmessage("Игрок выполнил всё.");
        }else if(targetplayer.contains(player.getName())){
            switch (player.getName()) {
                case "KAKSAD":
                    int diamondsMined = player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE);
                    Chat.debugmessage("KAKSAD количество добытых блоков: " + diamondsMined);
                    break;
                case "Bleebi":
                    int curevill = player.getStatistic(Statistic.FISH_CAUGHT);
                    Chat.debugmessage("Bleebi пойманных рыб: " + curevill);
                    break;
                case "Harsoff_":
                    Chat.debugmessage("Смотри инвси");
                    break;
                case "_firesta":
                    Chat.debugmessage("Арианка убила: " + countKillChast);
                    break;
                case "Xariil":
                    Chat.debugmessage("Xariil убила хряков: " + countKilling);
                    break;
                case "Mihalich":
                    Chat.debugmessage("У него достижение и тут только сам узнает");
                    break;
            }
        }
    }
    public static boolean hasArbalisticAdvancement(Player player) {
        Advancement adv = Bukkit.getAdvancement(NamespacedKey.fromString("minecraft:adventure/arbalistic"));
        if(player.getAdvancementProgress(adv).isDone()){
            return true;
        }
        return false;
    }
    @EventHandler
    public void OnDeathEvent(EntityDeathEvent event){
        if(event.getEntity().getUniqueId() == null) return;
        if(event.getEntity().getWorld().getName().equals("world_the_end") && event.getEntity().getType().equals(EntityType.WARDEN)){
            new BukkitRunnable(){
                int a = 0;
                @Override
                public void run(){
                    if(FinalFigth.getStoryParts().size()-1 == a){
                        canMove = true;
                        this.cancel();
                        return;
                    }
                    Bukkit.broadcastMessage(FinalFigth.getStoryParts().get(a));
                    a++;
                }
            }.runTaskTimer(SpecialEvents.getInstance(),0L,20 * 3);
            FinalFigth.getInstance().createBeamBetweenPoints(FinalFigth.getInstance().getCenter(),player.getLocation());
            plugininit.getInstanceInvManager().makeInvulnerable(player,120,"Падший бог");
            FinalFigth.AddEffects(player);
            canMove = false;
        }
    }
    @EventHandler
    public void onMovePlayer(PlayerMoveEvent event){
        assert player != null;
        if(player.getUniqueId().equals(event.getPlayer().getUniqueId())){
            if(!canMove){
                event.setCancelled(true);
            }
        }
    }
}
