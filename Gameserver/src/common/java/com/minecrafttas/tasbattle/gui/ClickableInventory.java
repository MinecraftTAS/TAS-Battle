package com.minecrafttas.tasbattle.gui;

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
	private Runnable[] interactions;
	
	/**
	 * Initialize inventory with actions
	 * @param title Window title
	 * @param size Window size
	 */
	public ClickableInventory(Component title, int size) {
		this.inv = Bukkit.createInventory(null, size, title);
		this.interactions = new Runnable[size];
		GuiHandler.instances.put(inv, this);
	}
	
	/**
	 * Set clickable inventory slot with item and action
	 * @param slot Slot
	 * @param item Item
	 * @param action Action
	 */
	public void setSlot(int slot, ItemStack item, Runnable action) {
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
	
	/**
	 * Interact with inventory
	 * @param slot
	 */
	public void onInteract(int slot) {
		if (this.interactions[slot] != null)
			this.interactions[slot].run();
	}
	
}
