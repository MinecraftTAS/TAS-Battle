package de.pfannekuchen.knockffa;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;

import net.kyori.adventure.text.Component;

/**
 * Simple events for the FFA Plugin
 * @author Pancake
  */
public class Events implements Listener {

	@EventHandler public void onPlayerBlockBreak(BlockBreakEvent e) { e.setCancelled(true); }
	@EventHandler public void onPlayerDrop(PlayerDropItemEvent e) { e.setCancelled(true); }
	@EventHandler public void onPlayerAdvancement(PlayerAdvancementCriterionGrantEvent e) { e.setCancelled(true); }
	@EventHandler public void onPlayerPickup(EntityPickupItemEvent e) { e.setCancelled(true); }
	@EventHandler 
	public void onPlayerMove(PlayerMoveEvent e) { 
		if (e.getTo().getY() < 50) {
			e.getPlayer().damage(9999);
		}
		e.getPlayer().setSneaking(false);
	}
	@EventHandler 
	public void onPlayerVelocity(PlayerVelocityEvent e) { 
		e.setVelocity(e.getVelocity().clone().multiply(1.5f).setY(e.getVelocity().getY()));
	}
	@EventHandler 
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e.getDamage() < 18) e.setDamage(0.0);
		if (e.getEntity().getLocation().getY() > 94)
			e.setCancelled(true);
	}
	@EventHandler 
	public void onPlayerBlockPlace(BlockPlaceEvent e) {
		if (e.getBlock().getLocation().getY() > 94) {
			e.setCancelled(true);
			return;
		}
		Game.onBlockPlace(e.getPlayer(), e.getBlock().getLocation());
	}
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		e.joinMessage(null);
		Game.onJoin(e.getPlayer());
		Game.onSpawn(e.getPlayer());
	}
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		e.quitMessage(null);
		Game.onQuit(e.getPlayer());
	}
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent e) {
		e.deathMessage(null);
		e.setKeepInventory(true);
		e.setKeepLevel(true);
		Game.onDeath(e.getEntity());
	}
	@EventHandler
	public void onEarlyPlayerRespawnEvent(PlayerRespawnEvent e) {
		e.setRespawnLocation(e.getPlayer().getWorld().getSpawnLocation());
	}
	@EventHandler
	public void onPlayerRespawnEvent(PlayerPostRespawnEvent e) {
		Game.onSpawn(e.getPlayer());
	}
	@EventHandler
	public void onLateJoin(PlayerJoinEvent e) {
		KnockFFA.queuedPlayers.add(e.getPlayer().getUniqueId());
		new Thread(() -> {
			try {
				Thread.sleep(8000);
			} catch (Exception e2) {
				
			}
			if (KnockFFA.queuedPlayers.contains(e.getPlayer().getUniqueId()) && e.getPlayer().isOnline()) {
				KnockFFA.queuedPlayers.remove(e.getPlayer().getUniqueId());
				new BukkitRunnable() {
					@Override
					public void run() {
						e.getPlayer().kick(Component.text("Login Failed."));
					}
				}.runTask(KnockFFA.instance());
			}
		}).start();
	}
	
}
