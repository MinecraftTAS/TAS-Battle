package com.minecrafttas.tasbattle.bedwars.components.shop;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.minecrafttas.tasbattle.gui.PagedInventory;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

/**
 * Bedwards item shop
 */
public class ItemShop extends PagedInventory {

	/**
	 * Price types for shop
	 */
	@RequiredArgsConstructor
	public static enum Price {
		IRON("§f_ Iron", Material.IRON_INGOT), GOLD("§6_ Gold", Material.GOLD_INGOT), DIAMOND("§b_ Diamond", Material.DIAMOND), EMERALD("§2_ Emerald", Material.EMERALD);
		
		private final String displayName;
		private final Material type;
		
	}
	
	public static class NameItem extends ItemStack {
		
		/**
		 * Initialize named item stack
		 * @param material Material of item
		 * @param count Item count
		 * @param name Item name
		 */
		public NameItem(Material material, int count, String name) {
			super(material, count);
			this.editMeta(e -> e.displayName(Component.text("§f" + name)));
		}
		
	}
	
	/**
	 * Purchasable item stack in shop inventory
	 */
	public static class PurchasableItem extends ItemStack {
		
		private Price price;
		private int amount;
		
		/**
		 * Initialize purchasable item stack
		 * @param price Price type
		 * @param amount Price amount
		 * @param material Material of item
		 * @param count Item count
		 * @param name Item name
		 */
		public PurchasableItem(Price price, int amount, Material material, int count, String name) {
			super(material, count);
			this.price = price;
			this.amount = amount;
			this.editMeta(e -> {
				e.displayName(Component.text("§f" + name));
				e.lore(Arrays.asList(Component.text("§7Cost: " + price.displayName.replace("_", amount + ""))));
			});
		}
		
		public Runnable purchase() {
			return null;
		}
		
	}
	
	/**
	 * Initialize item shop
	 */
	public ItemShop() {
		super(Component.text("§8Item Shop"), 54);
		// wool page
		var item = new NameItem(Material.TERRACOTTA, 1, "Blocks");
		
		var items = new PurchasableItem[] {
			new PurchasableItem(Price.IRON, 4, Material.WHITE_WOOL, 16, "Wool"),
			new PurchasableItem(Price.IRON, 8, Material.TERRACOTTA, 16, "Terracotta"),
			new PurchasableItem(Price.IRON, 12, Material.GLASS, 4, "Glass"),
			new PurchasableItem(Price.IRON, 24, Material.END_STONE, 12, "End Stone"),
			new PurchasableItem(Price.IRON, 3, Material.LADDER, 8, "Ladder"),
			new PurchasableItem(Price.GOLD, 4, Material.OAK_PLANKS, 16, "Wood"),
			new PurchasableItem(Price.EMERALD, 4, Material.OBSIDIAN, 4, "Obsidian"),
			
		};
		
		this.setPage(1, new Page(item, items, Arrays.asList(items).stream().map(i -> i.purchase()).toArray(Runnable[]::new)));
	}
	
}
