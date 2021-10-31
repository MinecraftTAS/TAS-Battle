package de.pfannekuchen.bedwars.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

import de.pfannekuchen.bedwars.Bedwars;
import de.pfannekuchen.bedwars.exceptions.ParseException;
import de.pfannekuchen.bedwars.utils.LocationUtils;
import net.kyori.adventure.text.Component;

/**
 * Summons shops and handles their GUIs
 * @author Pancake
 */
public class Shop implements Listener {

	/**
	 * Opens a shop gui when a player interacts with a shop
	 * @param e
	 */
	@EventHandler
	public void onShopInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Villager) {
			for (Villager villager : itemShopLocations) {
				if (e.getRightClicked().getLocation().distance(villager.getLocation()) <= 2) {
					openItemShop(e.getPlayer());
				}
			}
			for (Villager villager : upgradeShopLocations) {
				if (e.getRightClicked().getLocation().distance(villager.getLocation()) <= 2) {
					openUpgradeShop(e.getPlayer());
				}
			}
		}
	}
	
	/**
	 * Opens an Upgrade Shop for a player
	 * @param player Player to open for
	 */
	private void openUpgradeShop(Player player) {
		
	}

	/**
	 * Opens an Item Shop for a player
	 * @param player Player to open for
	 */
	private void openItemShop(Player player) {
		
	}

	private static Villager[] upgradeShopLocations;
	private static Villager[] itemShopLocations;
	
	/**
	 * Loads a configuration file, or creates one if it is empty
	 * @param configFile Configuration File
	 * @throws InvalidConfigurationException Throws whenever the configuration is incorrect
	 * @throws IOException Throws whenever the file can't be read
	 * @throws FileNotFoundException Throws whenever the file doesn't exist
	 */
	public static final void loadConfig(File configFolder) throws FileNotFoundException, IOException, InvalidConfigurationException {
		YamlConfiguration config = new YamlConfiguration();
		config.load(new File(configFolder, "shops.yml"));
		
		try {
			@NotNull List<String> configUpgradeShopLocations = config.getStringList("upgradeshops");
			World w = Bukkit.getWorlds().get(0);
			upgradeShopLocations = new Villager[configUpgradeShopLocations.size()];
			for (int i = 0; i < upgradeShopLocations.length; i++) {
				upgradeShopLocations[i] = summonUpgradeShop(LocationUtils.parseLocation(w, configUpgradeShopLocations.get(i)));
			}
		} catch (ParseException e) {
			throw new IOException("Unable to read and summon upgrade shops from configuration", e);
		}
		
		try {
			@NotNull List<String> configItemShopLocations = config.getStringList("itemshops");
			World w = Bukkit.getWorlds().get(0);
			itemShopLocations = new Villager[configItemShopLocations.size()];
			for (int i = 0; i < itemShopLocations.length; i++) {
				itemShopLocations[i] = summonItemShop(LocationUtils.parseLocation(w, configItemShopLocations.get(i)));
			}
		} catch (ParseException e) {
			throw new IOException("Unable to read and summon item shops from configuration", e);
		}
		
	}
	
	/**
	 * Spawns an item shop
	 * @param loc Position to spawn at
	 * @return Returns the Villager that has been summoned
	 */
	private static Villager summonItemShop(Location loc) {
		Villager villager = summonVillager(loc);
		villager.customName(Component.text("\u00A7b\u00A7lItem Shop"));
		return villager;
	}
	
	/**
	 * Spawns an upgrade shop
	 * @param loc Position to spawn at
	 * @return Returns the Villager that has been summoned
	 */
	private static Villager summonUpgradeShop(Location loc) {
		Villager villager = summonVillager(loc);
		villager.customName(Component.text("\u00A7b\u00A7lTeam Upgrades"));
		return villager;
	}

	/**
	 * Summons a dumb Villager
	 * @return
	 */
	private static Villager summonVillager(Location loc) {
		Villager villager = (Villager) Bedwars.PRIMARYWORLD.spawnEntity(loc.clone().add(.5, 0, .5), EntityType.VILLAGER);
		villager.setInvisible(true);
		villager.setInvulnerable(true);
		villager.setGravity(false);
		villager.setAI(false);
		villager.setCustomNameVisible(true);
		return villager;
	}

	/**
	 * @return the upgradeShopLocations
	 */
	public static synchronized final Villager[] getUpgradeShopLocations() {
		return upgradeShopLocations;
	}

	/**
	 * @return the itemShopLocations
	 */
	public static synchronized final Villager[] getItemShopLocations() {
		return itemShopLocations;
	}
	
}
