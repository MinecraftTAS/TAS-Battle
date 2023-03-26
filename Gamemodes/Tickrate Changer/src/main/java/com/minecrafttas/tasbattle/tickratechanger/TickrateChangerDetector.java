package com.minecrafttas.tasbattle.tickratechanger;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Tickrate changer detector. The goal of this class is to kick players that do not have the tickrate changer within the TAS Battle client installed.
 * When the TAS Battle client connects to the server it sends an empty plugin message to the server
 * @author Pancake
 */
class TickrateChangerDetector implements PluginMessageListener, Listener {

	/**
	 * Kick timers for pending players
	 */
	private HashMap<UUID, BukkitTask> kickTimers = new HashMap<>();

	/**
	 * Schedules a kick timer on player join
	 * @param e Player join event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		// schedule new 8s kick timer for new player
		this.kickTimers.put(e.getPlayer().getUniqueId(), new BukkitRunnable() {

			@Override
			public void run() {
				if (e.getPlayer().isOnline())
					e.getPlayer().kick(Component.text("Couldn't connect you to the game server. Please make sure you have installed the TAS Battle client properly.", NamedTextColor.RED));
			}

		}.runTaskLater(TickrateChanger.PLUGIN, 8 * 20));
	}

	/**
	 * Cancels the kick timer for a player on plugin message and
	 * updates their tickrate
	 */
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (this.kickTimers.containsKey(player.getUniqueId()))
			this.kickTimers.remove(player.getUniqueId()).cancel();

		TickrateChangerAPI.updatePlayer(player);
	}

}
