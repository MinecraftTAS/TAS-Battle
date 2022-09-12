package de.pancake.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

/**
 * Event listener implementation
 * @author Pancake
 */
class EventListener implements Listener {

	/**
	 * All registered event classes
	 */
	static Map<String, Events> phases = new HashMap<>();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		var events = phases.get(CommonTASBattle.PHASE);
		if (events != null)
			events.playerJoin(e.getPlayer());
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		var events = phases.get(CommonTASBattle.PHASE);
		if (events != null)
			events.playerLeave(e.getPlayer());
	}

	@EventHandler
	public void onPlayerBreak(BlockBreakEvent e) {
		var events = phases.get(CommonTASBattle.PHASE);
		if (events != null)
			e.setCancelled(events.playerBreak(e.getPlayer(), e.getBlock()));
	}

	@EventHandler
	public void onPlayerPlace(BlockPlaceEvent e) {
		var events = phases.get(CommonTASBattle.PHASE);
		if (events != null)
			e.setCancelled(events.playerPlace(e.getPlayer(), e.getBlock(), e.getItemInHand(), e.getBlockAgainst()));
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		var events = phases.get(CommonTASBattle.PHASE);
		if (events != null)
			e.setCancelled(events.entityDamage(e.getEntity(), e.getDamage(), e.getCause()));
	}

	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent e) {
		var events = phases.get(CommonTASBattle.PHASE);
		if (events != null)
			e.setCancelled(events.playerDrop(e.getPlayer(), e.getItemDrop()));
	}

	@EventHandler
	public void onPlayerPickup(EntityPickupItemEvent e) {
		var events = phases.get(CommonTASBattle.PHASE);
		if (events != null)
			e.setCancelled(events.entityPickup(e.getEntity(), e.getItem()));
	}

	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent e) {
		var events = phases.get(CommonTASBattle.PHASE);
		if (events != null)
			e.setCancelled(events.playerConsume(e.getPlayer(), e.getItem()));
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		var events = phases.get(CommonTASBattle.PHASE);
		if (events != null)
			e.setCancelled(events.playerInteract(e.getPlayer(), e.getAction(), e.getClickedBlock(), e.getMaterial(), e.getItem()));
	}

	@EventHandler
	public void onClickEvent(InventoryClickEvent e) {
		var events = phases.get(CommonTASBattle.PHASE);
		if (events != null)
			e.setCancelled(events.playerClick((Player) e.getWhoClicked(), e.getClick(), e.getSlot(), e.getCurrentItem(), e.getCursor(), e.getInventory()));
	}

	@EventHandler
	public void onEntityExplosion(EntityExplodeEvent e) {
		var events = phases.get(CommonTASBattle.PHASE);
		if (events != null)
			e.setCancelled(events.entityExplosion(e.getEntity(), e.getLocation()));
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		var events = phases.get(CommonTASBattle.PHASE);
		if (events != null) {
			var drops = e.getDrops();
			var newDrops = new ArrayList<>(events.playerDeath(e.getEntity(), drops));
			drops.clear();
			drops.addAll(newDrops);
		}
	}

}
