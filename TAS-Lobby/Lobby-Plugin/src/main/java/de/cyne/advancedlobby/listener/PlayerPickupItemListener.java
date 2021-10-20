package de.cyne.advancedlobby.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import de.cyne.advancedlobby.AdvancedLobby;

public class PlayerPickupItemListener implements Listener {

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent e) {
        LivingEntity p = e.getEntity();
        if (p instanceof Player) {
	        if (!AdvancedLobby.multiWorld_mode | AdvancedLobby.lobbyWorlds.contains(p.getWorld())) {
	            if (!AdvancedLobby.build.contains(p)) {
	                e.setCancelled(true);
	            }
	        }
        }
    }

}
