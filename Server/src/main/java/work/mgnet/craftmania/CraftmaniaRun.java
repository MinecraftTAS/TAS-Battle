package work.mgnet.craftmania;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import work.mgnet.utils.Prefix;

public class CraftmaniaRun {
	
	public static Material recipe;
	
	public static void setCrafting() {
		recipe = CraftmaniaConfiguration.recipe.get(new Random().nextInt(CraftmaniaConfiguration.recipe.size()));
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.getInventory().clear();
			p.openWorkbench(null, true);
			p.sendMessage(Prefix.CRAFTMANIA + "Craft " + recipe.toString().replaceAll("_", " ").toUpperCase());
			p.getInventory().addItem(CraftmaniaConfiguration.items.get(recipe));
			p.getInventory().setItem(35, new ItemStack(recipe));
			p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.0f, 1.0f);
		}
	}
	
}
