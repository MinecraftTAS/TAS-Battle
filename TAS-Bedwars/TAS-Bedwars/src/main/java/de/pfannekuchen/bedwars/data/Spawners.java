package de.pfannekuchen.bedwars.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import de.pfannekuchen.bedwars.Bedwars;
import de.pfannekuchen.bedwars.exceptions.ParseException;
import de.pfannekuchen.bedwars.utils.LocationUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Contains data for all spawners and when they will spawn
 * @author Pancake
 */
public class Spawners {

	private static final ItemStack GOLD_ITEM;
	private static final ItemStack IRON_ITEM;
	private static final ItemStack EMERALD_ITEM;
	private static final ItemStack DIAMOND_ITEM;
	
	static {
		GOLD_ITEM = new ItemStack(Material.GOLD_INGOT);
		GOLD_ITEM.editMeta(e -> {  e.displayName(Component.text("§eGold")); });
		IRON_ITEM = new ItemStack(Material.IRON_INGOT);
		IRON_ITEM.editMeta(e -> { e.displayName(Component.text("§fIron")); });
		EMERALD_ITEM = new ItemStack(Material.EMERALD);
		EMERALD_ITEM.editMeta(e -> { e.displayName(Component.text("§2Emerald")); });
		DIAMOND_ITEM = new ItemStack(Material.DIAMOND);
		DIAMOND_ITEM.editMeta(e -> { e.displayName(Component.text("§bDiamond")); });
	}
	
	private static Location[] goldSpawnLocations;
	private static Location[] ironSpawnLocations;
	private static Location[] emeraldSpawnLocations;
	private static Location[] diamondSpawnLocations;
	
	private static int ticksUntilGold = 1;
	private static int ticksUntilIron = 1;
	private static int ticksUntilEmeralds = 1;
	private static int ticksUntilDiamonds = 1;
	
	private static int defaultTicksUntilGold = 3 * 20;
	private static int defaultTicksUntilIron = 1 * 20;
	private static int defaultTicksUntilEmeralds = 60 * 20;
	private static int defaultTicksUntilDiamonds = 40 * 20;
	
	/**
	 * Loads a configuration file, or creates one if it is empty
	 * @param configFile Configuration File
	 * @throws InvalidConfigurationException Throws whenever the configuration is incorrect
	 * @throws IOException Throws whenever the file can't be read
	 * @throws FileNotFoundException Throws whenever the file doesn't exist
	 */
	public static final void loadConfig(File configFolder) throws FileNotFoundException, IOException, InvalidConfigurationException {
		YamlConfiguration config = new YamlConfiguration();
		config.load(new File(configFolder, "locations.yml"));
		
		try {
			@NotNull List<String> configGoldLocations = config.getStringList("goldspawners");
			goldSpawnLocations = new Location[configGoldLocations.size()];
			World w = Bukkit.getWorlds().get(0);
			for (int i = 0; i < goldSpawnLocations.length; i++) {
				goldSpawnLocations[i] = LocationUtils.parseLocation(w, configGoldLocations.get(i));
			}
		} catch (ParseException e) {
			throw new IOException("Unable to read gold spawn locations from configuration", e);
		}
		
		try {
			@NotNull List<String> configIronLocations = config.getStringList("ironspawners");
			ironSpawnLocations = new Location[configIronLocations.size()];
			World w = Bukkit.getWorlds().get(0);
			for (int i = 0; i < ironSpawnLocations.length; i++) {
				ironSpawnLocations[i] = LocationUtils.parseLocation(w, configIronLocations.get(i));
			}
		} catch (ParseException e) {
			throw new IOException("Unable to read iron spawn locations from configuration", e);
		}
		
		try {
			@NotNull List<String> configEmeraldLocations = config.getStringList("emeraldspawners");
			emeraldSpawnLocations = new Location[configEmeraldLocations.size()];
			World w = Bukkit.getWorlds().get(0);
			for (int i = 0; i < emeraldSpawnLocations.length; i++) {
				emeraldSpawnLocations[i] = LocationUtils.parseLocation(w, configEmeraldLocations.get(i));
			}
		} catch (ParseException e) {
			throw new IOException("Unable to read emerald spawn locations from configuration", e);
		}
		
		try {
			@NotNull List<String> configDiamondLocations = config.getStringList("diamondspawners");
			diamondSpawnLocations = new Location[configDiamondLocations.size()];
			World w = Bukkit.getWorlds().get(0);
			for (int i = 0; i < diamondSpawnLocations.length; i++) {
				diamondSpawnLocations[i] = LocationUtils.parseLocation(w, configDiamondLocations.get(i));
			}
		} catch (ParseException e) {
			throw new IOException("Unable to read diamond spawn locations from configuration", e);
		}
		
		defaultTicksUntilGold = config.getInt("goldticks", defaultTicksUntilGold);
		defaultTicksUntilIron = config.getInt("ironticks", defaultTicksUntilIron);
		defaultTicksUntilEmeralds = config.getInt("emeraldticks", defaultTicksUntilEmeralds);
		defaultTicksUntilDiamonds = config.getInt("diamondticks", defaultTicksUntilDiamonds);
	}
	
