package de.pfannekuchen.survivalgames;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;

/**
 * Simple events for the FFA Plugin
 * @author Pancake
  */
public class Events implements Listener {

	public ArrayList<Location> blocks = new ArrayList<>();
	public ArrayList<Location> chests = new ArrayList<Location>();
	
	@EventHandler public void onPlayerBlockBreak(BlockBreakEvent e) { e.setCancelled(!blocks.contains(e.getBlock().getLocation())); }
	@EventHandler public void onPlayerBlockPlace(BlockPlaceEvent e) { blocks.add(e.getBlock().getLocation()); }
	@EventHandler public void onPlayerAdvancement(PlayerAdvancementCriterionGrantEvent e) { e.setCancelled(true); }
	@EventHandler public void onPlayerPickup(EntityPickupItemEvent e) { e.setCancelled(!Game.isRunning); }
	@EventHandler public void onPlayerDamage(EntityDamageEvent e) { e.setCancelled(Game.shouldAllowInteraction(e.getEntity())); }
	@EventHandler public void onPlayerDrop(PlayerDropItemEvent e) { e.setCancelled(Game.shouldAllowInteraction(e.getPlayer())); }
	@EventHandler public void onPlayerConsume(PlayerItemConsumeEvent e) { e.setCancelled(Game.shouldAllowInteraction(e.getPlayer())); }
	@EventHandler public void onInteractEvent(PlayerInteractEvent e) throws Exception { Game.onInteract(e.getPlayer(), e.getItem(), e.getAction()); }
	@EventHandler public void onInteractEvent2(PlayerInteractEvent e) { e.setCancelled(Game.shouldAllowInteraction(e.getPlayer())); }
	@EventHandler public void onInteractEvent3(InventoryClickEvent e) { e.setCancelled(Game.shouldAllowInteraction(e.getWhoClicked())); }
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		e.joinMessage(null);
		Game.onJoin(e.getPlayer());
	}
	@EventHandler 
	public void onShieldDisable(EntityDamageByEntityEvent e) { 
		if (e.getDamager() == null) return;
		if (e.getEntity() == null) return;
		try {
			Material m = ((Player) e.getDamager()).getInventory().getItemInMainHand().getType();
			if (e.getEntity().getType() != EntityType.PLAYER || e.getDamager().getType() != EntityType.PLAYER) return;
			if (((Player) e.getEntity()).getInventory().getItemInOffHand().getType() == Material.SHIELD)
				if (m == Material.DIAMOND_AXE || m == Material.GOLDEN_AXE || m == Material.IRON_AXE || m == Material.WOODEN_AXE || m == Material.STONE_AXE) {
					if (((Player) e.getEntity()).getCooldown(Material.SHIELD) == 0 && ((Player) e.getEntity()).isBlocking()) {
						e.getDamager().playSound(Sound.sound(org.bukkit.Sound.ITEM_SHIELD_BREAK, Source.MASTER, 1.0f, 1.0f));
					}
				}
		} catch (Exception e1) {
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteractEvent4(PlayerInteractEvent e) throws Exception { 
		if (e.getClickedBlock() != null) {
			if (e.getClickedBlock().getType() == Material.CHEST) {
				if (!chests.contains(e.getClickedBlock().getLocation())) {
					chests.add(e.getClickedBlock().getLocation());
					// refill loot
					Game.fillLoot((Chest) e.getClickedBlock().getState());
				}
			}
			if (e.getClickedBlock().getType() == Material.BARREL) {
				if (!chests.contains(e.getClickedBlock().getLocation())) {
					chests.add(e.getClickedBlock().getLocation());
					// refill loot
					Game.fillLoot((Barrel) e.getClickedBlock().getState());
				}
			}
		}
	}
	@EventHandler
	public void onTntExplode(EntityExplodeEvent e) {
		new ArrayList<>(e.blockList()).forEach(c -> {
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
	public void onLateJoin(PlayerJoinEvent e) {
		SurvivalGames.queuedPlayers.add(e.getPlayer().getUniqueId());
		new Thread(() -> {
			try {
				Thread.sleep(8000);
			} catch (Exception e2) {
				
			}
			if (SurvivalGames.queuedPlayers.contains(e.getPlayer().getUniqueId()) && e.getPlayer().isOnline()) {
				SurvivalGames.queuedPlayers.remove(e.getPlayer().getUniqueId());
				new BukkitRunnable() {
					@Override
					public void run() {
						e.getPlayer().kick(Component.text("Login Failed."));
					}
				}.runTask(SurvivalGames.instance());
			}
		}).start();
	}
	
}
