package work.mgnet.blockrun;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BlockrunConfiguration {

	public static final List<String> deathMessages = Arrays.asList("% was never seen again.", "% fell to their death.", "% was taken hostage by the void.", "% was sent to the underworld.", "% didn't find the floor.");
	public static final Location[] mapLocation = new Location[] {
			new Location(Bukkit.getWorlds().get(0), 1000, 102, 1000),
			new Location(Bukkit.getWorlds().get(0), 1500, 101, 1000),
			new Location(Bukkit.getWorlds().get(0), 2500, 101, 1000)};
	
}
