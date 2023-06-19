package com.minecrafttas.tasbattle.ffa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.minecrafttas.tasbattle.TASBattleGameserver;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.minecrafttas.tasbattle.TASBattleGameserver.GameMode;
import com.minecrafttas.tasbattle.ffa.components.GameLogic;
import com.minecrafttas.tasbattle.ffa.managers.KitManager;
import com.minecrafttas.tasbattle.ffa.managers.KitManager.Kit;
import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager;
import com.minecrafttas.tasbattle.ffa.utils.SpreadplayersUtils;
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
	private TASBattleGameserver plugin;
	
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
	 * @param dev Development moed
	 */
	public FFA(TASBattleGameserver plugin) {
		this.plugin = plugin;
		this.kitManager = new KitManager(plugin);
		this.scenarioManager = new ScenarioManager(plugin);
		
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
		var kitVotes = new ArrayList<>(this.kitManager.getVotes().values().stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream().toList());
		Collections.shuffle(kitVotes);
		var kitPair = kitVotes.stream().max(Map.Entry.comparingByValue());
		
		// find kit for game
		Kit kit;
		if (kitPair.isPresent()) {
			kit = kitPair.get().getKey();
		} else {
			var kits = new ArrayList<>(this.kitManager.getKits().keySet().stream().toList());
			Collections.shuffle(kits);
			kit = kits.get(0);
		}
		Bukkit.broadcast(Component.text("§b» §7Selected kit: §f").append(Component.text(kit.getName())));

		// determine all enabled scenarios
		var scenarios = this.scenarioManager.getEnabled();
		Bukkit.broadcast(Component.text("§b» §7Enabled scenarios: §f" + scenarios.stream().map(e -> e.getTitle()).collect(Collectors.joining(", "))));
		
		// update players
		for (var p : players) {
			// prepare player
			p.setGameMode(org.bukkit.GameMode.SURVIVAL);
			p.getInventory().clear();
			p.closeInventory();
			try {
				kit.deserializeKit(p.getInventory());
			} catch (IOException _e) {
				Bukkit.broadcast(Component.text("§b» §cAn error occured while deserializing the kit."));
				_e.printStackTrace();
			}
		}
		
		// teleport players
		SpreadplayersUtils.spreadplayers(players, this.world.getSpawnLocation(), 128.0);
		
		// create game logic
		this.gameLogic = new GameLogic(this.plugin, this.world, players);
		for (var scenario : scenarios)
			scenario.gameStart(players);
	}

	@Override
	public List<LobbyManager> createManagers() {
		return Arrays.asList(
			this.kitManager,
			this.scenarioManager
		);
	}

	@Override
	public List<Pair<String, CommandHandler>> createCommands() {
		return Arrays.asList(
			Pair.of("ffa", this.kitManager)
		);
	}
	
}
