package com.minecrafttas.tasbattle.ffa.lobby;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.minecrafttas.tasbattle.common.gui.ListInventory;
import com.minecrafttas.tasbattle.common.gui.ListInventory.Item;

class LobbyScenarioManager {

	/**
	 * Description on every scenario item
	 */
	private static final String DESC = "\n\n" + ChatColor.AQUA + "Click to toggle!";

	// @formatter:off
	/**
	 * List of available scenarios
	 */
	private List<Item> SCENARIOS = Arrays.asList(
		new Item(ChatColor.GOLD + "20 Hearts", ChatColor.GRAY + "Every player has 20 hearts instead of 10" + DESC, Material.RED_DYE),
		new Item(ChatColor.GOLD + "Strength", ChatColor.GRAY + "Every player has Strength I" + DESC, Material.IRON_SWORD),
		new Item(ChatColor.GOLD + "Dynamic speed", ChatColor.GRAY + "Only decreases the game speed when players are close" + DESC, Material.CLOCK),
		new Item(ChatColor.GOLD + "No drops", ChatColor.GRAY + "Players and blocks do not drop" + DESC, Material.GRASS_BLOCK));
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
