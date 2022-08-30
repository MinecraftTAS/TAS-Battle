package de.pancake.ffa;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * FFA plugin
 * @author Pancake
 */
public class FFA extends JavaPlugin {

	/**
	 * FFA plugin instance
	 */
	public static FFA plugin;

	/**
	 * Initializes the ffa plugin into lobby phase
	 */
	@Override
	public void onEnable() {
		// update plugin singleton
		plugin = this;
	}

}
