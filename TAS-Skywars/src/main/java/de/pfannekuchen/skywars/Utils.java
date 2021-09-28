package de.pfannekuchen.skywars;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class Utils {

	public static ItemStack enchant(ItemStack item, Enchantment e, int level) {
		item.addUnsafeEnchantment(e, level);
		return item;
	}
	
}
