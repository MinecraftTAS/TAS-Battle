package com.minecrafttas.tasbattle.ffa;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.minecrafttas.tasbattle.TASBattleGameserver;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

import static com.minecrafttas.tasbattle.managers.GameserverTelemetry.FORMAT;

/**
 * FFA gamemode
 * @author Pancake
 */
@Getter
public class FFA implements GameMode {
	
	private final TASBattleGameserver plugin;
	private final World world;
	private final KitManager kitManager;
	private final ScenarioManager scenarioManager;
	private GameLogic gameLogic;
		
	/**
	 * Initialize ffa gamemode
	 * @param plugin Plugin
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

		this.plugin.getTelemetry().write(String.format("[%s SERVER  ]: FFA: %s (%s kits available) (%s scenarios available)\n", FORMAT.format(Date.from(Instant.now())), worldName, this.kitManager.getKits().size() + "", this.scenarioManager.getScenarios().size()));
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

		// determine all enabled scenarios
		var scenarios = this.scenarioManager.getEnabled();

		// print messages
		Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <green>The game has started.</green>"));
		Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray>The most voted kit is: <green>" + kit.getName() + "</green></gray>"));
		Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray>and the enabled scenarios are: <green>" + scenarios.stream().map(ScenarioManager.AbstractScenario::getTitle).collect(Collectors.joining(", ")) + "</green></gray>"));
		Bukkit.broadcast(MiniMessage.miniMessage().deserialize(""));
		Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray>Every player has been spread across the map. <red>Cross teaming is not allowed!</red></gray>"));
		Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray>The last person alive will be the winner.</gray>"));
		Bukkit.broadcast(MiniMessage.miniMessage().deserialize(""));
		this.plugin.getTelemetry().write(String.format("[%s SERVER  ]: players: %s, scenarios: %s, kit: %s\n", FORMAT.format(Date.from(Instant.now())), players.stream().map(Player::getName).collect(Collectors.joining(", ")), scenarios.stream().map(ScenarioManager.AbstractScenario::getTitle).collect(Collectors.joining(", ")), kit.getName()));

		// update players
		for (var p : players) {
			// prepare player
			p.setGameMode(org.bukkit.GameMode.SURVIVAL);
			p.getInventory().clear();
			p.closeInventory();
			try {
				kit.deserializeKit(p.getInventory());
			} catch (IOException _e) {
				Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <red>An error occured while deserializing the kit.</red>"));
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
		return List.of(
				Pair.of("ffa", this.kitManager)
		);
	}
	
}
