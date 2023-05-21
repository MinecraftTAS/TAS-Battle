package com.minecrafttas.tasbattle.gamemode;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;

/**
 * Event listener implementation
 * @author Pancake
 */
class EventListener implements Listener {

	private ModeManagement mode;
	
	/**
	 * Initialize event listener
	 * @param mode Mode management instance
	 */
	public EventListener(ModeManagement mode) {
		this.mode = mode;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		var events = this.mode.getGameMode();
		if (events != null)
			events.playerJoin(e.getPlayer());
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		var events = this.mode.getGameMode();
		if (events != null)
			events.playerLeave(e.getPlayer());
	}

	@EventHandler
	public void onPlayerBreak(BlockBreakEvent e) {
		var events = this.mode.getGameMode();
		if (events != null)
			e.setCancelled(events.playerBreak(e.getPlayer(), e.getBlock()));
	}

	@EventHandler
	public void onPlayerPlace(BlockPlaceEvent e) {
		var events = this.mode.getGameMode();
		if (events != null)
			e.setCancelled(events.playerPlace(e.getPlayer(), e.getBlock(), e.getItemInHand(), e.getBlockAgainst()));
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		var events = this.mode.getGameMode();
		if (events != null)
			e.setCancelled(events.entityDamage(e.getEntity(), e.getDamage(), e.getCause()));
	}

	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent e) {
		var events = this.mode.getGameMode();
		if (events != null)
			e.setCancelled(events.playerDrop(e.getPlayer(), e.getItemDrop()));
	}

	@EventHandler
	public void onPlayerPickup(EntityPickupItemEvent e) {
		var events = this.mode.getGameMode();
		if (events != null)
			e.setCancelled(events.entityPickup(e.getEntity(), e.getItem()));
	}

	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent e) {
		var events = this.mode.getGameMode();
		if (events != null)
			e.setCancelled(events.playerConsume(e.getPlayer(), e.getItem()));
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		var events = this.mode.getGameMode();
		if (events != null)
			e.setCancelled(events.playerInteract(e.getPlayer(), e.getAction(), e.getClickedBlock(), e.getMaterial(), e.getItem()));
	}

	@EventHandler
	public void onClickEvent(InventoryClickEvent e) {
		var events = this.mode.getGameMode();
		if (events != null)
			e.setCancelled(events.playerClick((Player) e.getWhoClicked(), e.getClick(), e.getSlot(), e.getCurrentItem(), e.getCursor(), e.getInventory()));
	}

	@EventHandler
	public void onEntityExplosion(EntityExplodeEvent e) {
		var events = this.mode.getGameMode();
		if (events != null)
			e.setCancelled(events.entityExplosion(e.getEntity(), e.getLocation(), e.blockList()));
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		var events = this.mode.getGameMode();
		if (events != null) {
			var drops = e.getDrops();
			var newDrops = new ArrayList<>(events.playerDeath(e.getEntity(), drops));
			drops.clear();
			drops.addAll(newDrops);
		}
	}

	@EventHandler
	public void onServerTick(ServerTickStartEvent e) {
		var events = this.mode.getGameMode();
		if (events != null)
			events.serverTick();
	}
	
}
