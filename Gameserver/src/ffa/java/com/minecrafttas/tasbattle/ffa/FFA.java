package com.minecrafttas.tasbattle.ffa;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.minecrafttas.tasbattle.TASBattle;
import com.minecrafttas.tasbattle.TASBattle.GameMode;
import com.minecrafttas.tasbattle.ffa.components.GameLogic;
import com.minecrafttas.tasbattle.ffa.managers.KitManager;
import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager;
import com.minecrafttas.tasbattle.ffa.utils.SpreadplayersUtils;
import com.minecrafttas.tasbattle.gui.ListInventory.Item;
import com.minecrafttas.tasbattle.loading.WorldUtils;
import com.minecrafttas.tasbattle.lobby.LobbyManager;

import lombok.Getter;
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
	
	@Getter
	private KitManager kitManager;
	
	@Getter
	private ScenarioManager scenarioManager;
	
	@Getter
	private GameLogic gameLogic;
	
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
		// determine most voted kit
		Item kit = this.kitManager.getInventories().values().stream().map(inv -> inv.getItems().get(0)).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
		Bukkit.broadcast(Component.text("§b» §7Selected kit: §6" + kit.getTitle()));

		// determine all enabled scenarios
		List<Item> scenarios = this.scenarioManager.getInventories().values().stream().map(inv -> inv.getItems()).flatMap(Collection::stream).toList();
		Bukkit.broadcast(Component.text("§b» §7Enabeld scenarios: §6" + Arrays.toString(scenarios.stream().map(e -> e.getTitle()).toArray())));
		
		// update players
		for (var p : players) {
			// prepare player
			p.setGameMode(org.bukkit.GameMode.SURVIVAL);
			p.getInventory().clear();
			p.closeInventory();
		}
		
		// teleport players
		SpreadplayersUtils.spreadplayers(players, this.world.getSpawnLocation(), 128.0);
		
		// create game logic
		this.gameLogic = new GameLogic(this.plugin, this.world, players);
	}

	@Override
	public List<LobbyManager> createManagers() {
		return Arrays.asList(
			this.kitManager = new KitManager(),
			this.scenarioManager = new ScenarioManager()
		);
	}
	
}
