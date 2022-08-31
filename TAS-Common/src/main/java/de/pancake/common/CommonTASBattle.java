package de.pancake.common;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Common TAS Battle library
 * @author Pancake
 */
public class CommonTASBattle extends JavaPlugin {

	/**
	 * Current phase
	 */
	static String PHASE;

	/**
	 * Updates the current phase
	 * @param newPhase new phase
	 */
	public static void updatePhase(String newPhase) {
		PHASE = newPhase;
	}

	/**
	 * Registers events to the listener under the given phase
	 * @param phase phase
	 * @param events events
	 */
	public static void registerEvents(String phase, Events events) {
		EventListener.phases.put(phase, events);
	}

	/**
	 * Enables the plugin
	 */
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
	}

}
