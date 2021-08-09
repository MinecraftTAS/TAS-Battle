package work.mgnet.spleef;

import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;

public class SpleefRun {

	public static void replaceBlocks() {
		for (Entry<Location, Material> location : SpleefListener.snow.entrySet()) {
			location.getKey().getBlock().setType(location.getValue());
		}
		SpleefListener.snow.clear();
	}
	
}
