package de.cyne.advancedlobby.commands;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.locale.Locale;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportAllCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command is available for players only.");
            return true;
        }
        Player p = (Player) sender;
        if (p.hasPermission("advancedlobby.commands.teleportall")) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (players != p) {
                    AdvancedLobby.playSound(p, p.getLocation(), "commands.teleportall_command");
                    if (!AdvancedLobby.silentLobby.contains(players)) {
                        players.teleport(p);
                        AdvancedLobby.playSound(players, p.getLocation(), "commands.teleportall_command");
                        players.sendMessage(Locale.COMMAND_TELEPORTALL_TELEPORT.getMessage(p).replace("%player%", AdvancedLobby.getName(p)));
                    }
                }
            }
            p.sendMessage(Locale.COMMAND_TELEPORTALL_TELEPORT.getMessage(p).replace("%player%", AdvancedLobby.getName(p)));
            return true;
        }
        p.sendMessage(Locale.NO_PERMISSION.getMessage(p));
        return true;
    }

}
