package de.cyne.advancedlobby.listener;

import de.cyne.advancedlobby.AdvancedLobby;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;

public class LeavesDecayListener implements Listener {

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        if (!AdvancedLobby.multiWorld_mode | AdvancedLobby.lobbyWorlds.contains(e.getBlock().getWorld())) {
            e.setCancelled(true);
        }
    }

}
