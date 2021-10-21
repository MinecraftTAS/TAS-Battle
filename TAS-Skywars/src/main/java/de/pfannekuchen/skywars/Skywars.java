package de.pfannekuchen.skywars;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import de.pfannekuchen.skywars.config.GameConfiguration;

/**
 * Main and basically heart of the Skywars Plugin
 * @author Pancake
 */
public class Skywars extends JavaPlugin {

	private static Skywars instance;
	public static Skywars instance() { return instance; }

	/**
	 * Load configurations and prepare the game
	 */
	@Override
	public void onEnable() {
		try {
			GameConfiguration.loadOrCreateConfiguration(new File(getDataFolder(), "config.yml"));
		} catch (IOException | InvalidConfigurationException e) {
			System.err.println("Unable to load configuration.");
			e.printStackTrace();
		}
	}

}
