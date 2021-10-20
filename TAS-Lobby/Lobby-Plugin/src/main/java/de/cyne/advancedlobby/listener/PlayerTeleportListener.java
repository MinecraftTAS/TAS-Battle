package de.cyne.advancedlobby.listener;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.cosmetics.Cosmetics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (Cosmetics.balloons.containsKey(p)) {
            if(!AdvancedLobby.multiWorld_mode | AdvancedLobby.lobbyWorlds.contains(p.getWorld())) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(AdvancedLobby.getInstance(), () -> {
                    Cosmetics.balloons.get(p).remove();
                    Cosmetics.balloons.get(p).create();
                }, 2L);
            }
        }
    }

}
