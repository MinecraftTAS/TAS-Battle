package de.pfannekuchen.skywars.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Utils for Items such as quick enchanting, etc.
 * @author Pancake
 */
public class ItemUtils {

	public static ItemStack enchantItem(ItemStack itemStack, Enchantment ench, int level) {
		itemStack.addUnsafeEnchantment(ench, level);
		return itemStack;
	}
	
}
