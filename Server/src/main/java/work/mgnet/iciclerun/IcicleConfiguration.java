package work.mgnet.iciclerun;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class IcicleConfiguration {

	public static final Material fallingBlock = Material.PACKED_ICE;
	public static final List<String> cubeDeathMessages = Arrays.asList("% didn't quite reach 42069 points.", "% didn't like being chased by ice.", "% sucks at Minecraft.", "% wasn't iced out at all.", "% left the fridge open.");
	public static final Location[] teleportLocation = new Location[] {
			new Location(Bukkit.getWorlds().get(0), 1000, 80, 0),
			new Location(Bukkit.getWorlds().get(0), 1000, 57, 0),
			new Location(Bukkit.getWorlds().get(0), 998, 14, -1)};
	
}
