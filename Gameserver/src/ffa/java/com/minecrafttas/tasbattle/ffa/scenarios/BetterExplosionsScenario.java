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

import java.util.List;

public class BetterExplosionsScenario extends AbstractScenario {

	private JavaPlugin plugin;

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

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if ((e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) && e.getDamage() >= 2.0)
			e.setDamage(Math.max(2, e.getDamage() / 5));
	}
	
}
