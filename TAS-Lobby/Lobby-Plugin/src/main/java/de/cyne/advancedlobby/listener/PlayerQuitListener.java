package de.cyne.advancedlobby.listener;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.cosmetics.Cosmetics;
import de.cyne.advancedlobby.locale.Locale;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        e.setQuitMessage(Locale.QUIT_MESSAGE.getMessage(p).replace("%player%", AdvancedLobby.getName(p)));

        if (Cosmetics.balloons.containsKey(p)) {
            Cosmetics.balloons.get(p).remove();
        }

        AdvancedLobby.build.remove(p);
        AdvancedLobby.buildInventory.remove(p);
        AdvancedLobby.fly.remove(p);
        AdvancedLobby.playerHider.remove(p);
        AdvancedLobby.shield.remove(p);
        AdvancedLobby.silentLobby.remove(p);
    }

}
