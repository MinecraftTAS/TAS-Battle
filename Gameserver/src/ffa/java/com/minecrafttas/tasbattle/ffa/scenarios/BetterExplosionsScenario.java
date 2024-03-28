package com.minecrafttas.tasbattle.ffa.scenarios;

import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class BetterExplosionsScenario extends AbstractScenario {

	private final JavaPlugin plugin;

	public BetterExplosionsScenario(JavaPlugin plugin) {
		super("Better explosions", new String[] {"Velocity of explosions is increased", "and damage is reduced"}, Material.TNT);
		this.plugin = plugin;
	}

	@Override
	public void gameStart(List<Player> participants) {
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onPlayerExplode(EntityExplodeEvent e) {
		for (var player : e.getEntity().getWorld().getNearbyEntitiesByType(Player.class, e.getLocation(), 10.0)) {
			var vec = player.getLocation().add(0, 1, 0).toVector().subtract(e.getLocation().toVector());
			player.setVelocity(player.getVelocity().add(vec.normalize().multiply(Math.min(3.5, 8.0 / vec.length())).divide(new Vector(3, 3, 3))));
		}
	}

	private final List<UUID> invinciblePlayers = new LinkedList<>();

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if ((e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) && e.getEntity() instanceof Player player)
			if (!this.invinciblePlayers.contains(player.getUniqueId()) && player.getHealth() > 1.0) {
				if (e.getDamage() >= 2.0)
					e.setDamage(Math.max(2, e.getDamage() / 5));
				this.invinciblePlayers.add(player.getUniqueId());
				Bukkit.getScheduler().runTaskLater(plugin, () -> this.invinciblePlayers.remove(player.getUniqueId()), 20L);
			} else
				e.setCancelled(true);
	}
	
}
