package com.minecrafttas.tasbattle.gui;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;

/**
 * Inventory with click actions bound
 * @author Pancake
 */
public class ClickableInventory {

	private Inventory inv;
	private Consumer<?>[] interactions;
	
	/**
	 * Initialize inventory with actions
	 * @param title Window title
	 * @param size Window size
	 */
	public ClickableInventory(Component title, int size) {
		this.inv = Bukkit.createInventory(null, size, title);
		this.interactions = new Consumer<?>[size];
	}
	
	/**
	 * Set clickable inventory slot with item and action
	 * @param slot Slot
	 * @param item Item
	 * @param action Action
	 */
	public void setSlot(int slot, ItemStack item, Consumer<Inventory> action) {
		this.inv.setItem(slot, item);
		this.interactions[slot] = action;
	}
	
	/**
	 * Return wrapped inventory
	 * @return Inventory
	 */
	public Inventory inventory() {
		return this.inv;
	}
	
}
