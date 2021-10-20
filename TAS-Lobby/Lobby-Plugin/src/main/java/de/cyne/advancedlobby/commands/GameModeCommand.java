package de.cyne.advancedlobby.commands;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.locale.Locale;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameModeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command is available for players only.");
            return true;
        }
        Player p = (Player) sender;
        if (p.hasPermission("advancedlobby.commands.gamemode")) {
            if (args.length == 0) {
                p.sendMessage(Locale.COMMAND_GAMEMODE_USAGE.getMessage(p));
                return true;
            }
            if (args.length == 1) {
                GameMode gameMode;
                String mode = args[0].toLowerCase();

                switch (mode) {
                    case ("0"):
                    case ("survival"):
                        gameMode = GameMode.SURVIVAL;
                        break;
                    case ("1"):
                    case ("creative"):
                        gameMode = GameMode.CREATIVE;
                        break;
                    case ("2"):
                    case ("adventure"):
                        gameMode = GameMode.ADVENTURE;
                        break;
                    case ("3"):
                    case ("spectator"):
                        gameMode = GameMode.SPECTATOR;
                        break;
                    default:
                        gameMode = null;
                        break;
                }
                if (gameMode == null) {
                    p.sendMessage(Locale.COMMAND_GAMEMODE_USAGE.getMessage(p));
                    return true;
                }
                p.setGameMode(gameMode);
                p.sendMessage(Locale.COMMAND_GAMEMODE_SWITCH.getMessage(p).replace("%gamemode%", gameMode.name()));
                return true;
            }
            if (args.length == 2) {
                Player target = Bukkit.getPlayerExact(args[1]);

                if (target == null) {
                    p.sendMessage(Locale.PLAYER_NOT_FOUND.getMessage(p).replace("%player%", args[1]));
                    return true;
                }

                GameMode gameMode;
                String mode = args[0];

                switch (mode) {
                    case ("0"):
                        gameMode = GameMode.SURVIVAL;
                        break;
                    case ("1"):
                        gameMode = GameMode.CREATIVE;
                        break;
                    case ("2"):
                        gameMode = GameMode.ADVENTURE;
                        break;
                    case ("3"):
                        gameMode = GameMode.SPECTATOR;
                        break;
                    default:
                        gameMode = null;
                        break;
                }

                if (gameMode == null) {
                    p.sendMessage(Locale.COMMAND_GAMEMODE_USAGE.getMessage(p));
                    return true;
                }

                if (target == p) {
                    p.setGameMode(gameMode);
                    p.sendMessage(Locale.COMMAND_GAMEMODE_SWITCH.getMessage(p).replace("%gamemode%", gameMode.name()));
                    return true;
                }

                target.setGameMode(gameMode);
                target.sendMessage(Locale.COMMAND_GAMEMODE_SWITCH.getMessage(p).replace("%gamemode%", gameMode.name()));

                p.sendMessage(Locale.COMMAND_GAMEMODE_SWITCH_OTHER.getMessage(p).replace("%gamemode%", gameMode.name()).replace("%player%", AdvancedLobby.getName(target)));
                return true;
            }
            p.sendMessage(Locale.COMMAND_GAMEMODE_USAGE.getMessage(p));
            return true;
        }
        p.sendMessage(Locale.NO_PERMISSION.getMessage(p));
        return true;
    }

}