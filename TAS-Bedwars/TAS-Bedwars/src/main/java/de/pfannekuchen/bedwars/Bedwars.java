package de.pfannekuchen.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;

import de.pfannekuchen.bedwars.data.Spawners;

/**
 * Bedwars Launch Plugin File
 * @author Pancake
 */
public class Bedwars extends JavaPlugin implements Listener {

	public static World PRIMARYWORLD;
	
	/**
	 * Ran when the server starts.
	 * Registers listeners, loads configurations
	 */
	@Override
	public void onEnable() {
		PRIMARYWORLD = Bukkit.getWorlds().get(0);
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	/**
	 * Ran every tick, game will be ticked here
	 */
	@EventHandler
	public void onServerTick(ServerTickStartEvent e) {
		Spawners.tick();
	}
	
}
