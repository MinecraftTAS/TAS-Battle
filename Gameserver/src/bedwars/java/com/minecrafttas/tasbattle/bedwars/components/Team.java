package com.minecrafttas.tasbattle.bedwars.components;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.minecrafttas.tasbattle.TASBattleGameserver;

/**
 * Team
 */
public class Team implements Listener {

	private final World world;
	private final Location[] baseLocations;
	private final Player[][] teamPlayers;
	
	/**
	 * Initialize teams
	 * @param plugin Plugin
	 * @param players Participating players
	 * @param world Game world
	 */
	public Team(TASBattleGameserver plugin, List<Player> players, World world) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.world = world;
		
		// load config
		var config = new YamlConfiguration();
		try {
			config.load(new File(this.world.getWorldFolder(), "teams.yml"));
		} catch (IOException | InvalidConfigurationException e) {
			System.err.println("Unable to load teams");
			e.printStackTrace();
		}
		
		// load config
		this.baseLocations = config.getStringList("teams").stream().map(s -> s.split(" ")).map(s -> new Location(this.world, Double.parseDouble(s[0]), Double.parseDouble(s[1]), Double.parseDouble(s[2]), Float.parseFloat(s[3]), Float.parseFloat(s[4]))).toArray(Location[]::new);
		
		// spread players across teams
		System.out.println(players.size() + "," + this.baseLocations.length);
		this.teamPlayers = new Player[this.baseLocations.length][];
		var playersPerTeam = (int) Math.ceil(players.size() / (double) this.teamPlayers.length);
		for (int i = 0; i < this.teamPlayers.length; i++) {
			int start = i * playersPerTeam;
			if (start < players.size()) {
				this.teamPlayers[i] = players.subList(start, Math.min(players.size(), (i+1) * playersPerTeam)).toArray(Player[]::new);
				// prepare players
				for (var player : this.teamPlayers[i]) {
					player.getInventory().clear();
					player.setFallDistance(0.0f);
					player.teleport(this.baseLocations[i]);
					player.setBedSpawnLocation(this.baseLocations[i], true);
					player.setGameMode(GameMode.SURVIVAL);
				}
			}
		}
	}

}
