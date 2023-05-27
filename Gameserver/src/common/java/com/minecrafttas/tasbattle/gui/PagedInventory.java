package com.minecrafttas.tasbattle.gui;

import org.bukkit.inventory.ItemStack;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

/**
 * Inventory with multiple pages of items
 */
public class PagedInventory extends ClickableInventory {

	@RequiredArgsConstructor
	public static class Page {
		private final ItemStack item;
		private final ItemStack[] content;
		private final Runnable[] interactions;
	}

	private Page[] pages;
	
	/**
	 * Initialize paged inventory
	 * @param title Title
	 * @param size Inventory size
	 */
	public PagedInventory(Component title, int size) {
		super(title, size);
		this.pages = new Page[9];
	}

	/**
	 * Add page to inventory
	 * @param i Page index
	 * @param p Page
	 */
	public void setPage(int i, Page p) {
		this.pages[i] = p;
		this.setSlot(i, p.item, () -> {
			for (int j = 0; j < p.content.length; j++)
				this.setSlot(j + 9, p.content[j], p.interactions[j]);
		});
	}
	
}
