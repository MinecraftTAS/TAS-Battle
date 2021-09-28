package de.pfannekuchen.skywars;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Simple events for the Skywars Plugin
 * @author Pancake
  */
public class Events implements Listener {

	@EventHandler public void onPlayerBlockBreak(BlockBreakEvent e) { e.setCancelled(Game.shouldAllowInteraction(e.getPlayer())); }
	@EventHandler public void onPlayerBlockPlace(BlockPlaceEvent e) { e.setCancelled(Game.shouldAllowInteraction(e.getPlayer()) || e.getBlock().getType() == Material.CHEST); }
	@EventHandler public void onPlayerDamage(EntityDamageEvent e) { e.setCancelled(Game.shouldAllowInteraction(e.getEntity())); }
	@EventHandler public void onPlayerDrop(PlayerDropItemEvent e) { e.setCancelled(Game.shouldAllowInteraction(e.getPlayer())); }
	@EventHandler public void onInteractEvent(PlayerInteractEvent e) throws Exception { Game.onInteract(e.getPlayer(), e.getItem(), e.getAction()); }
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		e.joinMessage(null);
		Game.onJoin(e.getPlayer());
	}
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		e.quitMessage(null);
		Game.onQuit(e.getPlayer());
	}
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent e) {
		e.deathMessage(null);
		Game.onDeath(e.getEntity());
	}

}