	/**
	 * Ticks the spawner causing items to spawn once the spawner tick counter reaches 0
	 */
	public static synchronized final void tick() {
		/* Tick through all spawner types and spawn items if required */
		decreaseTimer();
		if (untilGold() <= 0) {
			ticksUntilGold = defaultTicksUntilGold;
			spawn(goldSpawnLocations, GOLD_ITEM);
		}
		if (untilIron() <= 0) {
			ticksUntilIron = defaultTicksUntilIron;
			spawn(ironSpawnLocations, IRON_ITEM);
		}
		if (untilEmeralds() <= 0) {
			ticksUntilEmeralds = defaultTicksUntilEmeralds;
			spawn(emeraldSpawnLocations, EMERALD_ITEM);
		}
		if (untilDiamonds() <= 0) {
			ticksUntilDiamonds = defaultTicksUntilDiamonds;
			spawn(diamondSpawnLocations, DIAMOND_ITEM);
		}
	}
	
	/**
	 * Decreases all timers for the spawners
	 */
	private static synchronized final void decreaseTimer() {
		ticksUntilGold--;
		ticksUntilIron--;
		ticksUntilEmeralds--;
		ticksUntilDiamonds--;
	}
	
	/**
	 * Spawns items at the given locations
	 * @param locs Location to spawn at
	 * @param spawn Item to spawn
	 */
	private static synchronized final void spawn(Location[] locs, ItemStack spawn) {
		for (Location location : locs) Bedwars.PRIMARYWORLD.dropItem(location, spawn.clone());
	}
	
	// Getters and Setters here
	
	/**
	 * @return the ticksUntilGold
	 */
	public static synchronized final int untilGold() {
		return ticksUntilGold;
	}
	
	/**
	 * @return the ticksUntilIron
	 */
	public static synchronized final int untilIron() {
		return ticksUntilIron;
	}
	
	/**
	 * @return the ticksUntilEmeralds
	 */
	public static synchronized final int untilEmeralds() {
		return ticksUntilEmeralds;
	}
	
	/**
	 * @return the ticksUntilDiamonds
	 */
	public static synchronized final int untilDiamonds() {
		return ticksUntilDiamonds;
	}

	/**
	 * @param defaultTicksUntilGold the defaultTicksUntilGold to set
	 */
	public static synchronized final void setDefaultTicksUntilGold(int defaultTicksUntilGold) {
		Spawners.defaultTicksUntilGold = defaultTicksUntilGold;
	}

	/**
	 * @param defaultTicksUntilIron the defaultTicksUntilIron to set
	 */
	public static synchronized final void setDefaultTicksUntilIron(int defaultTicksUntilIron) {
		Spawners.defaultTicksUntilIron = defaultTicksUntilIron;
	}

	/**
	 * @param defaultTicksUntilEmeralds the defaultTicksUntilEmeralds to set
	 */
	public static synchronized final void setDefaultTicksUntilEmeralds(int defaultTicksUntilEmeralds) {
		Spawners.defaultTicksUntilEmeralds = defaultTicksUntilEmeralds;
	}

	/**
	 * @param defaultTicksUntilDiamonds the defaultTicksUntilDiamonds to set
	 */
	public static synchronized final void setDefaultTicksUntilDiamonds(int defaultTicksUntilDiamonds) {
		Spawners.defaultTicksUntilDiamonds = defaultTicksUntilDiamonds;
	}

	/**
	 * @return the goldSpawnLocations
	 */
	public static synchronized final Location[] getGoldSpawnLocations() {
		return goldSpawnLocations;
	}

	/**
	 * @return the ironSpawnLocations
	 */
	public static synchronized final Location[] getIronSpawnLocations() {
		return ironSpawnLocations;
	}

	/**
	 * @return the emeraldSpawnLocations
	 */
	public static synchronized final Location[] getEmeraldSpawnLocations() {
		return emeraldSpawnLocations;
	}

	/**
	 * @return the diamondSpawnLocations
	 */
	public static synchronized final Location[] getDiamondSpawnLocations() {
		return diamondSpawnLocations;
	}
	
}
