package com.minecrafttas.tasbattle;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.minecrafttas.tasbattle.ffa.FFA;
import com.minecrafttas.tasbattle.gamemode.ModeManagement;
import com.minecrafttas.tasbattle.lobby.Lobby;
import com.minecrafttas.tasbattle.lobby.LobbyManager;
import com.minecrafttas.tasbattle.tickratechanger.TickrateChanger;

public class TASBattle extends JavaPlugin {

	/**
	 * Abstract module
	 */
	public static abstract class AbstractModule {

		/**
		 * Enable gamemode
		 * @param plugin Main Plugin
		 */
		public abstract void onEnable(TASBattle plugin);
		
		/**
		 * Execute command
		 * @param sender Command sender
		 * @param args Command parameters
		 */
		public abstract void onCommand(CommandSender sender, String[] args);
		
		/**
		 * Get command name
		 * @return Command name
		 */
		public abstract String getCommandName();
		
	}
	
	/**
	 * Abstract gamemode
	 */
	public static abstract class AbstractGameMode {
		
		protected TASBattle plugin;
		
		/**
		 * Initialize abstract gamemode
		 * @param plugin
		 */
		public AbstractGameMode(TASBattle plugin) {
			this.plugin = plugin;
		}

		/**
		 * Start game mode
		 * @param players Participating players
		 */
		public abstract void startGameMode(List<Player> players);
		
		/**
		 * Create all managers that modify the game rules
		 * @return List of managers
		 */
		public abstract List<LobbyManager> createManagers();
		
	}
	
	private TickrateChanger tickrateChanger;
	private ModeManagement modeManagement;

	/**
	 * Enable TAS Battle mod
	 */
	@Override
	public void onEnable() {
		this.tickrateChanger = new TickrateChanger();
		this.tickrateChanger.onEnable(this);
		
		this.modeManagement = new ModeManagement();
		this.modeManagement.onEnable(this);
		
		// init ffa lobby for now
		this.modeManagement.registerGameMode("LOBBY", new Lobby(this, new FFA(this)));
		this.modeManagement.setGameMode("LOBBY");
	}
	
	/**
	 * Handle TAS Battle command
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (this.tickrateChanger.getCommandName().equalsIgnoreCase(command.getName())) {
			this.tickrateChanger.onCommand(sender, args);
			return true;
		}
		
		return true;
	}

	/**
	 * Get Tickrate Changer instance
	 * @return Tickrate Changer instance
	 */
	public TickrateChanger getTickrateChanger() {
		return this.tickrateChanger;
	}

	/**
	 * Get Mode Management instance
	 * @return Mode Management instance
	 */
	public ModeManagement getModeManagement() {
		return this.modeManagement;
	}
	
}
