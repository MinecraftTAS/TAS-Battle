package de.cyne.advancedlobby.commands;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.locale.Locale;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command is available for players only.");
            return true;
        }
        Player p = (Player) sender;
        if (p.hasPermission("advancedlobby.commands.fly")) {
            if (args.length == 0) {
                if (!AdvancedLobby.fly.contains(p)) {
                    AdvancedLobby.fly.add(p);
                    p.setAllowFlight(true);
                    p.setFlying(true);
                    p.sendMessage(Locale.COMMAND_FLY_ENABLE.getMessage(p));
                    return true;
                }
                AdvancedLobby.fly.remove(p);
                p.setAllowFlight(false);
                p.setFlying(false);
                p.sendMessage(Locale.COMMAND_FLY_DISABLE.getMessage(p));
                return true;
            }
            if (args.length == 1) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    p.sendMessage(Locale.PLAYER_NOT_FOUND.getMessage(p).replace("%player%", args[0]));
                    return true;
                }

                if (target == p) {
                    if (!AdvancedLobby.fly.contains(p)) {
                        AdvancedLobby.fly.add(p);
                        p.setAllowFlight(true);
                        p.setFlying(true);
                        p.sendMessage(Locale.COMMAND_FLY_ENABLE.getMessage(p));
                        return true;
                    }
                    AdvancedLobby.fly.remove(p);
                    p.setAllowFlight(false);
                    p.setFlying(false);
                    p.sendMessage(Locale.COMMAND_FLY_DISABLE.getMessage(p));
                    return true;
                }

                if (!AdvancedLobby.fly.contains(target)) {
                    AdvancedLobby.fly.add(target);
                    target.setAllowFlight(true);
                    target.setFlying(true);
                    target.sendMessage(Locale.COMMAND_FLY_ENABLE.getMessage(p));
                    p.sendMessage(Locale.COMMAND_FLY_ENABLE_OTHER.getMessage(p).replace("%player%", AdvancedLobby.getName(target)));
                    return true;
                }
                AdvancedLobby.fly.remove(target);
                target.setAllowFlight(false);
                target.setFlying(false);
                target.sendMessage(Locale.COMMAND_FLY_DISABLE.getMessage(p));
                p.sendMessage(Locale.COMMAND_FLY_DISABLE_OTHER.getMessage(p).replace("%player%", AdvancedLobby.getName(target)));
                return true;
            }
            p.sendMessage(Locale.COMMAND_FLY_USAGE.getMessage(p));
            return true;
        }
        p.sendMessage(Locale.NO_PERMISSION.getMessage(p));
        return true;
    }

}
