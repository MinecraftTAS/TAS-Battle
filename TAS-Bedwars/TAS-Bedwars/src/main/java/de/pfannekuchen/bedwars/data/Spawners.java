package de.pfannekuchen.bedwars.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import de.pfannekuchen.bedwars.Bedwars;
import de.pfannekuchen.bedwars.exceptions.ParseException;
import de.pfannekuchen.bedwars.utils.LocationUtils;
import net.kyori.adventure.text.Component;

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
		GOLD_ITEM.editMeta(e -> {  e.displayName(Component.text("\u00A7eGold")); });
		IRON_ITEM = new ItemStack(Material.IRON_INGOT);
		IRON_ITEM.editMeta(e -> { e.displayName(Component.text("\u00A7fIron")); });
		EMERALD_ITEM = new ItemStack(Material.EMERALD);
		EMERALD_ITEM.editMeta(e -> { e.displayName(Component.text("\u00A72Emerald")); });
		DIAMOND_ITEM = new ItemStack(Material.DIAMOND);
		DIAMOND_ITEM.editMeta(e -> { e.displayName(Component.text("\u00A7bDiamond")); });
	}
	
	private static Location[] teamSpawnerLocations; // index is the team-index
	private static Location[] emeraldSpawnLocations;
	private static Location[] diamondSpawnLocations;
	
	private static int[] ticksUntilGold;
	private static int[] ticksUntilIron;
	private static int ticksUntilEmeralds = 1;
	private static int ticksUntilDiamonds = 1;
	
	private static int tierEmerald = 0;
	private static int tierDiamond = 0;
	
	private static int[] defaultTicksUntilGold;
	private static int[] defaultTicksUntilIron;
	private static int defaultTicksUntilEmeralds = 60 * 20;
	private static int defaultTicksUntilDiamonds = 40 * 20;
	
	private static ArrayList<Runnable> armorStands = new ArrayList<>();
	
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
			@NotNull List<String> configSpawnLocations = config.getStringList("spawners");
			teamSpawnerLocations = new Location[configSpawnLocations.size()];
			defaultTicksUntilGold = new int[configSpawnLocations.size()];
			defaultTicksUntilIron = new int[configSpawnLocations.size()];
			ticksUntilGold = new int[configSpawnLocations.size()];
			ticksUntilIron = new int[configSpawnLocations.size()];
			World w = Bukkit.getWorlds().get(0);
			for (int i = 0; i < teamSpawnerLocations.length; i++) {
				defaultTicksUntilGold[i] = 20;
				defaultTicksUntilIron[i] = 60;
				teamSpawnerLocations[i] = LocationUtils.parseLocation(w, configSpawnLocations.get(i));
			}
		} catch (ParseException e) {
			throw new IOException("Unable to read spawner locations from configuration", e);
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
		
		spawnArmorStands();
	}

	/**
	 * Spawns armor stands for every spawner location
	 */
	private static void spawnArmorStands() {
		for (Location location : emeraldSpawnLocations) {
			getArmorStand(location.add(0, .7, 0), "\u00A72Emerald", null, null);
			getArmorStand(location.add(0, .3, 0), null, e -> {
				e.customName(Component.text("\u00A7eTier \u00A7c" + tierEmerald));
			}, null);
			getArmorStand(location.add(0, -.6, 0), null, e -> {
				e.customName(Component.text("\u00A7eSpawns in \u00A7c" + ((int) (ticksUntilEmeralds / 20)) + " \u00A7eseconds"));
				e.setRotation(e.getLocation().getYaw() + 1, e.getLocation().getPitch());
			}, Material.EMERALD_BLOCK);
		}
		for (Location location : diamondSpawnLocations) {
			getArmorStand(location.add(0, .7, 0), "\u00A7bDiamond", null, null);
			getArmorStand(location.add(0, .3, 0), null, e -> {
				e.customName(Component.text("\u00A7eTier \u00A7c" + tierDiamond));
			}, null);
			getArmorStand(location.add(0, -.6, 0), null, e -> {
				e.customName(Component.text("\u00A7eSpawns in \u00A7c" + ((int) (ticksUntilDiamonds / 20)) + " \u00A7eseconds"));
				e.setRotation(e.getLocation().getYaw() + 1, e.getLocation().getPitch());
			},  Material.DIAMOND_BLOCK);
		}
	}
	
	/**
	 * Obtains an automatically updating armor stand
	 * @param loc Location of the armor stand
	 * @param startString Name of the Armor Stand
	 * @param update Update thread that updates every tick
	 */
	private static void getArmorStand(Location loc, String startString, Consumer<ArmorStand> update, Material materialOnHead) {
		ArmorStand stand = (ArmorStand) Bedwars.PRIMARYWORLD.spawnEntity(loc.clone().add(.5, 0, .5), EntityType.ARMOR_STAND);
		stand.setInvisible(true);
		stand.setInvulnerable(true);
		stand.setGravity(false);
		stand.setAI(false);
		if (materialOnHead != null) stand.getEquipment().setHelmet(new ItemStack(materialOnHead), true);
		stand.setCustomNameVisible(true);
		if (startString != null) stand.customName(Component.text(startString));
		if (update != null) {
			update.accept(stand);
			armorStands.add(() -> {
				update.accept(stand);
			});
		}
	}

	/**
	 * Ticks the spawner causing items to spawn once the spawner tick counter reaches 0
	 */
	public static synchronized final void tick() {
		/* Tick through all spawner types and spawn items if required */
		decreaseTimer();
		for (int team = 0; team < defaultTicksUntilGold.length; team++) {
			if (untilGold(team) <= 0) {
				ticksUntilGold[team] = defaultTicksUntilGold[team];
				spawn(teamSpawnerLocations[team], GOLD_ITEM);
			}
			if (untilIron(team) <= 0) {
				ticksUntilIron[team] = defaultTicksUntilIron[team];
				spawn(teamSpawnerLocations[team], IRON_ITEM);
			}
		}
		if (untilEmeralds() <= 0) {
			ticksUntilEmeralds = defaultTicksUntilEmeralds;
			spawn(emeraldSpawnLocations, EMERALD_ITEM);
		}
		if (untilDiamonds() <= 0) {
			ticksUntilDiamonds = defaultTicksUntilDiamonds;
			spawn(diamondSpawnLocations, DIAMOND_ITEM);
		}
		/* Update the armor stands */
		for (Runnable consumer : armorStands) {
			consumer.run();
		}
	}
	
	/**
	 * Decreases all timers for the spawners
	 */
	private static synchronized final void decreaseTimer() {
		for (int i = 0; i < defaultTicksUntilGold.length; i++) {
			ticksUntilGold[i]--;
			ticksUntilIron[i]--;	
		}
		ticksUntilEmeralds--;
		ticksUntilDiamonds--;
	}
	
	/**
	 * Spawns items at the given locations
	 * @param locs Location to spawn at
	 * @param spawn Item to spawn
	 */
	private static synchronized final void spawn(Location location, ItemStack spawn) {
		Bedwars.PRIMARYWORLD.spawnEntity(location.clone().add(.5, 0, .5), EntityType.DROPPED_ITEM, SpawnReason.CUSTOM, e -> { ((Item) e).setItemStack(spawn.clone()); e.setVelocity(new Vector(0, 0, 0)); } );		
	}
	
	/**
	 * Spawns items at the given locations
	 * @param locs Location to spawn at
	 * @param spawn Item to spawn
	 */
	private static synchronized final void spawn(Location[] locs, ItemStack spawn) {
		for (Location location : locs)  Bedwars.PRIMARYWORLD.spawnEntity(location.clone().add(.5, 0, .5), EntityType.DROPPED_ITEM, SpawnReason.CUSTOM, e -> { ((Item) e).setItemStack(spawn.clone()); e.setVelocity(new Vector(0, 0, 0)); } );		
	}
	
	// Getters and Setters here
	
	/**
	 * @return the ticksUntilGold
	 */
	public static synchronized final int untilGold(int team) {
		return ticksUntilGold[team];
	}
	
	/**
	 * @return the ticksUntilIron
	 */
	public static synchronized final int untilIron(int team) {
		return ticksUntilIron[team];
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
	public static synchronized final void setDefaultTicksUntilGold(int defaultTicksUntilGold, int team) {
		Spawners.defaultTicksUntilGold[team] = defaultTicksUntilGold;
	}

	/**
	 * @param defaultTicksUntilIron the defaultTicksUntilIron to set
	 */
	public static synchronized final void setDefaultTicksUntilIron(int defaultTicksUntilIron, int team) {
		Spawners.defaultTicksUntilIron[team] = defaultTicksUntilIron;
	}

	/**
	 * Updates the Tier
	 * @param defaultTicksUntilEmeralds the defaultTicksUntilEmeralds to set
	 */
	public static synchronized final void setDefaultTicksUntilEmeralds(int defaultTicksUntilEmeralds) {
		Spawners.defaultTicksUntilEmeralds = defaultTicksUntilEmeralds;
		Spawners.tierEmerald++;
	}

	/**
	 * Updates the Tier
	 * @param defaultTicksUntilDiamonds the defaultTicksUntilDiamonds to set
	 */
	public static synchronized final void setDefaultTicksUntilDiamonds(int defaultTicksUntilDiamonds) {
		Spawners.defaultTicksUntilDiamonds = defaultTicksUntilDiamonds;
		Spawners.tierDiamond++;
	}

	/**
	 * @return the teamSpawnerLocations
	 */
	public static synchronized final Location[] getSpawnLocations() {
		return teamSpawnerLocations;
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

	/**
	 * @return the tierEmerald
	 */
	public static synchronized final int getTierEmerald() {
		return tierEmerald;
	}

	/**
	 * @return the tierDiamond
	 */
	public static synchronized final int getTierDiamond() {
		return tierDiamond;
	}
	
}
