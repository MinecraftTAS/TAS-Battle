package de.cyne.advancedlobby.commands;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.locale.Locale;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalMuteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command is available for players only.");
            return true;
        }
        Player p = (Player) sender;
        if (p.hasPermission("advancedlobby.commands.globalmute")) {
            if (!AdvancedLobby.globalMute) {
                AdvancedLobby.globalMute = true;
                for (Player players : Bukkit.getOnlinePlayers()) {
                    players.sendMessage(Locale.COMMAND_GLOBALMUTE_ENABLE.getMessage(p).replace("%player%", AdvancedLobby.getName(p)));
                }
                return true;
            }
            AdvancedLobby.globalMute = false;
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.sendMessage(Locale.COMMAND_GLOBALMUTE_DISABLE.getMessage(p).replace("%player%", AdvancedLobby.getName(p)));
            }
            return true;
        }
        p.sendMessage(Locale.NO_PERMISSION.getMessage(p));
        return true;
    }

}