package me.dark_infect.specialevents.commands;

import me.dark_infect.specialevents.classes.FishModifer.GUI.AdminPanelGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AdminCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public AdminCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cТолько для игроков!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("fishing.admin")) {
            player.sendMessage("§cУ вас нет прав для использования этой команды!");
            return true;
        }

        new AdminPanelGUI(plugin).openMainMenu(player);
        return true;
    }
}
