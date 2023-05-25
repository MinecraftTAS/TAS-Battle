package com.minecrafttas.tasbattle;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.minecrafttas.tasbattle.bedwars.Bedwars;
import com.minecrafttas.tasbattle.lobby.Lobby;
import com.minecrafttas.tasbattle.lobby.LobbyManager;
import com.minecrafttas.tasbattle.tickratechanger.TickrateChanger;

public class TASBattle extends JavaPlugin {
	
	public static interface GameMode {
		abstract void startGameMode(List<Player> players);
		abstract List<LobbyManager> createManagers();
	}
	
	private TickrateChanger tickrateChanger;
	private GameMode gameMode;
	private Lobby lobby;
	
	/**
	 * Enable TAS Battle mod
	 */
	@Override
	public void onEnable() {
		this.tickrateChanger = new TickrateChanger(this);
		
		// TODO: fix this
		this.gameMode = new Bedwars(this);
		this.lobby = new Lobby(this, this.gameMode);
	}

	/**
	 * Get Tickrate Changer instance
	 * @return Tickrate Changer instance
	 */
	public TickrateChanger getTickrateChanger() {
		return this.tickrateChanger;
	}
	
	/**
	 * Get Gamemode instance
	 * @return Gamemode instance
	 */
	public GameMode getGameMode() {
		return this.gameMode;
	}
	
	/**
	 * Get Lobby instance
	 * @return Lobby instance
	 */
	public Lobby getLobby() {
		return this.lobby;
	}
	
}
