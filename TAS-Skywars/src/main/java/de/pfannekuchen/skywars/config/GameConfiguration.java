package de.pfannekuchen.skywars.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import de.pfannekuchen.skywars.exceptions.ParseException;
import de.pfannekuchen.skywars.utils.LocationUtils;

public class GameConfiguration {

	public static Location[] cageLocations;
	public static Location[][] spawnChestLocations;
	public static Location[] midChestLocations;
	
	/**
	 * Loads a configuration file, or creates one if it is empty
	 * @param configFile Configuration File
	 * @throws InvalidConfigurationException Throws whenever the configuration is incorrect
	 * @throws IOException Throws whenever the file can't be read
	 * @throws FileNotFoundException Throws whenever the file doesn't exist
	 */
	public static void loadOrCreateConfiguration(File configFile) throws FileNotFoundException, IOException, InvalidConfigurationException {
		YamlConfiguration config = new YamlConfiguration();
		config.load(configFile);
		
		try {
			@NotNull List<String> configCageLocations = config.getStringList("cages");
			cageLocations = new Location[configCageLocations.size()];
			World w = Bukkit.getWorlds().get(0);
			for (int i = 0; i < cageLocations.length; i++) {
				cageLocations[i] = LocationUtils.parseLocation(w, configCageLocations.get(i));
			}
		} catch (ParseException e) {
			throw new IOException("Unable to read cage locations from configuration", e);
		}
		
		try {
			spawnChestLocations = new Location[cageLocations.length][];
			for (int cage = 0; cage < cageLocations.length; cage++) {
				@NotNull List<String> configChestLocations = config.getStringList("cage_" + cage);
				Location[] chestLocations = new Location[configChestLocations.size()];
				World w = Bukkit.getWorlds().get(0);
				for (int i = 0; i < chestLocations.length; i++) {
					chestLocations[i] = LocationUtils.parseLocation(w, configChestLocations.get(i));
				}
				spawnChestLocations[cage] = chestLocations;
			}
		} catch (ParseException e) {
			throw new IOException("Unable to read spawn chest locations from configuration", e);
		}
		
		try {
			@NotNull List<String> configMidChestLocations = config.getStringList("midchests");
			midChestLocations = new Location[configMidChestLocations.size()];
			World w = Bukkit.getWorlds().get(0);
			for (int i = 0; i < midChestLocations.length; i++) {
				midChestLocations[i] = LocationUtils.parseLocation(w, configMidChestLocations.get(i));
			}
		} catch (ParseException e) {
			throw new IOException("Unable to read mid chest locations from configuration", e);
		}
	}
	
}
