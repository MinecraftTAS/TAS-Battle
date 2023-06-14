package com.minecrafttas.tasbattle;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.minecrafttas.tasbattle.ffa.FFA;
import com.minecrafttas.tasbattle.gui.GuiHandler;
import com.minecrafttas.tasbattle.lobby.Lobby;
import com.minecrafttas.tasbattle.lobby.LobbyManager;
import com.minecrafttas.tasbattle.tickratechanger.TickrateChanger;

import lombok.Getter;

public class TASBattle extends JavaPlugin {
	
	public static interface GameMode {
		abstract void startGameMode(List<Player> players);
		abstract List<LobbyManager> createManagers();
	}
	
	@Getter
	private TickrateChanger tickrateChanger;
	@Getter
	private GameMode gameMode;
	@Getter
	private Lobby lobby;
	
	/**
	 * Enable TAS Battle mod
	 */
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new GuiHandler(), this);
		this.tickrateChanger = new TickrateChanger(this);
		
		// TODO: fix this
		this.gameMode = new FFA(this);
		this.lobby = new Lobby(this, this.gameMode);
	}
	
}
