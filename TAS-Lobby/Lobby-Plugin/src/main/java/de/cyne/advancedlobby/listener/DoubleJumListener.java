package de.cyne.advancedlobby.listener;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

public class DoubleJumListener implements Listener {
    private ArrayList<UUID> player = new ArrayList<>();

    @SuppressWarnings("deprecation")
	@EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        // TODO: JnR Support!
        boolean onJnR = false;
        if (onJnR) return;

        if ((player.isOnGround() || !player.isFlying())
                && this.player.contains(player.getUniqueId()) && player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            player.setAllowFlight(false);
            Vector vector = player.getLocation().getDirection().normalize();
            vector = vector.setY(Math.max(0.5, vector.getY())).multiply(2.5f);
            player.setVelocity(vector);
            player.setFlying(false);
            player.playSound(player.getLocation(), Sound.BLOCK_GRAVEL_STEP, 1.0f, 2.0f);
            player.setFallDistance(-9999.0f);
        }
    }

}
