package com.minecrafttas.tasbattle.gamemode;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.minecrafttas.tasbattle.TASBattle;
import com.minecrafttas.tasbattle.TASBattle.AbstractModule;

/**
 * TAS Battle mode management
 * @author Pancake
 */
public class ModeManagement extends AbstractModule {

	private String gameMode;
	private Map<String, GameMode> gameModes;
	
	/**
	 * Initialize plugin
	 */
	@Override
	public void onEnable(TASBattle plugin) {
		this.gameModes = new HashMap<>();
		
		// register events
		Bukkit.getPluginManager().registerEvents(new EventListener(this), plugin);
	}
	
	/**
	 * Update the current game mode
	 * @param gameMode New game mode
	 */
	public void setGameMode(String gameMode) {
		this.gameMode = gameMode;
	}

	/**
	 * Register game mode to the listener for given phase
	 * @param name Game mode name
	 * @param mode Game mode
	 */
	public void registerGameMode(String name, GameMode mode) {
		this.gameModes.put(name, mode);
	}

	/**
	 * Get current gamemode
	 * @return Current gamemode or null
	 */
	public GameMode getGameMode() {
		return this.gameModes.get(this.gameMode);
	}
	
	@Override public void onCommand(CommandSender sender, String[] args) { }
	@Override public String getCommandName() { return null; }

}
