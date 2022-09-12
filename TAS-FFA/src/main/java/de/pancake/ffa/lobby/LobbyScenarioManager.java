package de.pancake.ffa.lobby;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.pancake.common.gui.ListInventory;
import de.pancake.common.gui.ListInventory.Item;

class LobbyScenarioManager {

	// @formatter:off
	/**
	 * List of available scenarios
	 */
	private List<Item> SCENARIOS = Arrays.asList(
		new Item("20 Hearts", "Every player has 20 hearts instead of 10", Material.RED_DYE),
		new Item("Strength", "Every player has Strength I", Material.IRON_SWORD),
		new Item("Dynamic speed", "Only decreases the game speed when players are close", Material.CLOCK),
		new Item("No drops", "Players and blocks do not drop", Material.GRASS_BLOCK));
	// @formatter:on

	/**
	 * List of players with their scenario inventories
	 */
	private HashMap<Player, ListInventory> inventories = new HashMap<>();

	/**
	 * Lock-state
	 */
	private boolean locked;

	/**
	 * Opens the scenario inventory for a player
	 * @param p Player
	 */
	public void openInventory(Player p) {
		if (this.locked)
			return;

		var inv = this.inventories.get(p);
		if (inv == null)
			this.inventories.put(p, inv = new ListInventory("Scenarios", this.SCENARIOS, true));

		inv.openInventory(p);
	}

	/**
	 * Returns all scenarios and locks the inventories
	 * TODO: should not use "item"
	 * @return All active scenarios
	 */
	public List<Item> getScenarios() {
		this.locked = true;
		return null;
	}

	/**
	 * Interacts with the inventory of a player
	 * @param p Player
	 * @param clickedItem Item clicked
	 */
	public void interact(Player p, ItemStack clickedItem) {
		var inv = this.inventories.get(p);
		if (inv != null)
			inv.interact(clickedItem);
	}

}
