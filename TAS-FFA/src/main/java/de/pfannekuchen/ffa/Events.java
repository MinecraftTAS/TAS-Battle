package de.pfannekuchen.ffa;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;

import net.kyori.adventure.text.Component;

/**
 * Simple events for the FFA Plugin
 * @author Pancake
  */
public class Events implements Listener {

	@EventHandler public void onPlayerBlockBreak(BlockBreakEvent e) { e.setCancelled(Game.shouldAllowInteraction(e.getPlayer())); }
	@EventHandler public void onPlayerBlockPlace(BlockPlaceEvent e) { e.setCancelled(Game.shouldAllowInteraction(e.getPlayer())); }
	@EventHandler public void onPlayerDamage(EntityDamageEvent e) { e.setCancelled(Game.shouldAllowInteraction(e.getEntity())); }
	@EventHandler public void onPlayerDrop(PlayerDropItemEvent e) { e.setCancelled(Game.shouldAllowInteraction(e.getPlayer())); }
	@EventHandler public void onPlayerAdvancement(PlayerAdvancementCriterionGrantEvent e) { e.setCancelled(true); }
	@EventHandler public void onPlayerPickup(EntityPickupItemEvent e) { e.setCancelled(!Game.isRunning); }
	@EventHandler public void onPlayerConsume(PlayerItemConsumeEvent e) { e.setCancelled(Game.shouldAllowInteraction(e.getPlayer())); }
	@EventHandler public void onInteractEvent(PlayerInteractEvent e) throws Exception { Game.onInteract(e.getPlayer(), e.getItem(), e.getAction()); }
	@EventHandler public void onInteractEvent2(PlayerInteractEvent e) { e.setCancelled(Game.shouldAllowInteraction(e.getPlayer())); }
	@EventHandler public void onInteractEvent3(InventoryClickEvent e) { e.setCancelled(Game.shouldAllowInteraction(e.getWhoClicked())); }
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		e.joinMessage(null);
		Game.onJoin(e.getPlayer());
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteractEvent4(PlayerInteractEvent e) throws Exception { 
		if (e.getClickedBlock() != null)
			if (e.getClickedBlock().getType() == Material.CHEST)
				e.setCancelled(true);
	}
	@EventHandler
	public void onTntExplode(EntityExplodeEvent e) {
		new ArrayList<>(e.blockList()).forEach(c -> {
			if (c.getType() == Material.CHEST || c.getType() == Material.TRAPPED_CHEST)
				e.blockList().remove(c);
		});
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
	@EventHandler 
	public void onInventoryClickEvent(InventoryClickEvent e) {
		Game.onInventory((Player) e.getWhoClicked(), e.getAction(), e.getCurrentItem(), e.getCursor(), e.getInventory(), e.getClickedInventory(), e.getSlot());
	}
	@EventHandler
	public void onLateJoin(PlayerJoinEvent e) {
		FFA.queuedPlayers.add(e.getPlayer().getUniqueId());
		new Thread(() -> {
			try {
				Thread.sleep(8000);
			} catch (Exception e2) {
				
			}
			if (FFA.queuedPlayers.contains(e.getPlayer().getUniqueId()) && e.getPlayer().isOnline()) {
				FFA.queuedPlayers.remove(e.getPlayer().getUniqueId());
				new BukkitRunnable() {
					@Override
					public void run() {
						e.getPlayer().kick(Component.text("Login Failed."));
					}
				}.runTask(FFA.instance());
			}
		}).start();
	}
	
}
