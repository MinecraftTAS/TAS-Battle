package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleGameserver;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Quick and dirty serverside spectating command
 * @author Pancake
 */
public class SpectatingCommand implements CommandExecutor {

    private TASBattleGameserver plugin;

    private Map<String, ArmorStand> entities = new HashMap<>();

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
            else if (args.length == 1) {
                var target = player.getServer().getPlayer(args[0]);
                if (target == null)
                    return true;

                // WHY IS MINECRAFT SO JANKY??
                player.setSpectatorTarget(null);
                player.teleport(target);
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> player.setSpectatorTarget(target), 2L); // spectate a tick later; just kidding 2 because that's when the player data finally arrives
            } else {
                var targets = Arrays.stream(args).map(player.getServer()::getPlayer).toList();
                var targetString = String.join(" ", args);

                var target = this.entities.get(targetString);
                if (target != null) {
                    player.setSpectatorTarget(null);
                    player.teleport(target);

                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> player.setSpectatorTarget(target), 2L);
                } else {
                    this.entities.put(targetString, target);

                    var finalTarget = player.getWorld().spawn(player.getLocation(), ArmorStand.class, armorStand -> {
                        armorStand.setInvisible(true);
                        armorStand.setInvulnerable(true);
                        armorStand.setGravity(false);
                        armorStand.setMarker(true);
                    });

                    // calculate center of triangle and teleport armor stand every tick
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
                        var center = targets.stream().filter(p -> p.isOnline() && p.getGameMode() == GameMode.SURVIVAL).map(p -> p.getLocation().toVector()).reduce((a, b) -> a.add(b)).get().multiply(1.0 / targets.size());
                        finalTarget.teleport(center.toLocation(player.getWorld()));
                    }, 0L, 1L);

                    player.setSpectatorTarget(null);
                    player.teleport(finalTarget);

                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> player.setSpectatorTarget(finalTarget), 2L);
                }

            }

        return true;
    }
}
