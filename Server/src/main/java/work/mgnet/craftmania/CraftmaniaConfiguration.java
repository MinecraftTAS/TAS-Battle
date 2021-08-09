package work.mgnet.craftmania;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CraftmaniaConfiguration {
	
	public static List<Material> recipe = Arrays.asList(Material.OAK_DOOR, 
			Material.OAK_BOAT, 
			Material.ACACIA_BUTTON, 
			Material.STONE_PRESSURE_PLATE, 
			Material.BARREL, 
			Material.ARMOR_STAND, 
			Material.CHEST, 
			Material.PISTON, 
			Material.SLIME_BLOCK, 
			Material.RESPAWN_ANCHOR, 
			Material.ACACIA_SIGN, 
			Material.BLAZE_POWDER, 
			Material.ENDER_EYE, 
			Material.LODESTONE, 
			Material.ACACIA_PRESSURE_PLATE, 
			Material.ANVIL, 
			Material.BOW, 
			Material.BREWING_STAND, 
			Material.RED_STAINED_GLASS, 
			Material.POLISHED_ANDESITE, 
			Material.BLACK_DYE, 
			Material.WHITE_WOOL);
	
	public static HashMap<Material, ItemStack[]> items = new HashMap<>();
	public static Location pLoc = new Location(Bukkit.getWorlds().get(0), 4000, 96, 0);
	
	static {
		items.put(recipe.get(0), new ItemStack[] {new ItemStack(Material.OAK_PLANKS, 6)});
		items.put(recipe.get(1), new ItemStack[] {new ItemStack(Material.OAK_PLANKS, 5)});
		items.put(recipe.get(2), new ItemStack[] {new ItemStack(Material.ACACIA_PLANKS, 1)});
		items.put(recipe.get(3), new ItemStack[] {new ItemStack(Material.STONE, 2)});
		items.put(recipe.get(4), new ItemStack[] {new ItemStack(Material.OAK_PLANKS, 6), new ItemStack(Material.OAK_SLAB, 2)});
		items.put(recipe.get(5), new ItemStack[] {new ItemStack(Material.STICK, 6), new ItemStack(Material.SMOOTH_STONE_SLAB, 1)});
		items.put(recipe.get(6), new ItemStack[] {new ItemStack(Material.OAK_PLANKS, 8)});
		items.put(recipe.get(7), new ItemStack[] {new ItemStack(Material.IRON_INGOT, 1), new ItemStack(Material.REDSTONE, 1), new ItemStack(Material.OAK_PLANKS, 3),new ItemStack(Material.COBBLESTONE, 4)});
		items.put(recipe.get(8), new ItemStack[] {new ItemStack(Material.SLIME_BALL, 9)});
		items.put(recipe.get(9), new ItemStack[] {new ItemStack(Material.GLOWSTONE, 3), new ItemStack(Material.CRYING_OBSIDIAN, 6)});
		items.put(recipe.get(10), new ItemStack[] {new ItemStack(Material.ACACIA_PLANKS, 6), new ItemStack(Material.STICK, 1)});
		items.put(recipe.get(11), new ItemStack[] {new ItemStack(Material.BLAZE_ROD, 1)});
		items.put(recipe.get(12), new ItemStack[] {new ItemStack(Material.BLAZE_POWDER, 1), new ItemStack(Material.ENDER_PEARL, 1)});
		items.put(recipe.get(13), new ItemStack[] {new ItemStack(Material.CHISELED_STONE_BRICKS, 8), new ItemStack(Material.NETHERITE_INGOT, 1)});
		items.put(recipe.get(14), new ItemStack[] {new ItemStack(Material.ACACIA_PLANKS, 2)});
		items.put(recipe.get(15), new ItemStack[] {new ItemStack(Material.IRON_BLOCK, 3), new ItemStack(Material.IRON_INGOT, 4)});
		items.put(recipe.get(16), new ItemStack[] {new ItemStack(Material.STICK, 3), new ItemStack(Material.STRING, 3)});
		items.put(recipe.get(17), new ItemStack[] {new ItemStack(Material.BLAZE_ROD, 1), new ItemStack(Material.COBBLESTONE, 3)});
		items.put(recipe.get(18), new ItemStack[] {new ItemStack(Material.GLASS, 8), new ItemStack(Material.RED_DYE, 1)});
		items.put(recipe.get(19), new ItemStack[] {new ItemStack(Material.ANDESITE, 4)});
		items.put(recipe.get(20), new ItemStack[] {new ItemStack(Material.INK_SAC, 1)});
		items.put(recipe.get(21), new ItemStack[] {new ItemStack(Material.STRING, 4)});
	}
	
}
