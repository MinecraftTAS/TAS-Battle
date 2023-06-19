package com.minecrafttas.tasbattle;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.minecrafttas.tasbattle.bedwars.Bedwars;
import com.minecrafttas.tasbattle.ffa.FFA;
import com.minecrafttas.tasbattle.gui.GuiHandler;
import com.minecrafttas.tasbattle.lobby.Lobby;
import com.minecrafttas.tasbattle.lobby.LobbyManager;
import com.minecrafttas.tasbattle.managers.TickrateChanger;

import lombok.Getter;

public class TASBattleGameserver extends JavaPlugin {
	
	public static interface GameMode {
		public static interface CommandHandler extends CommandExecutor, TabCompleter {}
		abstract void startGameMode(List<Player> players);
		abstract List<LobbyManager> createManagers();
		abstract List<Pair<String, CommandHandler>> createCommands();
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
		
		this.gameMode = switch (System.getProperty("mode")) {
			case "ffa": yield new FFA(this);
			case "bedwars": yield new Bedwars(this);
			default: throw new IllegalArgumentException("Unsupported gamemode");
		};
				
		for (var pair : this.gameMode.createCommands()) {
			this.getCommand(pair.getKey()).setExecutor(pair.getValue());
			this.getCommand(pair.getKey()).setTabCompleter(pair.getValue());
		}
		
		if (System.getProperty("dev") == null)
			this.lobby = new Lobby(this, this.gameMode);
		else
			this.gameMode.startGameMode(new ArrayList<>());
	}
	
}
