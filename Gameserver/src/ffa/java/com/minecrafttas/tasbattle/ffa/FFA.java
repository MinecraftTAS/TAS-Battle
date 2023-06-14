package com.minecrafttas.tasbattle.ffa;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.minecrafttas.tasbattle.TASBattle.GameMode;
import com.minecrafttas.tasbattle.ffa.managers.KitManager;
import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager;
import com.minecrafttas.tasbattle.lobby.LobbyManager;

import net.kyori.adventure.text.Component;

/**
 * FFA gamemode
 * @author Pancake
 */
public class FFA implements GameMode {
	
	@Getter
	private TASBattle plugin;
	
	@Getter
	private World world;
	
	
	/**
	 * Initialize ffa gamemode
	 * @param plugin Plugin
	 */
	public FFA(TASBattle plugin) {
		this.plugin = plugin;
		
		// find available worlds
		var serverDir = new File(".");
		var availableWorlds = serverDir.listFiles((dir, name) -> name.startsWith("ffa-"));
		// pick random world
		var worldName = availableWorlds[(int) (Math.random() * availableWorlds.length)].getName();
		this.world = WorldUtils.loadWorld(worldName);
	}
	
	@Override
	public void startGameMode(List<Player> players) {
		Bukkit.broadcast(Component.text("we can have fun now... woo"));
		// teleport players
		SpreadplayersUtils.spreadplayers(players, this.world.getSpawnLocation(), 128.0);
	}

	@Override
	public List<LobbyManager> createManagers() {
		return Arrays.asList(
			new KitManager(),
			new ScenarioManager()
		);
	}
	
}
