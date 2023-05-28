package com.minecrafttas.tasbattle.bedwars.components.shop.stacks;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

/**
 * Purchasable item stack with enchantments in shop inventory
 */
public class EnchantedItemStack extends PurchasableItemStack {
	
	/**
	 * Initialize purchasable item stack with enchantments
	 * @param price Price type
	 * @param amount Price amount
	 * @param type Item type
	 * @param enchantments Enchantments
	 * @param name Item name
	 */
	public EnchantedItemStack(Price price, int amount, Material type, Enchantment[] enchantments, int[] levels, String name) {
		super(price, amount, type, 1, name);
		for (int i = 0; i < enchantments.length; i++)
			this.addUnsafeEnchantment(enchantments[i], levels[i]);
	}
	
}