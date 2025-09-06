package me.dark_infect.specialevents.commands;

import me.dark_infect.specialevents.classes.BossAbilityManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class BossCommand implements CommandExecutor, TabCompleter {

    private static final Map<UUID, BossAbilityManager> bossPlayers = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только для игроков!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§6Команды босса:");
            player.sendMessage("§b/boss dash §7- Рывок вперед");
            player.sendMessage("§b/boss cloud §7- Смертельное облако");
            player.sendMessage("§b/boss trap §7- Ловушки под игроками");
            player.sendMessage("§b/boss toggle §7- Вкл/выкл режим босса");
            return true;
        }

        if (!bossPlayers.containsKey(player.getUniqueId()) && !args[0].equalsIgnoreCase("toggle")) {
            player.sendMessage("§cСначала активируйте режим босса: /boss toggle");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "toggle":
                toggleBossMode(player);
                break;
            case "dash":
                useDashAbility(player);
                break;
            case "cloud":
                useCloudAbility(player);
                break;
            case "trap":
                useTrapAbility(player);
                break;
            case "ball":
                onUseFireball(player);
                break;
            default:
                player.sendMessage("§cНеизвестная команда! Используйте /boss");
        }

        return true;
    }
    public void onUseFireball(Player player){
        BossAbilityManager manager = bossPlayers.get(player.getUniqueId());
        if(manager != null){
            if(manager.executeAbility("ball")){
                player.sendMessage("Fireball был заспавнен");
            }else{
                player.sendMessage("Способность на перезарядке");
            }
        }
    }

    private void toggleBossMode(Player player) {
        UUID uuid = player.getUniqueId();

        if (bossPlayers.containsKey(uuid)) {
            bossPlayers.remove(uuid);
            player.sendMessage("§cРежим босса отключен");
            player.removePotionEffect(PotionEffectType.RESISTANCE);
        } else {
            bossPlayers.put(uuid, new BossAbilityManager(player));
            player.sendMessage("§aРежим босса активирован!");
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 1000000, 1));
        }
    }

    private void useDashAbility(Player player) {
        BossAbilityManager manager = bossPlayers.get(player.getUniqueId());
        if (manager != null) {
            if (manager.executeAbility("dash")) {
                player.sendMessage("§b⚡ Рывок!");
            } else {
                player.sendMessage("§cСпособность на перезарядке!");
            }
        }
    }

    private void useCloudAbility(Player player) {
        BossAbilityManager manager = bossPlayers.get(player.getUniqueId());
        if (manager != null) {
            if (manager.executeAbility("aoecloud")) {
                player.sendMessage("§4☁️ Смертельное облако!");
            } else {
                player.sendMessage("§cСпособность на перезарядке!");
            }
        }
    }

    private void useTrapAbility(Player player) {
        BossAbilityManager manager = bossPlayers.get(player.getUniqueId());
        if (manager != null) {
            if (manager.executeAbility("aoetrap")) {
                player.sendMessage("§6⚠️ Ловушки установлены!");
            } else {
                player.sendMessage("§cСпособность на перезарядке!");
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("toggle", "dash", "cloud", "trap", "ball");
        }
        return Collections.emptyList();
    }

    public static boolean isBoss(Player player) {
        return bossPlayers.containsKey(player.getUniqueId());
    }

    public static BossAbilityManager getBossManager(Player player) {
        return bossPlayers.get(player.getUniqueId());
    }
}
