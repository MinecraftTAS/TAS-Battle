package com.minecrafttas.tasbattle.loading;

import java.util.Arrays;
import java.util.List;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

public class WorldUtils {

	/**
	 * Load world and preset gamerules
	 * @param name World name
	 * @return World
	 */
	public static World loadWorld(String name) {
		// create world
		var w = WorldCreator.name(name)
			.generator(new ChunkGenerator() {})
			.biomeProvider(new BiomeProvider() {
				@Override public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) { return Arrays.asList(Biome.THE_VOID); }
				@Override public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) { return Biome.THE_VOID; }
			})
			.createWorld();
		
		// set gamerules
		w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		w.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
		w.setGameRule(GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK, true);
		w.setGameRule(GameRule.DISABLE_RAIDS, true);
		w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		w.setGameRule(GameRule.DO_ENTITY_DROPS, false);
		w.setGameRule(GameRule.DO_FIRE_TICK, false);
		w.setGameRule(GameRule.DO_INSOMNIA, false);
		w.setGameRule(GameRule.DO_MOB_LOOT, false);
		w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		w.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
		w.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
		w.setGameRule(GameRule.DO_WARDEN_SPAWNING, false);
		w.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		w.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
		w.setGameRule(GameRule.SPAWN_RADIUS, 0);
		w.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
		w.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
		w.setAutoSave(false);
		return w;

	}
	
}
