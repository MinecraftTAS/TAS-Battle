package work.mgnet.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtils {

	public static void diamondArmor(Player p) {
		p.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		p.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
		p.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		p.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
	}
	
}
