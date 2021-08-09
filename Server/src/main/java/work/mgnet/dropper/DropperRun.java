package work.mgnet.dropper;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;

public class DropperRun {

	public static void loadDropper() {
		for (int i = 0; i < 14; i++) {
			Location currentPasteLoc = DropperConfiguration.pasteLoc.clone().add(0, i*4*4, 0);
			Location currentCopyLoc = DropperConfiguration.copyLoc.clone().add(-16 * new Random().nextInt(DropperConfiguration.dropperCount), 0, 0);
			Material mat = DropperConfiguration.replacements.get(new Random().nextInt(DropperConfiguration.replacements.size()));
			for (int x = 0; x < 15; x++) {
				for (int y = 0; y < 4; y++) {
					for (int z = 0; z < 15; z++) {
						Location copyHere = currentCopyLoc.clone().add(x, y, z);
						Location pasteHere = currentPasteLoc.clone().add(x, y, z);
						if (copyHere.getBlock().getType() == Material.AIR) pasteHere.getBlock().setType(Material.AIR);
						else pasteHere.getBlock().setType(mat);
					}
				}
			}
		}
	}
	
}
