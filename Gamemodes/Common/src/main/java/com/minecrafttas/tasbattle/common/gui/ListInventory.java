package com.minecrafttas.tasbattle.common.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;

/**
 * Inventory with a list of toggleable items
 * @author Pancake
 */
public class ListInventory {

	/**
	 * Item visible in the list inventory
	 * @author Pancake
	 */
	public static class Item {
		private String title;
		private String description;
		private Material type;
		private ItemStack stack;

		public Item(String title, String description, Material type) {
			this.title = title;
			this.description = description;
			this.type = type;
			// make item stack
			this.stack = new ItemStack(this.type);
			this.stack.editMeta(meta -> {
				meta.displayName(Component.text(ChatColor.WHITE + this.title));
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				meta.lore(Arrays.asList(this.description.split("\n")).stream().map(c -> Component.text(ChatColor.DARK_PURPLE + c)).collect(Collectors.toList()));
			});
		}

		public String getTitle() {
			return this.title;
		}

		public String getDescription() {
			return this.description;
		}

		public Material getType() {
			return this.type;
		}

		public ItemStack getItemStack() {
			return this.stack;
		}

	}

	/**
	 * Entries in the inventory
	 */
	private HashMap<Item, Boolean> entries;

	/**
	 * ItemStacks corresponding to each item
	 */
	private HashMap<ItemStack, Item> alias;

	/**
	 * Last item stack updated (used for allowMultiple)
	 */
	private ItemStack lastUpdated;

	/**
	 * The inventory
	 */
	private Inventory inventory;

	/**
	 * Multi-selection state
	 */
	private boolean allowMultiple;

	/**
	 * Creates a new list inventory
	 * @param items
	 */
	public ListInventory(String title, List<Item> items, boolean allowMultiple) {
		this.allowMultiple = allowMultiple;
		this.entries = new HashMap<>();
		this.alias = new HashMap<>();

		// make inventory
		this.inventory = Bukkit.createInventory(null, (int) Math.ceil(items.size() / 9.0f) * 9, Component.text(title));
		items.forEach(i -> {
			this.entries.put(i, false);
			this.alias.put(i.stack, i);
			this.inventory.addItem(i.stack);
		});
	}

	/**
	 * Opens the inventory to a player
	 * @param p Player to open for
	 */
	public void openInventory(Player p) {
		p.openInventory(this.inventory);
	}

	/**
	 * Interacts with the inventory
	 * @param i Item Stack clicked
	 */
	public void interact(ItemStack i) {
		var item = this.alias.remove(i);
		if (item == null)
			return;

		// toggle last item again (looks scuffed, why?)
		if (this.lastUpdated != null)
			if (this.alias.get(this.lastUpdated) != null)
				if (!this.allowMultiple && i != this.lastUpdated && this.entries.get(this.alias.get(this.lastUpdated)))
					this.interact(this.lastUpdated);
		this.lastUpdated = i;

		// toggle item
		var meta = i.getItemMeta();
		if (this.entries.get(item)) {
			meta.removeEnchant(Enchantment.LUCK);
			this.entries.put(item, false);
		} else {
			meta.addEnchant(Enchantment.LUCK, 1, true);
			this.entries.put(item, true);
		}
		i.setItemMeta(meta);
		this.alias.put(i, item);
	}

	public List<Item> getItems() {
		List<Item> items = new ArrayList<>();
		for (Entry<Item, Boolean> entry : this.entries.entrySet())
			if (entry.getValue())
				items.add(entry.getKey());
		return items;
	}

}
