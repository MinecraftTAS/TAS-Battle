package work.mgnet.dropper;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class DropperConfiguration {
	
	public static final Location copyLoc = new Location(Bukkit.getWorlds().get(0), 19, 102, 3001);
	public static final Location pasteLoc = new Location(Bukkit.getWorlds().get(0), 86, 4, 3000);
	
	public static final Location playLoc = new Location(Bukkit.getWorlds().get(0), 94, 246, 3003);
	public static final int dropperCount = 20;
	
	public static List<Material> replacements = Arrays.asList(Material.RED_CONCRETE, Material.ORANGE_CONCRETE, Material.YELLOW_CONCRETE, Material.GREEN_CONCRETE, Material.LIME_CONCRETE, Material.LIGHT_BLUE_CONCRETE, Material.BLUE_CONCRETE, Material.PURPLE_CONCRETE);
	
}
