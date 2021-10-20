package de.cyne.advancedlobby.commands;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.locale.Locale;
import de.cyne.advancedlobby.misc.LocationManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobbyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command is available for players only.");
            return true;
        }
        Player p = (Player) sender;

        if (AdvancedLobby.lobbyWorlds.contains(p.getWorld())) {
            p.sendMessage(Locale.COMMAND_LOBBY_ALREADY_IN_LOBBY.getMessage(p));
            return true;
        }

        Location location = LocationManager.getLocation(AdvancedLobby.cfg.getString("spawn_location"));
        if (location != null) {
            p.teleport(location);
        }

        p.sendMessage(Locale.COMMAND_LOBBY_TELEPORT.getMessage(p));
        return true;
    }

}