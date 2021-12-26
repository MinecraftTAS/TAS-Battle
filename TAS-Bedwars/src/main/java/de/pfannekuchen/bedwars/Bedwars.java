package de.pfannekuchen.bedwars;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;

import de.pfannekuchen.bedwars.data.Shop;
import de.pfannekuchen.bedwars.data.Spawners;

/**
 * Bedwars Launch Plugin File
 * @author Pancake
 */
public class Bedwars extends JavaPlugin implements Listener {

	public static World PRIMARYWORLD;
	public static Bedwars plugin;
	
	/**
	 * Ran when the server starts.
	 * Registers listeners, loads configurations.
	 */
	@Override
	public void onEnable() {
		plugin = this;
		PRIMARYWORLD = Bukkit.getWorlds().get(0);
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(new Shop(), this);
		try {
			Spawners.loadConfig(getDataFolder());
			Shop.loadConfig(getDataFolder());
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Ran every tick, game will be ticked here
	 */
	@EventHandler
	public void onServerTick(ServerTickStartEvent e) {
		Spawners.tick();
		Shop.tick();
	}
	
}
