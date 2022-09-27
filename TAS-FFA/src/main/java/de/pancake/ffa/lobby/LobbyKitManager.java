package de.pancake.ffa.lobby;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.pancake.common.gui.ListInventory;
import de.pancake.common.gui.ListInventory.Item;

class LobbyKitManager {

	// @formatter:off
	/**
	 * List of available kits
	 */
	private List<Item> KITS = Arrays.asList(
		new Item("Kit 1", "B", Material.DIAMOND_CHESTPLATE),
		new Item("Kit 2", "D", Material.IRON_SWORD),
		new Item("Kit 3", "C", Material.IRON_CHESTPLATE),
		new Item("Kit 4", "A", Material.CHAINMAIL_BOOTS));
	// @formatter:on

	/**
	 * List of players with their kit inventories
	 */
	private HashMap<Player, ListInventory> inventories = new HashMap<>();

	/**
	 * Lock-state
	 */
	private boolean locked;

	/**
	 * Opens the kit inventory for a player
	 * @param p Player
	 */
	public void openInventory(Player p) {
		if (this.locked)
			return;

		var inv = this.inventories.get(p);
		if (inv == null)
			this.inventories.put(p, inv = new ListInventory("Kits", this.KITS, false));

		inv.openInventory(p);
	}

	/**
	 * Returns all kits and locks the inventories
	 * TODO: should not use "item"
	 * @return All active kit
	 */
	public List<Item> getKits() {
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
