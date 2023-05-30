package com.minecrafttas.tasbattle.bedwars.components.shop;

import java.util.Arrays;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.minecrafttas.tasbattle.bedwars.components.InventoryManagement;
import com.minecrafttas.tasbattle.bedwars.components.shop.stacks.CustomItemStack;
import com.minecrafttas.tasbattle.bedwars.components.shop.stacks.EnchantedItemStack;
import com.minecrafttas.tasbattle.bedwars.components.shop.stacks.NamedItemStack;
import com.minecrafttas.tasbattle.bedwars.components.shop.stacks.PotionItemStack;
import com.minecrafttas.tasbattle.bedwars.components.shop.stacks.PurchasableItemStack;
import com.minecrafttas.tasbattle.bedwars.components.shop.stacks.PurchasableItemStack.Price;
import com.minecrafttas.tasbattle.gui.PagedInventory;

import net.kyori.adventure.text.Component;

/**
 * Bedwards item shop
 */
public class ItemShop extends PagedInventory {

	private InventoryManagement invMng;
	
	/**
	 * Initialize item shop
	 */
	public ItemShop(InventoryManagement invMng) {
		super(Component.text("§8Item Shop"), 54);
		this.invMng = invMng;
		
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
//		this.setPage(0, new Page(item, items, Arrays.asList(items).stream().map(i -> i == null ? null : i.purchase()).toArray(Interaction[]::new)));
		
		// blocks page
		var item = new NamedItemStack(Material.TERRACOTTA, 1, "Blocks");
		var items = new PurchasableItemStack[] {
			null,
			new PurchasableItemStack(Price.IRON, 4, Material.WHITE_WOOL, 16, "Wool"),
			new PurchasableItemStack(Price.IRON, 8, Material.TERRACOTTA, 16, "Terracotta"),
			new PurchasableItemStack(Price.IRON, 12, Material.GLASS, 4, "Glass"),
			new PurchasableItemStack(Price.IRON, 24, Material.END_STONE, 12, "End Stone"),
			new PurchasableItemStack(Price.IRON, 3, Material.LADDER, 8, "Ladder"),
			new PurchasableItemStack(Price.GOLD, 4, Material.OAK_PLANKS, 16, "Wood"),
			new PurchasableItemStack(Price.EMERALD, 4, Material.OBSIDIAN, 4, "Obsidian"),
		};
		this.setPage(1, new Page(item, items, Arrays.asList(items).stream().map(i -> i == null ? null : i.purchase()).toArray(Interaction[]::new)));
		
		// weapons page
		item = new NamedItemStack(Material.GOLDEN_SWORD, 1, "Weapons");
		items = new PurchasableItemStack[] {
			null,
			new PurchasableItemStack(Price.IRON, 10, Material.STONE_SWORD, 1, "Stone Sword"),
			new PurchasableItemStack(Price.GOLD, 6, Material.IRON_SWORD, 1, "Iron Sword"),
			new PurchasableItemStack(Price.EMERALD, 4, Material.DIAMOND_SWORD, 1, "Diamond Sword"),
			new EnchantedItemStack(Price.GOLD, 5, Material.STICK, new Enchantment[] { Enchantment.KNOCKBACK }, new int[] { 2 }, "Knockback Stick"),
		};
		this.setPage(2, new Page(item, items, Arrays.asList(items).stream().map(i -> i == null ? null : i.purchase()).toArray(Interaction[]::new)));
		
		// armor page
		item = new NamedItemStack(Material.CHAINMAIL_BOOTS, 1, "Armor");
		items = new PurchasableItemStack[] {
			null,
			new CustomItemStack(Price.IRON, 24, Material.CHAINMAIL_BOOTS, 1, "Chainmail Armor", p -> {
				if (this.invMng.getArmorTier(p) < 0) {
					this.invMng.setArmorTier(p, 0);
					return true;
				}
				return false;
			}),
			new CustomItemStack(Price.GOLD, 12, Material.IRON_BOOTS, 1, "Iron Armor", p -> {
				if (this.invMng.getArmorTier(p) < 1) {
					this.invMng.setArmorTier(p, 1);
					return true;
				}
				return false;
			}),
			new CustomItemStack(Price.EMERALD, 6, Material.DIAMOND_BOOTS, 1, "Diamond Armor", p -> {
				if (this.invMng.getArmorTier(p) < 2) {
					this.invMng.setArmorTier(p, 2);
					return true;
				}
				return false;
			}),
		};
		this.setPage(3, new Page(item, items, Arrays.asList(items).stream().map(i -> i == null ? null : i.purchase()).toArray(Interaction[]::new)));
		
		// tools page
		item = new NamedItemStack(Material.STONE_PICKAXE, 1, "Tools");
		items = new PurchasableItemStack[] {
			null,
//			new CustomItemStack(Price.IRON, 4, Material.WHITE_WOOL, 16, "Wool"),
//			new CustomItemStack(Price.IRON, 8, Material.TERRACOTTA, 16, "Terracotta"),
//			new CustomItemStack(Price.IRON, 12, Material.GLASS, 4, "Glass"),
//			new CustomItemStack(Price.EMERALD, 4, Material.OBSIDIAN, 4, "Obsidian"),
		};
		this.setPage(4, new Page(item, items, Arrays.asList(items).stream().map(i -> i == null ? null : i.purchase()).toArray(Interaction[]::new)));
		
		// bows page
		item = new NamedItemStack(Material.BOW, 1, "Bows");
		items = new PurchasableItemStack[] {
			null,
			new PurchasableItemStack(Price.GOLD, 2, Material.ARROW, 8, "Arrow"),
			new PurchasableItemStack(Price.GOLD, 12, Material.BOW, 1, "Bow"),
			new EnchantedItemStack(Price.GOLD, 24, Material.BOW, new Enchantment[] { Enchantment.ARROW_DAMAGE }, new int[] { 1 }, "Bow II"),
			new EnchantedItemStack(Price.EMERALD, 6, Material.BOW, new Enchantment[] { Enchantment.ARROW_DAMAGE, Enchantment.ARROW_KNOCKBACK }, new int[] { 1, 1 }, "Bow III"),
		};
		this.setPage(5, new Page(item, items, Arrays.asList(items).stream().map(i -> i == null ? null : i.purchase()).toArray(Interaction[]::new)));
		
		// potions page
		item = new NamedItemStack(Material.BREWING_STAND, 1, "Potions");
		items = new PurchasableItemStack[] {
			null,
			new PotionItemStack(Price.EMERALD, 1, new PotionEffect(PotionEffectType.SPEED, 45*20, 4), Color.fromRGB(0xFFBF00), "Speed V Potion"),
			new PotionItemStack(Price.EMERALD, 1, new PotionEffect(PotionEffectType.JUMP, 45*20, 4), Color.fromRGB(0xE6E6FA), "Jump Boost V Potion"),
			new PotionItemStack(Price.EMERALD, 2, new PotionEffect(PotionEffectType.INVISIBILITY, 30*20, 0), Color.fromRGB(0x9c9d97), "Invisibility Potion")
		};
		this.setPage(6, new Page(item, items, Arrays.asList(items).stream().map(i -> i == null ? null : i.purchase()).toArray(Interaction[]::new)));
		
		// secondaries page
		item = new NamedItemStack(Material.TNT, 1, "Utilities");
		items = new PurchasableItemStack[] {
			null,
			new PurchasableItemStack(Price.GOLD, 3, Material.GOLDEN_APPLE, 1, "Golden Apple"),
			new PurchasableItemStack(Price.IRON, 40, Material.FIRE_CHARGE, 1, "Fireball"),
			new PurchasableItemStack(Price.GOLD, 4, Material.TNT, 1, "TNT"),
			new PurchasableItemStack(Price.EMERALD, 4, Material.ENDER_PEARL, 1, "Obsidian"),
			new PurchasableItemStack(Price.GOLD, 3, Material.WATER_BUCKET, 1, "Obsidian"),
		};
		this.setPage(7, new Page(item, items, Arrays.asList(items).stream().map(i -> i == null ? null : i.purchase()).toArray(Interaction[]::new)));
		
		this.onInteract(null, 1);
//		this.onInteract(0);
	}
	
}
