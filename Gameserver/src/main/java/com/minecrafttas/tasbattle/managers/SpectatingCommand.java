package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleGameserver;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Quick and dirty serverside spectating command
 * @author Pancake
 */
public class SpectatingCommand implements CommandExecutor {

    private TASBattleGameserver plugin;

    /**
     * Initialize spectating command
     * @param plugin Plugin
     */
    public SpectatingCommand(TASBattleGameserver plugin) {
        this.plugin = plugin;
        plugin.getCommand("orbit").setExecutor(this);
    }

    /**
     * Handle spectating command
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player && player.getGameMode() == GameMode.SPECTATOR)
            if (args.length == 0)
                player.setSpectatorTarget(null);
            else {
                var target = player.getServer().getPlayer(args[0]);
                if (target == null)
                    return true;

                // WHY IS MINECRAFT SO JANKY??
                player.setSpectatorTarget(null);
                player.teleport(target);
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> player.setSpectatorTarget(target), 2L); // spectate a tick later; just kidding 2 because that's when the player data finally arrives
            }

        return true;
    }
}
