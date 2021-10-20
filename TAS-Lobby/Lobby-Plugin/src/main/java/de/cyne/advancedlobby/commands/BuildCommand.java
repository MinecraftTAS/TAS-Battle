package de.cyne.advancedlobby.commands;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.locale.Locale;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command is available for players only.");
            return true;
        }
        Player p = (Player) sender;
        if (p.hasPermission("advancedlobby.commands.build")) {
            if (!AdvancedLobby.build.contains(p)) {
                AdvancedLobby.build.add(p);
                p.sendMessage(Locale.COMMAND_BUILD_JOIN.getMessage(p));

                p.setGameMode(GameMode.CREATIVE);

                AdvancedLobby.buildInventory.put(p, p.getInventory().getContents());
                p.getInventory().clear();
                return true;
            }
            AdvancedLobby.build.remove(p);
            p.sendMessage(Locale.COMMAND_BUILD_LEAVE.getMessage(p));

            GameMode gameMode;
            String mode = AdvancedLobby.cfg.getString("player_join.gamemode");

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
                    gameMode = GameMode.SURVIVAL;
                    break;
            }

            p.setGameMode(gameMode);

            p.getInventory().clear();
            p.getInventory().setContents(AdvancedLobby.buildInventory.get(p));
            return true;
        }
        p.sendMessage(Locale.NO_PERMISSION.getMessage(p));
        return true;
    }

}