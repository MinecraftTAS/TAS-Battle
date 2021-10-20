package de.cyne.advancedlobby.listener;

import de.cyne.advancedlobby.AdvancedLobby;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntityListener implements Listener {

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (!AdvancedLobby.multiWorld_mode | AdvancedLobby.lobbyWorlds.contains(p.getWorld())) {
            if (e.getRightClicked().getType() == EntityType.ITEM_FRAME && !AdvancedLobby.build.contains(p)) {
                e.setCancelled(true);
            }
            if (p.getInventory().getItemInMainHand().getType() == Material.NAME_TAG && !AdvancedLobby.build.contains(p)) {
                e.setCancelled(true);
            }
        }
    }

}
