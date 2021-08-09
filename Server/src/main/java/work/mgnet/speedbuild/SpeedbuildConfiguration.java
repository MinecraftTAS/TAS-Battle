package work.mgnet.speedbuild;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpeedbuildConfiguration {

	public static Location[] locs = new Location[] {
		new Location(Bukkit.getWorlds().get(0), 4979, 103, -33),
		new Location(Bukkit.getWorlds().get(0), 4997, 103, -38),
		new Location(Bukkit.getWorlds().get(0), 5015, 103, -33),
		new Location(Bukkit.getWorlds().get(0), 5027, 103, -21),
		new Location(Bukkit.getWorlds().get(0), 5032, 103, -3),
		new Location(Bukkit.getWorlds().get(0), 5027, 103, 15),
		new Location(Bukkit.getWorlds().get(0), 5015, 103, 27),
		new Location(Bukkit.getWorlds().get(0), 4997, 103, 32),
		new Location(Bukkit.getWorlds().get(0), 4979, 103, 27),
		new Location(Bukkit.getWorlds().get(0), 4967, 103, 15),
		new Location(Bukkit.getWorlds().get(0), 4962, 103, -3),
		new Location(Bukkit.getWorlds().get(0), 4967, 103, -21)
	};
	
	public static Location copyLocation = new Location(Bukkit.getWorlds().get(0), -3, 100, 4997);
	
	public static HashMap<Player, Location> pos = new HashMap<>();
	
}
