package de.pfannekuchen.ffa;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.text.Component;

/**
 * Simple events for the FFA Plugin
 * @author Pancake
  */
public class Events implements Listener {

	/** Map that holds all players kit votes */
	public static HashMap<UUID, String> kits = new HashMap<>();
	/** Task when the game is starting */
	private static BukkitRunnable startingTask;
	/** Whether the game is actually running */
	private static boolean isRunning;
	
	private static Events instance;
	public Events() { Events.instance = this; }
	public static Events instance() { return instance; }

	@EventHandler public void onPlayerBlockBreak(BlockBreakEvent e) { if (!e.getPlayer().isOp() && !isRunning) e.setCancelled(true); }
	@EventHandler public void onPlayerBlockPlace(BlockPlaceEvent e) { if (!e.getPlayer().isOp() && !isRunning) e.setCancelled(true); }
	@EventHandler public void onPlayerDamage(EntityDamageEvent e) { if (!e.getEntity().isOp() && !isRunning) e.setCancelled(true); }
	@EventHandler public void onPlayerDrop(PlayerDropItemEvent e) { if (!e.getPlayer().isOp() && !isRunning) e.setCancelled(true); }
	
	/**
	 * Whenever a player join, play a sound, send them a Message and start the game if can
	 * @param e Join Event
	 */
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.sendMessage(Component.text("\u00A7b\u00bb \u00A7a" + p.getName() + "\u00A77 has joined the game."));
		p.sendActionBar(Component.text("\u00A7cLC to view a kit. RC to vote a kit."));
		e.joinMessage(null);
	}
	
	/**
	 * Clear a player from the game or vote list if they leave the server
	 */
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		e.quitMessage(null);
		if (kits.containsKey(e.getPlayer().getUniqueId())) kits.remove(e.getPlayer().getUniqueId());
		if (startingTask != null) {
			int playersLeft = Bukkit.getOnlinePlayers().size() - 1;
			if (playersLeft < 2) {
				/* Cancel the game starting */
				startingTask.cancel();
				startingTask = null;
				Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The game will not start."));
			}
		}
	}
	
	/**
	 * Whenever a player selects a kit with RC
	 */
	public void onPlayerVoteKitEvent() {
		
	}
	
	/**
	 * Whenever a kit gets selected, a countdown of 10 seconds will start, after which the players will be spread across the map.
	 */
	public void onKitSelectedEvent() {
		if (Bukkit.getOnlinePlayers().size() >= 2 /* Check if the game is startable */)  {
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The selected kit is: \u00A7b" + FFA.selectedKitName + "\u00A77."));
			Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The game will start in 10 seconds."));
			startingTask = new BukkitRunnable() {
				@Override public void run() {
					/* Start the game */
					try {
						Bukkit.broadcast(Component.text("\u00A7b\u00bb \u00A77The game has started."));
						Random rng = new Random();
						isRunning = true;
						for (Player p : Bukkit.getOnlinePlayers()) {
							Serialization.base64ToPlayerInventory(p, FFA.serializedSelectedKit); // load kit
							/* Teleport the player */
							while (true) {
								int x = rng.nextInt(801) - 400;
								int z = rng.nextInt(801) - 400;
								Block b = p.getWorld().getHighestBlockAt(x, z);
								if (b != null && b.getType() != Material.AIR) {
									p.teleport(new Location(p.getWorld(), x, b.getY() + 1, z));
									break;
								}
							}
						}
					} catch (Exception e) {
						Bukkit.broadcast(Component.text("\u00A7b\u00bb	 \u00A7cAn error occured whilst trying to start the game!"));
						e.printStackTrace();
					}
					startingTask = null;
				}
			};
			startingTask.runTaskLater(FFA.instance(), 10*20L);
		}
	}

}
