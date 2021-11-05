package de.pfannekuchen.skywars.utils;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LootUtils {

	/**
	 * List of Items with MID_LOOT. Items with 63 count will get a random count.
	 */
	public static final HashMap<Integer, ItemStack> MID_LOOT = new HashMap<>();
	
	static {
			MID_LOOT.put(40, new ItemStack(Material.ARROW, 32));
			MID_LOOT.put(30, new ItemStack(Material.OAK_PLANKS, 64));
			MID_LOOT.put(30, new ItemStack(Material.SNOWBALL, 63));
			MID_LOOT.put(25, new ItemStack(Material.DIAMOND_PICKAXE));
			MID_LOOT.put(25, new ItemStack(Material.TNT, 63));
			MID_LOOT.put(25, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
			MID_LOOT.put(25, new ItemStack(Material.FLINT_AND_STEEL));
			MID_LOOT.put(25, new ItemStack(Material.LAVA_BUCKET));
			MID_LOOT.put(20, new ItemStack(Material.DIAMOND_AXE));
			MID_LOOT.put(15, ItemUtils.enchantItem(new ItemStack(Material.BOW), Enchantment.ARROW_DAMAGE, 3));
			MID_LOOT.put(15, ItemUtils.enchantItem(new ItemStack(Material.DIAMOND_HELMET), Enchantment.PROTECTION_ENVIRONMENTAL, 4));
			MID_LOOT.put(15, ItemUtils.enchantItem(new ItemStack(Material.DIAMOND_CHESTPLATE), Enchantment.PROTECTION_ENVIRONMENTAL, 4));
			MID_LOOT.put(15, ItemUtils.enchantItem(new ItemStack(Material.DIAMOND_LEGGINGS), Enchantment.PROTECTION_PROJECTILE, 3));
			MID_LOOT.put(15, ItemUtils.enchantItem(new ItemStack(Material.DIAMOND_BOOTS), Enchantment.PROTECTION_FIRE, 2));
			MID_LOOT.put(10, new ItemStack(Material.ENDER_PEARL));
			MID_LOOT.put(10, new ItemStack(Material.GOLDEN_APPLE));
			MID_LOOT.put(10, ItemUtils.enchantItem(new ItemStack(Material.FISHING_ROD), Enchantment.KNOCKBACK, 3));
	};
	
	/**
	 * Fills a chest with default loot
	 * @param chestLocation Location of the Chest
	 */
	public static void fillChestWithSpawnLoot(Location chestLocation) {
		
	}
	
	/**
	 * Chest Randomness
	 */
	private static final Random rng = new Random();
	
	/**
	 * Fills a chest with stronger loot
	 * @param chestLocation Location of the Chest
	 */
	public static void fillChestWithMidLoot(Location chestLocation) {
		int itemCount = rng.nextInt(4) + 3;
		Chest chest = (Chest) chestLocation.getBlock();
		Inventory inv = chest.getBlockInventory();
		while (itemCount > 0) {
			for (Entry<Integer, ItemStack> entry : MID_LOOT.entrySet()) {
				if (rng.nextInt(100) < entry.getKey()) {
					itemCount--;
					inv.addItem(entry.getValue());
				}
			}
		}
		chest.update();
	}
	
}
