package com.minecrafttas.tasbattle.bedwars.components;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.minecrafttas.tasbattle.TASBattle;

/**
 * Shop villager
 */
public class TeamShop implements Listener {

	private World world;
	
	/**
	 * Initialize team shop
	 * @param plugin Plugin
	 * @param world Game world
	 */
	public TeamShop(TASBattle plugin, World world) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.world = world;
	}
	
	/**
	 * Update shop villagers to face player
	 * @param e Event
	 */
	@EventHandler
	public void onTick(ServerTickStartEvent e) {
		for (var v : this.world.getEntitiesByClass(Villager.class)) {
			for (var p : this.world.getNearbyEntitiesByType(Player.class, v.getLocation(), 10.0)) {
				var offsetVec = v.getLocation().subtract(p.getLocation());
				var eyeVec = v.getEyeLocation().setDirection(offsetVec.toVector());
				v.setRotation(180.0f + eyeVec.getYaw(), -eyeVec.getPitch());
			}
		}
	}
	
}
