package com.minecrafttas.tasbattle.bedwars;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.minecrafttas.tasbattle.TASBattle;
import com.minecrafttas.tasbattle.TASBattle.GameMode;
import com.minecrafttas.tasbattle.bedwars.components.ExplosivePhysics;
import com.minecrafttas.tasbattle.bedwars.components.InventoryManagement;
import com.minecrafttas.tasbattle.bedwars.components.PlacementRules;
import com.minecrafttas.tasbattle.bedwars.components.ResourceSpawner;
import com.minecrafttas.tasbattle.bedwars.components.Team;
import com.minecrafttas.tasbattle.bedwars.components.TeamShop;
import com.minecrafttas.tasbattle.loading.WorldUtils;
import com.minecrafttas.tasbattle.lobby.LobbyManager;

import lombok.Getter;

/**
 * Bedwars gamemode
 * @author Pancake
 */
public class Bedwars implements GameMode {
	
	@Getter
	private TASBattle plugin;
	
	@Getter
	private World world;
	
	@Getter
	private ResourceSpawner spawner;
	
	@Getter
	private PlacementRules placementRules;
	
	@Getter
	private ExplosivePhysics explosivePhysics;
	
	@Getter
	private InventoryManagement inventoryManagement;
	
	@Getter
	private TeamShop teamShop;
	
	@Getter
	private Team team;
	
	/**
	 * Initialize bedwars gamemode
	 * @param plugin Plugin
	 */
	public Bedwars(TASBattle plugin) {
		this.plugin = plugin;
		
		// find available worlds
		File serverDir = new File(".");
		File[] availableWorlds = serverDir.listFiles((dir, name) -> name.startsWith("bedwars-"));
		// pick random world
		String worldName = availableWorlds[(int) (Math.random() * availableWorlds.length)].getName();
		this.world = WorldUtils.loadWorld(worldName);
	}

	/**
	 * Start bedwars gamemode
	 */
	@Override
	public void startGameMode(List<Player> players) {
		this.spawner = new ResourceSpawner(this.plugin, this.world);
		this.placementRules = new PlacementRules(this.plugin);
		this.explosivePhysics = new ExplosivePhysics(this.plugin, this.placementRules);
		this.inventoryManagement = new InventoryManagement(this.plugin, players);
		this.teamShop = new TeamShop(this.plugin, this.inventoryManagement, this.world);
		this.team = new Team(this.plugin, players, this.world);
	}
	
	@Override
	public List<LobbyManager> createManagers() {
		return Arrays.asList();
	}

	@Override
	public List<Pair<String, CommandHandler>> createCommands() {
		return Arrays.asList();
	}
	
}
