package com.minecrafttas.tasbattle.ffa.lobby;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.minecrafttas.tasbattle.gui.ListInventory;
import com.minecrafttas.tasbattle.gui.ListInventory.Item;

class LobbyKitManager {

	/**
	 * Description on every kit item
	 */
	private static final String DESC = "\n\n" + ChatColor.AQUA + "Click to select!";

	// @formatter:off
	/**
	 * List of available kits
	 */
	private List<Item> KITS = Arrays.asList(
		new Item(ChatColor.GOLD + "Kit 1", ChatColor.GRAY + "B" + DESC, Material.DIAMOND_CHESTPLATE),
		new Item(ChatColor.GOLD + "Kit 2", ChatColor.GRAY + "D" + DESC, Material.IRON_SWORD),
		new Item(ChatColor.GOLD + "Kit 3", ChatColor.GRAY + "C" + DESC, Material.IRON_CHESTPLATE),
		new Item(ChatColor.GOLD + "Kit 4", ChatColor.GRAY + "A" + DESC, Material.CHAINMAIL_BOOTS));
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
