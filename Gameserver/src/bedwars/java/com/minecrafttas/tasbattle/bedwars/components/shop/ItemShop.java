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
		
		// quick buy
//		var item = new NameItem(Material.NETHER_STAR, 1, "Quick Buy");
//		var items = new PurchasableItem[] {
//			null,
//			new PurchasableItem(Price.IRON, 4, Material.WHITE_WOOL, 16, "Wool"),
//			new PurchasableItem(Price.IRON, 8, Material.TERRACOTTA, 16, "Terracotta"),
//			new PurchasableItem(Price.IRON, 12, Material.GLASS, 4, "Glass"),
//			new PurchasableItem(Price.IRON, 24, Material.END_STONE, 12, "End Stone"),
//			new PurchasableItem(Price.IRON, 3, Material.LADDER, 8, "Ladder"),
//			new PurchasableItem(Price.GOLD, 4, Material.OAK_PLANKS, 16, "Wood"),
//			new PurchasableItem(Price.EMERALD, 4, Material.OBSIDIAN, 4, "Obsidian"),
//		};
//		this.setPage(0, new Page(item, items, Arrays.asList(items).stream().map(i -> i == null ? null : i.purchase()).toArray(Runnable[]::new)));
		
		// blocks page
		var item = new NameItem(Material.TERRACOTTA, 1, "Blocks");
		var items = new PurchasableItem[] {
			null,
			new PurchasableItem(Price.IRON, 4, Material.WHITE_WOOL, 16, "Wool"),
			new PurchasableItem(Price.IRON, 8, Material.TERRACOTTA, 16, "Terracotta"),
			new PurchasableItem(Price.IRON, 12, Material.GLASS, 4, "Glass"),
			new PurchasableItem(Price.IRON, 24, Material.END_STONE, 12, "End Stone"),
			new PurchasableItem(Price.IRON, 3, Material.LADDER, 8, "Ladder"),
			new PurchasableItem(Price.GOLD, 4, Material.OAK_PLANKS, 16, "Wood"),
			new PurchasableItem(Price.EMERALD, 4, Material.OBSIDIAN, 4, "Obsidian"),
		};
		this.setPage(1, new Page(item, items, Arrays.asList(items).stream().map(i -> i == null ? null : i.purchase()).toArray(Runnable[]::new)));
		
		// weapons page
		item = new NameItem(Material.GOLDEN_SWORD, 1, "Weapons");
		items = new PurchasableItem[] {
			null,
			new PurchasableItem(Price.IRON, 10, Material.STONE_SWORD, 1, "Stone Sword"),
			new PurchasableItem(Price.GOLD, 6, Material.IRON_SWORD, 1, "Iron Sword"),
			new PurchasableItem(Price.EMERALD, 4, Material.DIAMOND_SWORD, 1, "Diamond Sword"),
//			new PurchasableItem(Price.GOLD, 5, Material.STICK, 1, "Knockback Stick"),
		};
		this.setPage(2, new Page(item, items, Arrays.asList(items).stream().map(i -> i == null ? null : i.purchase()).toArray(Runnable[]::new)));
		
		// armor page
		item = new NameItem(Material.CHAINMAIL_BOOTS, 1, "Armor");
		items = new PurchasableItem[] {
			null,
//			new PurchasableItem(Price.IRON, 24, Material.CHAINMAIL_BOOTS, 1, "Chainmail Armor"),
//			new PurchasableItem(Price.GOLD, 12, Material.IRON_BOOTS, 1, "Iron Armor"),
//			new PurchasableItem(Price.EMERALD, 6, Material.DIAMOND_BOOTS, 1, "Diamond Armor"),
		};
		this.setPage(3, new Page(item, items, Arrays.asList(items).stream().map(i -> i == null ? null : i.purchase()).toArray(Runnable[]::new)));
		
		// tools page
		item = new NameItem(Material.STONE_PICKAXE, 1, "Tools");
		items = new PurchasableItem[] {
			null,
//			new PurchasableItem(Price.IRON, 4, Material.WHITE_WOOL, 16, "Wool"),
//			new PurchasableItem(Price.IRON, 8, Material.TERRACOTTA, 16, "Terracotta"),
//			new PurchasableItem(Price.IRON, 12, Material.GLASS, 4, "Glass"),
//			new PurchasableItem(Price.EMERALD, 4, Material.OBSIDIAN, 4, "Obsidian"),
		};
		this.setPage(4, new Page(item, items, Arrays.asList(items).stream().map(i -> i == null ? null : i.purchase()).toArray(Runnable[]::new)));
		
		// bows page
		item = new NameItem(Material.BOW, 1, "Bows");
		items = new PurchasableItem[] {
			null,
			new PurchasableItem(Price.GOLD, 2, Material.ARROW, 8, "Arrow"),
//			new PurchasableItem(Price.GOLD, 12, Material.BOW, 1, "Bow"),
//			new PurchasableItem(Price.GOLD, 24, Material.BOW, 1, "Bow II"),
//			new PurchasableItem(Price.EMERALD, 6, Material.BOW, 1, "Bow III"),
		};
		this.setPage(5, new Page(item, items, Arrays.asList(items).stream().map(i -> i == null ? null : i.purchase()).toArray(Runnable[]::new)));
		
		// potions page
		item = new NameItem(Material.BREWING_STAND, 1, "Potions");
		items = new PurchasableItem[] {
			null,
//			new PurchasableItem(Price.EMERALD, 1, Material.POTION, 1, "Speed V Potion"),
//			new PurchasableItem(Price.EMERALD, 1, Material.POTION, 1, "Jump Boost V Potion"),
//			new PurchasableItem(Price.EMERALD, 2, Material.POTION, 1, "Invisibility Potion"),
		};
		this.setPage(6, new Page(item, items, Arrays.asList(items).stream().map(i -> i == null ? null : i.purchase()).toArray(Runnable[]::new)));
		
		// secondaries page
		item = new NameItem(Material.TNT, 1, "Utilities");
		items = new PurchasableItem[] {
			null,
			new PurchasableItem(Price.GOLD, 3, Material.GOLDEN_APPLE, 1, "Golden Apple"),
			new PurchasableItem(Price.IRON, 40, Material.FIRE_CHARGE, 1, "Fireball"),
			new PurchasableItem(Price.GOLD, 4, Material.TNT, 1, "TNT"),
			new PurchasableItem(Price.EMERALD, 4, Material.ENDER_PEARL, 1, "Obsidian"),
			new PurchasableItem(Price.GOLD, 3, Material.WATER_BUCKET, 1, "Obsidian"),
		};
		this.setPage(7, new Page(item, items, Arrays.asList(items).stream().map(i -> i == null ? null : i.purchase()).toArray(Runnable[]::new)));
		
		this.onInteract(1);
//		this.onInteract(0);
	}
	
}
