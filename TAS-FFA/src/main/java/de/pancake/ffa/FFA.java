package de.pancake.ffa;

import org.bukkit.plugin.java.JavaPlugin;

import de.pancake.common.CommonTASBattle;

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
		CommonTASBattle.registerEvents("LOBBY", new Lobby());
		CommonTASBattle.updatePhase("LOBBY");
	}

}
