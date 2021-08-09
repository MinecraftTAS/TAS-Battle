package work.mgnet.replika;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ReplicaRun {

	public static HashMap<Location, Material> demo = new HashMap<Location, Material>();
	public static HashMap<Player, HashMap<Location, Material>> playerBuild = new HashMap<Player, HashMap<Location, Material>>();
	public static long timeStart = 0L;
	
	public static int checkPoints(Player e) {
		int points = 0;
		
		try {
			for (Entry<Location, Material> map : demo.entrySet()) {
				try {
					if (playerBuild.get(e).get(map.getKey()).equals(map.getValue())) {
						points++;
					}
				} catch (Exception e3) {
					
				}
			}
		} catch (Exception e2) {
			
		}
		return points;
	}
	
	public static void loadBuild(int i) {
		demo.clear();
		timeStart = System.currentTimeMillis();
		int corner = i * 9;
		System.out.println(i);
		Location cornerLoc = ReplicaConfiguration.copyLocation.clone().add(corner, 0, 0);
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				Location newLoc = cornerLoc.clone().add(x, y, 0);
				Location newPasteLoc = ReplicaConfiguration.demoLocation.clone().add(x, y, 0);
				newPasteLoc.getBlock().setType(newLoc.getBlock().getType());
				demo.put(new Location(Bukkit.getWorlds().get(0), -8, 100, 1000).add(x, 1, y), newLoc.getBlock().getType());
				
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.getInventory().addItem(new ItemStack(newLoc.getBlock().getType()));
				}
				
			}
		}
	}
	
}
