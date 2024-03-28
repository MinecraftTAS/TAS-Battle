package com.minecrafttas.tasbattle.bedwars.components;

import java.util.ArrayList;
import java.util.List;

import com.minecrafttas.tasbattle.TASBattleGameserver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

/**
 * Physics of all explosive items
 */
public class ExplosivePhysics implements Listener {

	private final TASBattleGameserver plugin;
	private final PlacementRules placementRules;
	private final List<Player> timeout;
	
	/**
	 * Initialize explosive physics
	 * @param plugin Plugin
	 * @param placementRules Placement rules
	 */
	public ExplosivePhysics(TASBattleGameserver plugin, PlacementRules placementRules) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
		this.placementRules = placementRules;
		this.timeout = new ArrayList<>();
	}
	
	/**
	 * Ignite tnt on place
	 * @param e Event
	 */
	@EventHandler
	public void onTntPlace(BlockPlaceEvent e) {
		var block = e.getBlock();
		if (block.getType() == Material.TNT) {
			block.setType(Material.AIR);
			block.getWorld().spawn(block.getLocation().add(0.5, 0.5, 0.5), TNTPrimed.class).setFuseTicks(50);
		}
	}
	
	/**
	 * Reduce explosion damage to a maximum of 1 heart
	 * @param e Event
	 */
	@EventHandler
	public void onExplosionDamage(EntityDamageEvent e) {
		if (e.getCause() == DamageCause.ENTITY_EXPLOSION || e.getCause() == DamageCause.BLOCK_EXPLOSION || e.getCause() == DamageCause.CONTACT)
			e.setDamage(Math.min(e.getDamage() / 2, 2));
	}
	
	/**
	 * Update blocks destroyed by explosives
	 * @param e Event
	 */
	@EventHandler
	public void onBlockExplode(EntityExplodeEvent e) {
		e.setYield(0.3f);
		var blockList = e.blockList();
		blockList.removeIf(block -> !this.placementRules.isPlacedByPlayer(block.getLocation()) || (e.getEntityType() == EntityType.FIREBALL && block.getType() == Material.END_STONE));
	}
	
	/**
	 * Update player velocity on explosion
	 * @param e Event
	 */
	@EventHandler
	public void onPlayerExplode(EntityExplodeEvent e) {
		var explosion = e.getEntity();
		for (var player : explosion.getWorld().getNearbyEntitiesByType(Player.class, e.getLocation(), 10.0)) {
			var vec = player.getLocation().add(0, 1, 0).toVector().subtract(e.getLocation().toVector());
			var len = vec.length();
			if (e.getEntityType() == EntityType.PRIMED_TNT)
				player.setVelocity(player.getVelocity().add(vec.normalize().multiply(4.0 / len).divide(new Vector(1, 7, 1))));
			else
				player.setVelocity(player.getVelocity().add(vec.normalize().multiply(Math.min(3.5, 8.0 / len)).divide(new Vector(3, 3, 3))));
		}
	}
	
	
	/**
	 * Throw fireballs on right click
	 * @param e Event
	 */
	@EventHandler
	public void onFireballInteract(PlayerInteractEvent e) {
		var item = e.getItem();
		var player = e.getPlayer();
		
		// check for fireball throw interaction
		if (item == null || item.getType() != Material.FIRE_CHARGE || (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK))
			return;
		
		e.setCancelled(true);

		// cancel on cooldown
		if (this.timeout.contains(player))
			return;
		
		// use item
		if (item.getAmount() == 1)
			player.getInventory().remove(item);
		else
			item.setAmount(item.getAmount() - 1);
		
		var fireball = player.launchProjectile(Fireball.class, player.getLocation().getDirection());
		fireball.setVelocity(fireball.getVelocity().multiply(0.2));
		fireball.setIsIncendiary(false);
		fireball.setYield(1.25f);

		// add cooldown
		this.timeout.add(player);
		Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.timeout.remove(player), 10L);
	}
	
}
