package com.minecrafttas.tasbattle;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
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
	@Getter
	private boolean developmentMode;
	
	/**
	 * Enable TAS Battle mod
	 */
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new GuiHandler(), this);
		this.tickrateChanger = new TickrateChanger(this);
		
		// TODO: fix this
//		this.developmentMode = true;
		this.gameMode = new FFA(this);
		for (var pair : this.gameMode.createCommands()) {
			this.getCommand(pair.getKey()).setExecutor(pair.getValue());
			this.getCommand(pair.getKey()).setTabCompleter(pair.getValue());
		}
//		this.gameMode.startGameMode(Bukkit.getOnlinePlayers().stream().map(p -> (Player) p).toList());
		this.lobby = new Lobby(this, this.gameMode);
	}
	
}
