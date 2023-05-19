package com.minecrafttas.tasbattle.lobby;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.minecrafttas.tasbattle.gui.ListInventory;
import com.minecrafttas.tasbattle.gui.ListInventory.Item;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;

/**
 * Abstract lobby manager
 */
public abstract class LobbyManager {
	
	private HashMap<Player, ListInventory> inventories;
	private String inventoryTitle;
	private boolean allowMultiple;
	
	/**
	 * Initialize lobby manager
	 */
	public LobbyManager(String inventoryTitle, boolean allowMultiple) {
		this.inventories = new HashMap<>();
		this.inventoryTitle = inventoryTitle;
		this.allowMultiple = allowMultiple;
	}
	
	/**
	 * Opens the scenario inventory for a player
	 * @param p Player
	 */
	public void openInventory(Player p) {
		var inv = this.inventories.get(p);
		if (inv == null)
			this.inventories.put(p, inv = new ListInventory(this.inventoryTitle, this.getItems(), this.allowMultiple));

		inv.openInventory(p);
		p.playSound(Sound.sound(org.bukkit.Sound.BLOCK_CHEST_OPEN, Source.PLAYER, 0.25f, 1.0f));
	}
	
	/**
	 * Interact with inventory of player
	 * @param p Player
	 * @param clickedItem Item clicked
	 */
	public void onInteract(Player p, ItemStack clickedItem) {
		var inv = this.inventories.get(p);
		
		if (inv == null)
			return;
		
		inv.interact(clickedItem);
	}
	
	/**
	 * Create item for inventory
	 * @param name Item name
	 * @param lore Item lore
	 * @param item Item type
	 * @return
	 */
	protected final Item createItem(String name, String lore, Material item) {
		return new Item(ChatColor.GOLD + name, ChatColor.GRAY + lore + this.getItemBaseLore(), Material.RED_DYE);
	}
	
	/**
	 * Get inventory title
	 * @return Inventory title
	 */
	public final String getInventoryTitle() {
		return this.inventoryTitle;
	}
	
	
	/**
	 * Get items in inventory
	 * @return List of items
	 */
	protected abstract List<Item> getItems();
	
	/**
	 * Get base lore of each item in inventory
	 * @return
	 */
	protected abstract String getItemBaseLore();
	
	/**
	 * Material of item in hand
	 * @return Material of item
	 */
	protected abstract Material getItem();

	/**
	 * Lore of item in hand
	 * @return Lore of item
	 */
	protected abstract List<Component> getItemLore();
	
}
