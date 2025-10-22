package me.dark_infect.specialevents.commands;
import me.dark_infect.specialevents.classes.FishModifer.FishingSkill;
import me.dark_infect.specialevents.utils.Plugininit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FishingCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cТолько для игроков!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0 || args[0].equalsIgnoreCase("stats")) {
            showStats(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            showHelp(player);
            return true;
        }

        return false;
    }

    private void showStats(Player player) {
        int totalCatches = Plugininit.getDataManager().getTotalCatches(player);
        int successfulCatches = Plugininit.getDataManager().getSuccessfulCatches(player);
        int bestStreak = Plugininit.getDataManager().getBestStreak(player);
        int legendaryCount = Plugininit.getDataManager().getLegendaryCount(player);
        double successRate = Plugininit.getDataManager().getSuccessRate(player);

        FishingSkill skill = Plugininit.getDataManager().getSkillLevel(player);
        int currentCatches = successfulCatches;
        int progress = skill.getProgressToNext(currentCatches);

        player.sendMessage("§7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("§b§l   СТАТИСТИКА РЫБОЛОВСТВА");
        player.sendMessage("");
        player.sendMessage("  §7Уровень: " + skill.getFormattedName() +
                " §7(" + progress + "%)");
        player.sendMessage("  §7Бонус удачи: §e+" + skill.getLuckBonus() + "%");
        player.sendMessage("");
        player.sendMessage("  §7Всего попыток: §f" + totalCatches);
        player.sendMessage("  §7Успешных: §a" + successfulCatches);
        player.sendMessage("  §7Успешность: §e" + String.format("%.1f", successRate) + "%");
        player.sendMessage("");
        player.sendMessage("  §7Лучшая серия: §6" + bestStreak);
        player.sendMessage("  §7Легендарных: §6§l" + legendaryCount);
        player.sendMessage("§7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private void showHelp(Player player) {
        player.sendMessage("§7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("§b§l   ИНТЕРАКТИВНАЯ РЫБАЛКА");
        player.sendMessage("");
        player.sendMessage("  §7При поклёвке нужно быстро подсечь!");
        player.sendMessage("  §7Чем быстрее реакция - тем лучше лут");
        player.sendMessage("");
        player.sendMessage("  §e/fishing stats §7- статистика");
        player.sendMessage("  §e/fishing help §7- эта справка");
        player.sendMessage("§7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
}
