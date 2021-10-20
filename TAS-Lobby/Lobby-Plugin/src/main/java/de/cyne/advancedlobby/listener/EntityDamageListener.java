package de.cyne.advancedlobby.listener;

import de.cyne.advancedlobby.AdvancedLobby;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (!AdvancedLobby.multiWorld_mode | AdvancedLobby.lobbyWorlds.contains(p.getWorld())) {
                e.setCancelled(true);
            }
        } else if (AdvancedLobby.cfg.getBoolean("disable_mob_damage")) {
            if (!AdvancedLobby.multiWorld_mode | AdvancedLobby.lobbyWorlds.contains(e.getEntity().getWorld())) {
                e.setCancelled(true);
            }
        }

    }

}
